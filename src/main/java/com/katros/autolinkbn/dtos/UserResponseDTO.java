package com.katros.autolinkbn.dtos;

import com.katros.autolinkbn.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private String userId;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String email;
    private String country;
    private String phoneNumber;
    private String profilePicUrl;
}
