package com.katros.autolinkbn.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.katros.autolinkbn.enums.Gender;
import com.katros.autolinkbn.enums.Role;
import com.katros.autolinkbn.enums.Status;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDTO {

    private String userId;

    private String firstName;

    private String lastName;

    private Gender gender;

    private String email;

    private Role role;

    private String country;

    private String phoneNumber;

    private LocalDate dob;

    private String profilePicUrl;

    private boolean isVerified;

    private Status accountStatus;

    private LocalDateTime createdAt;
}
