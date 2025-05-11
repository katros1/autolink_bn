package com.katros.autolinkbn.dtos;

import com.katros.autolinkbn.customvalidations.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePasswordDTO {

    @Schema(description = "Current password", example = "OldPassword123!")
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @Schema(description = "New password", example = "NewPassword456!")
    @ValidPassword
    private String newPassword;
}
