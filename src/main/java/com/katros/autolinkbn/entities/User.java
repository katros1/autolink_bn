package com.katros.autolinkbn.entities;

import com.katros.autolinkbn.enums.Gender;
import com.katros.autolinkbn.enums.Role;
import com.katros.autolinkbn.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String userId;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String email;
    private Set<Role> roles;
    private String country;
    private String phoneNumber;
    private LocalDate dob;
    private String profilePicUrl;
    private String password;

    private boolean isVerified;
    private Status accountStatus;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
