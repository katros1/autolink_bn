package com.katros.autolinkbn.entities;

import com.katros.autolinkbn.enums.BookingStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "bookings")
public class Booking {
    @Id
    private String id;

    private String carId;
    private String renterId;
    private BookingStatus status = BookingStatus.PENDING;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
