package com.katros.autolinkbn.dtos;

import com.katros.autolinkbn.enums.RequestStatus;
import com.katros.autolinkbn.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleChangeRequestDTO {
    private String requestId;
    private String names;
    private String userEmail;
    private String profilePic;
    private Role requestedRole;
    private RequestStatus status;
}
