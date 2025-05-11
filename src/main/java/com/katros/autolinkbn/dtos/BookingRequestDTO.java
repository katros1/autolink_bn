package com.katros.autolinkbn.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequestDTO {
    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;
}
