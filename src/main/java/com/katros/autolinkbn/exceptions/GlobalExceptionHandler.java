package com.katros.autolinkbn.exceptions;

import com.katros.autolinkbn.utils.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        CustomResponse<Map<String, String>> response = CustomResponse.errorResponse("Validation failed", HttpStatus.BAD_REQUEST.value());
        response.setData(errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse<String>> handleGeneralExceptions(Exception ex) {
        CustomResponse<String> response = CustomResponse.errorResponse("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CustomResponse<String>> handleIllegalStateException(IllegalStateException ex) {
        CustomResponse<String> response = CustomResponse.errorResponse("Illegal State: " + ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        CustomResponse<String> response = CustomResponse.errorResponse("Invalid argument: " + ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomResponse<String>> handleNotFoundException(NotFoundException ex) {
        CustomResponse<String> response = CustomResponse.errorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<CustomResponse<String>> handleConflictException(ConflictException ex) {
        CustomResponse<String> response = CustomResponse.errorResponse(ex.getMessage(), HttpStatus.CONFLICT.value());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<CustomResponse<String>> handleForbiddenException(ForbiddenException ex) {
        CustomResponse<String> response = CustomResponse.errorResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<CustomResponse<String>> handleUnauthorizedException(UnauthorizedException ex) {
        CustomResponse<String> response = CustomResponse.errorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<CustomResponse<String>> handleEmailSendingException(EmailSendingException ex) {

        CustomResponse<String> response = CustomResponse.errorResponse("Failed to send OTP email. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CustomResponse<String>> handleBadRequestException(BadRequestException ex) {
        CustomResponse<String> response = CustomResponse.errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomResponse<String>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(CustomResponse.errorResponse("Access Denied", HttpStatus.FORBIDDEN.value()));
    }
}
