package com.katros.autolinkbn.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceTokenRequestDTO {
    @NotBlank
    private String token;
}
