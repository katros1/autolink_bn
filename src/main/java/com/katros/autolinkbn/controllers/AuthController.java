package com.katros.autolinkbn.controllers;

import com.katros.autolinkbn.dtos.*;
import com.katros.autolinkbn.entities.User;
import com.katros.autolinkbn.enums.Role;
import com.katros.autolinkbn.services.UserLoginService;
import com.katros.autolinkbn.services.UserService;
import com.katros.autolinkbn.utils.CustomResponse;
import com.katros.autolinkbn.utils.JwtHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserLoginService userLoginService;
    private final UserService userService;
    private final JwtHandler jwtHandler;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(UserLoginService userLoginService, UserService userService, JwtHandler jwtHandler, AuthenticationManager authenticationManager) {
        this.userLoginService = userLoginService;
        this.userService = userService;
        this.jwtHandler = jwtHandler;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<CustomResponse<UserLoginResponseDTO>> login(@Valid @RequestBody UserLoginDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User account = userLoginService.getActiveUser(request.getEmail());

            Set<Role> roleSet = account.getRoles();

            List<String> roles = roleSet.stream()
                    .map(Enum::name)
                    .toList();

            String token = jwtHandler.generateToken(
                    account.getUserId(), request.getEmail(), roles, account.isVerified()
            );

            UserLoginResponseDTO authResponse = UserLoginResponseDTO.builder()
                    .userId(account.getUserId())
                    .firstName(account.getFirstName())
                    .lastName(account.getLastName())
                    .email(account.getEmail())
                    .role(roles)
                    .profilePicture(account.getProfilePicUrl())
                    .isVerified(account.isVerified())
                    .accountStatus(account.getAccountStatus())
                    .token(token)
                    .build();

            return ResponseEntity.ok(CustomResponse.successResponse("Login successful! Welcome back, " + authResponse.getLastName(), HttpStatus.OK.value(), authResponse));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(CustomResponse.errorResponse(e.getMessage(), HttpStatus.FORBIDDEN.value()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.errorResponse("Invalid credentials", HttpStatus.UNAUTHORIZED.value()));
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<CustomResponse<String>> validateToken(HttpServletRequest request) {

        String token = jwtHandler.extractToken(request);

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomResponse.errorResponse("No token provided", HttpStatus.BAD_REQUEST.value()));
        }

        boolean isValid = jwtHandler.validateToken(token);

        if (isValid) {
            return ResponseEntity.ok(CustomResponse.successResponse("Token is valid", HttpStatus.OK.value()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomResponse.errorResponse("Token is invalid or expired", HttpStatus.UNAUTHORIZED.value()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<CustomResponse<User>> registerUser(@Valid @RequestBody UserDTO userDTO) {
        User registeredUser = userService.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.successResponse("User registered successfully", HttpStatus.CREATED.value(), registeredUser));
    }

    @PostMapping("/verify")
    public ResponseEntity<CustomResponse<String>> verifyOtp(@RequestBody OtpVerificationDTO dto) {
        String message = userService.verifyOtp(dto.getEmail(), dto.getOtp());
        return ResponseEntity.ok(CustomResponse.successResponse(message, HttpStatus.OK.value()));
    }
}
