package com.katros.autolinkbn.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpVerificationDTO {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String otp;
}
