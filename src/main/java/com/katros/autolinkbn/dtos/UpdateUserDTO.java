package com.katros.autolinkbn.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class UpdateUserDTO {

    @Schema(description = "First name of the teacher", example = "John")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(description = "Last name of the teacher", example = "Doe")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(description = "Nationality of the teacher", example = "Rwandan")
    @NotBlank(message = "Nationality is required")
    private String nationality;

    @Schema(description = "Phone number of the teacher", example = "+250788123456")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}

