package com.katros.autolinkbn.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "email_otps")
public class EmailOTP {

    @Id
    private String id;

    private String email;
    private String otp;

    private LocalDateTime createdAt = LocalDateTime.now();

    public boolean isExpired() {
        return createdAt.plusDays(1).isBefore(LocalDateTime.now());
    }
}
