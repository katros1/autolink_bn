package com.katros.autolinkbn.dtos;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class RatingResponseDTO {
    private String userId;
    private int stars;
    private String comment;
    private LocalDateTime ratedAt;
}

