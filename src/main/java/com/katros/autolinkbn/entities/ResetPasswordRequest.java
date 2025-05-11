package com.katros.autolinkbn.entities;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "reset_password_requests")
public class ResetPasswordRequest {

    @Id
    private String id;

    private String email;

    private String otp;

    private boolean isUsed = false;

    @CreatedDate
    private LocalDateTime createdAt;

}
