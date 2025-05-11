package com.katros.autolinkbn.entities;

import com.katros.autolinkbn.enums.RequestStatus;
import com.katros.autolinkbn.enums.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "role_change_requests")
public class RoleChangeRequest {

    @Id
    private String id;

    @DBRef
    private User user;

    private Role requestedRole;

    private RequestStatus status = RequestStatus.PENDING;
}
