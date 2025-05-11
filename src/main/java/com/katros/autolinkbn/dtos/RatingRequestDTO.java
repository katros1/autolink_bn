package com.katros.autolinkbn.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class RatingRequestDTO {
    @Min(1)
    @Max(5)
    private int stars;
    private String comment;
}
