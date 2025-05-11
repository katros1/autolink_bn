package com.katros.autolinkbn.controllers;

import com.katros.autolinkbn.dtos.PaginatedResponse;
import com.katros.autolinkbn.entities.User;
import com.katros.autolinkbn.enums.Role;
import com.katros.autolinkbn.exceptions.NotFoundException;
import com.katros.autolinkbn.services.UserService;
import com.katros.autolinkbn.utils.CustomResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<CustomResponse<PaginatedResponse<User>>> getAllUsers(
            @RequestParam Optional<Role> role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> userPage = userService.getAllUsers(role, pageable);

        PaginatedResponse<User> response = new PaginatedResponse<>();
        response.setContent(userPage.getContent());
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(userPage.getTotalElements());
        response.setTotalPages(userPage.getTotalPages());

        return ResponseEntity.ok(CustomResponse.successResponse("Users fetched successfully", HttpStatus.OK.value(), response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<User>> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return ResponseEntity.ok(CustomResponse.successResponse("User fetched successfully", HttpStatus.OK.value(), user));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CustomResponse<User>> deactivateUser(@PathVariable String id) {
        User updatedUser = userService.deactivateUser(id);
        return ResponseEntity.ok(CustomResponse.successResponse("User deactivated successfully", HttpStatus.OK.value(), updatedUser));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<CustomResponse<User>> activateUser(@PathVariable String id) {
        User updatedUser = userService.activateUser(id);
        return ResponseEntity.ok(CustomResponse.successResponse("User activated successfully", HttpStatus.OK.value(), updatedUser));
    }
}

