package com.katros.autolinkbn.services;

import com.katros.autolinkbn.dtos.AdminDashboardStatsDTO;
import com.katros.autolinkbn.enums.BookingStatus;
import com.katros.autolinkbn.repositories.BookingRepository;
import com.katros.autolinkbn.repositories.CarRepository;
import com.katros.autolinkbn.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AdminDashboardService {

    private final CarRepository carRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public AdminDashboardService(CarRepository carRepository,
                                 BookingRepository bookingRepository,
                                 UserRepository userRepository) {
        this.carRepository = carRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public AdminDashboardStatsDTO getDashboardStats() {
        long totalCars = carRepository.count();
        long availableCars = carRepository.countByIsAvailable(true);
        long unavailableCars = carRepository.countByIsAvailable(false);
        long carsForRent = carRepository.countByForRentTrue();
        long carsForSale = carRepository.countByForSaleTrue();

        long totalBookings = bookingRepository.count();
        long approved = bookingRepository.countByStatus(BookingStatus.APPROVED);
        long returned = bookingRepository.countByStatus(BookingStatus.RETURNED);
        long successfulBookings = approved + returned;
        long pendingBookings = bookingRepository.countByStatus(BookingStatus.PENDING);
        long rejectedBookings = bookingRepository.countByStatus(BookingStatus.REJECTED);
        long canceledBookings = bookingRepository.countByStatus(BookingStatus.CANCELLED);

        long totalOwners = userRepository.countByRoles("OWNER");
        long totalRenters = userRepository.countByRoles("CLIENT");

        return new AdminDashboardStatsDTO(
                totalCars,
                totalRenters,
                totalOwners,
                totalBookings,
                successfulBookings,
                pendingBookings,
                rejectedBookings,
                canceledBookings,
                availableCars,
                unavailableCars,
                carsForRent,
                carsForSale
        );
    }
}
