package com.katros.autolinkbn.dtos;

import com.katros.autolinkbn.customvalidations.ValidPassword;
import com.katros.autolinkbn.enums.Gender;
import com.katros.autolinkbn.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserDTO {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9\\-\\+]{9,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotNull(message = "Gender is required")
    private Gender gender;

//    @NotNull(message = "Role is required")
    private Role role;

    @NotBlank(message = "Country is required")
    private String country;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    @NotBlank(message = "New password is required")
    @ValidPassword
    private String password;
}
