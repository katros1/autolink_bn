package com.katros.autolinkbn.services;

import com.katros.autolinkbn.entities.User;
import com.katros.autolinkbn.exceptions.ForbiddenException;
import com.katros.autolinkbn.exceptions.NotFoundException;
import com.katros.autolinkbn.exceptions.UnauthorizedException;
import com.katros.autolinkbn.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserLoginService implements UserDetailsService {
    private final UserRepository userRepository;
    private final EmailValidationService emailValidationService;

    public UserLoginService(UserRepository userRepository, EmailValidationService emailValidationService) {
        this.userRepository = userRepository;
        this.emailValidationService = emailValidationService;
    }

    public User getActiveUser(String email) {
        User user = emailValidationService.getUserByEmail(email, userRepository);

        if (user == null) {
            throw new NotFoundException("Invalid credentials");
        }

        if (!Objects.equals(user.getAccountStatus().name(), "ACTIVE")) {
            throw new ForbiddenException("Account not active");
        }

        if (!user.isVerified()) {
            throw new UnauthorizedException("Account is not verified");
        }

        return user;

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = emailValidationService.getUserByEmail(email, userRepository);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.toString()))
                        .collect(Collectors.toSet())
        );
    }
}
