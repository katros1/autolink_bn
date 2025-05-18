package com.katros.autolinkbn.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardStatsDTO {
    private long totalCars;
    private long totalRenters;
    private long totalOwners;
    private long totalBookings;
    private long successfulBookings;
    private long pendingBookings;
    private long rejectedBookings;
    private long canceledBookings;
    private long availableCars;
    private long unavailableCars;
    private long carsForRent;
    private long carsForSale;
}
