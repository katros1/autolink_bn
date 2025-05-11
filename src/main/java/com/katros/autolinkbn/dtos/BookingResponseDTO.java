package com.katros.autolinkbn.dtos;

import com.katros.autolinkbn.enums.BookingStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class BookingResponseDTO {
    private String bookingId;
    private String carId;
    private String renterId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BookingStatus status;
    private LocalDateTime createdAt;
}
