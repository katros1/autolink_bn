package com.katros.autolinkbn.services;

import com.katros.autolinkbn.entities.User;
import com.katros.autolinkbn.repositories.UserRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmailValidationService {

    public void validateEmailFormat(String email) {
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
    }

    public void checkEmailAlreadyExists(String email, UserRepository userRepository) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email already in use.");
        }
    }

    public User getUserByEmail(String email, UserRepository userRepository) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
