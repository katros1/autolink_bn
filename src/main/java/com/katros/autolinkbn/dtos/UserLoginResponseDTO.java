package com.katros.autolinkbn.dtos;

import com.katros.autolinkbn.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class UserLoginResponseDTO {

    private String userId;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private List<String> role;

    private String profilePicture;

    private boolean isVerified;

    private Status accountStatus;

    private String token;

}
