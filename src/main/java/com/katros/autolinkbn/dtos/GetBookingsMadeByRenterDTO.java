package com.katros.autolinkbn.dtos;

import com.katros.autolinkbn.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GetBookingsMadeByRenterDTO {
    private String carId;
    private String carName;
    private String carPicUrl;
    private String ownerId;
    private String ownerName;
    private String ownerPicUrl;
    private String ownerPhone;
    private String ownerEmail;
    private String ownerAddress;
    private String bookingId;
    private LocalDateTime bookingDate;
    private BookingStatus bookingStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal rentalPricePerDay;
}
