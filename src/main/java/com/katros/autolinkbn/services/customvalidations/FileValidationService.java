package com.katros.autolinkbn.services.customvalidations;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class FileValidationService {
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of("image/png", "image/jpeg", "image/jpg");
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

    public void validateImageFile(MultipartFile logo) {
        if (logo == null || logo.isEmpty()) {
            throw new IllegalArgumentException("Logo file is required");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(logo.getContentType())) {
            throw new IllegalArgumentException("Invalid file type. Only PNG, JPG, and JPEG are allowed.");
        }

        if (logo.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Logo file size must not exceed 2MB.");
        }
    }

    public void validateImageFiles(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw new IllegalArgumentException("At least one image file is required.");
        }

        for (MultipartFile image : images) {
            validateImageFile(image);
        }
    }
}
