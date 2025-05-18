package com.katros.autolinkbn.dtos;

import com.katros.autolinkbn.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GetMadeOwnerCarsDTO {
    private String carId;
    private String carName;
    private String carPicUrl;
    private String renterId;
    private String renterName;
    private String renterPicUrl;
    private String renterPhone;
    private String renterEmail;
    private String renterAddress;
    private String bookingId;
    private LocalDateTime bookingDate;
    private BookingStatus bookingStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal rentalPricePerDay;
}
