package com.katros.autolinkbn.controllers;

import com.katros.autolinkbn.dtos.RoleChangeApprovalDTO;
import com.katros.autolinkbn.dtos.RoleChangeRequestDTO;
import com.katros.autolinkbn.enums.Role;
import com.katros.autolinkbn.services.UserService;
import com.katros.autolinkbn.utils.CustomResponse;
import com.katros.autolinkbn.utils.JwtHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleChangeRequestController {

    private final UserService roleChangeRequestService;
    private final JwtHandler jwtHandler;
    private final HttpServletRequest httpServletRequest;

    public RoleChangeRequestController(UserService roleChangeRequestService, JwtHandler jwtHandler, HttpServletRequest httpServletRequest) {
        this.roleChangeRequestService = roleChangeRequestService;
        this.jwtHandler = jwtHandler;
        this.httpServletRequest = httpServletRequest;
    }

    // User makes a role change request
    @PostMapping("/request")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<CustomResponse<String>> requestRoleChange(
            @RequestParam("requestedRole") Role requestedRole) {

        String userId = jwtHandler.extractUserId(httpServletRequest.getHeader("Authorization").split(" ")[1]);

        roleChangeRequestService.makeRoleChangeRequest(userId, requestedRole);

        return ResponseEntity.ok(CustomResponse.successResponse("Role change request submitted", HttpStatus.OK.value()));
    }

    // Admin approves or rejects a role change request
    @PutMapping("/approve/{requestId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CustomResponse<String>> approveRoleChange(
            @PathVariable String requestId,
            @RequestBody @NotNull RoleChangeApprovalDTO requestApprovalDTO) {

        roleChangeRequestService.approveRoleChangeRequest(requestId, requestApprovalDTO.isApprove());

        String message = requestApprovalDTO.isApprove() ? "Role request approved" : "Role request rejected";
        return ResponseEntity.ok(CustomResponse.successResponse(message, HttpStatus.OK.value()));
    }

    // Admin gets all pending role change requests
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CustomResponse<List<RoleChangeRequestDTO>>> getPendingRequests() {

        List<RoleChangeRequestDTO> pending = roleChangeRequestService.getRoleChangeRequest();

        return ResponseEntity.ok(CustomResponse.successResponse(
                "Pending requests fetched",
                HttpStatus.OK.value(),
                pending));
    }
}

