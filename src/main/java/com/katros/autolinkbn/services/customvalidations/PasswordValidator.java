package com.katros.autolinkbn.services.customvalidations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Getter
    private static class ValidationRule {
        private final Predicate<String> predicate;
        private final String errorMessage;

        public ValidationRule(Predicate<String> predicate, String errorMessage) {
            this.predicate = predicate;
            this.errorMessage = errorMessage;
        }

        public boolean isValid(String value) {
            return predicate.test(value);
        }
    }

    private final List<ValidationRule> rules = new ArrayList<>();

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        rules.add(new ValidationRule(
                password -> password != null && !password.isEmpty(),
                "Password cannot be empty."
        ));
        rules.add(new ValidationRule(
                password -> password != null && password.length() >= 8,
                "Password must be at least 8 characters long."
        ));
        rules.add(new ValidationRule(
                password -> password != null && password.matches(".*[A-Z].*"),
                "Password must contain at least one uppercase letter."
        ));
        rules.add(new ValidationRule(
                password -> password != null && password.matches(".*[a-z].*"),
                "Password must contain at least one lowercase letter."
        ));
        rules.add(new ValidationRule(
                password -> password != null && password.matches(".*\\d.*"),
                "Password must contain at least one digit."
        ));
        rules.add(new ValidationRule(
                password -> password != null && password.matches(".*[@#$%^&+=!].*"),
                "Password must contain at least one special character."
        ));
        rules.add(new ValidationRule(
                password -> password != null && !password.matches(".*\\s.*"),
                "Password cannot contain whitespace."
        ));
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        for (ValidationRule rule : rules) {
            if (!rule.isValid(password)) {
                context.buildConstraintViolationWithTemplate(rule.getErrorMessage())
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
