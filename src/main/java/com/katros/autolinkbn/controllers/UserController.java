package com.katros.autolinkbn.controllers;

import com.katros.autolinkbn.dtos.UpdatePasswordDTO;
import com.katros.autolinkbn.dtos.UserProfileDTO;
import com.katros.autolinkbn.entities.User;
import com.katros.autolinkbn.services.UserService;
import com.katros.autolinkbn.utils.CustomResponse;
import com.katros.autolinkbn.utils.JwtHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final JwtHandler jwtHandler;
    private final HttpServletRequest httpServletRequest;

    public UserController(UserService userService, JwtHandler jwtHandler, HttpServletRequest httpServletRequest) {
        this.userService = userService;
        this.jwtHandler = jwtHandler;
        this.httpServletRequest = httpServletRequest;
    }

    @PutMapping("/update-password")
    public ResponseEntity<CustomResponse<String>> updatePassword(
            @Valid @RequestBody UpdatePasswordDTO updatePasswordDto) {

        String userId = jwtHandler.extractUserId(httpServletRequest.getHeader("Authorization").split(" ")[1]);

        userService.updatePassword(userId, updatePasswordDto);

        return ResponseEntity.ok(CustomResponse.successResponse(
                "Password updated successfully",
                HttpStatus.OK.value()));
    }

    @PutMapping("/update-profile-picture")
    public ResponseEntity<CustomResponse<String>> updateProfilePicture(
            @RequestParam("file") MultipartFile profilePicture) {

        String userId = jwtHandler.extractUserId(httpServletRequest.getHeader("Authorization").split(" ")[1]);

        userService.updateProfilePicture(userId, profilePicture);

        return ResponseEntity.ok(CustomResponse.successResponse(
                "Profile picture updated successfully",
                HttpStatus.OK.value()));
    }

    @GetMapping("/profile")
    public ResponseEntity<CustomResponse<UserProfileDTO>> getUserProfile() {

        String userId = jwtHandler.extractUserId(httpServletRequest.getHeader("Authorization").split(" ")[1]);

        UserProfileDTO userProfile = userService.getUserProfile(userId);
        return ResponseEntity.ok(CustomResponse.successResponse("User profile fetched", HttpStatus.OK.value(), userProfile));
    }

    @PutMapping("/profile")
    public ResponseEntity<CustomResponse<User>> updateUserProfile(
            @RequestBody User updatedUser,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization").split(" ")[1];
        String userId = jwtHandler.extractUserId(token);

        User updated = userService.updateUser(userId, updatedUser);
        return ResponseEntity.ok(CustomResponse.successResponse("Profile updated successfully", HttpStatus.OK.value(), updated));
    }
}
