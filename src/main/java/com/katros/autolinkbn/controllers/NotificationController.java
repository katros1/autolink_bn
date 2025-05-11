package com.katros.autolinkbn.controllers;

import com.katros.autolinkbn.dtos.DeviceTokenRequestDTO;
import com.katros.autolinkbn.services.NotificationService;
import com.katros.autolinkbn.utils.CustomResponse;
import com.katros.autolinkbn.utils.JwtHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtHandler jwtHandler;
    private final HttpServletRequest httpServletRequest;

    public NotificationController(NotificationService notificationService, JwtHandler jwtHandler, HttpServletRequest httpServletRequest) {
        this.notificationService = notificationService;
        this.jwtHandler = jwtHandler;
        this.httpServletRequest = httpServletRequest;
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<CustomResponse<String>> registerDeviceToken(
            @Valid @RequestBody DeviceTokenRequestDTO request) {
        String userId = jwtHandler.extractUserId(httpServletRequest.getHeader("Authorization").split(" ")[1]);
        notificationService.registerDeviceToken(userId, request.getToken());
        return ResponseEntity.ok(CustomResponse.successResponse("Device token registered", HttpStatus.OK.value()));
    }

    @PostMapping("/test/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CustomResponse<String>> sendTestNotification(@PathVariable String userId) {
        notificationService.sendNotificationToUser(userId, "Test Notification", "This is a test push message.");
        return ResponseEntity.ok(CustomResponse.successResponse("Notification sent", HttpStatus.OK.value()));
    }
}

