package com.katros.autolinkbn.services;

import com.katros.autolinkbn.dtos.BookingRequestDTO;
import com.katros.autolinkbn.dtos.BookingResponseDTO;
import com.katros.autolinkbn.dtos.GetBookingsMadeByRenterDTO;
import com.katros.autolinkbn.dtos.GetMadeOwnerCarsDTO;
import com.katros.autolinkbn.entities.Booking;
import com.katros.autolinkbn.entities.Car;
import com.katros.autolinkbn.entities.User;
import com.katros.autolinkbn.enums.BookingStatus;
import com.katros.autolinkbn.exceptions.BadRequestException;
import com.katros.autolinkbn.exceptions.ForbiddenException;
import com.katros.autolinkbn.exceptions.NotFoundException;
import com.katros.autolinkbn.repositories.BookingRepository;
import com.katros.autolinkbn.repositories.CarRepository;
import com.katros.autolinkbn.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    private final CarService carService;

    private final NotificationService notificationService;

    private final CarRepository carRepository;

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository, CarService carService, NotificationService notificationService, CarRepository carRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.carService = carService;
        this.notificationService = notificationService;
        this.carRepository = carRepository;
    }

    public void createBooking(String carId, String renterId, BookingRequestDTO dto) {
        Car car = carService.getCarById(carId);
        if (!car.isAvailable()) throw new BadRequestException("Car is not available");

        Booking booking = new Booking();
        booking.setCarId(carId);
        booking.setRenterId(renterId);
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setCreatedAt(LocalDateTime.now());
        booking.setStatus(BookingStatus.PENDING);

        bookingRepository.save(booking);

        notificationService.sendPushNotificationToCarOwner(car.getOwnerId(),
                "New booking request for your car: " + car.getTitle());
    }

    public void approveBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        Car car = carService.getCarById(booking.getCarId());
        car.setAvailable(false);
        car.setUpdatedAt(LocalDateTime.now());
        carService.save(car);

        booking.setStatus(BookingStatus.APPROVED);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        notificationService.sendPushNotificationToRenter(booking.getRenterId(),
                "Your booking for car '" + car.getTitle() + "' has been approved!");
    }

    public List<GetMadeOwnerCarsDTO> getBookingsForOwner(String ownerId) {

        List<Car> ownerCars = carRepository.findByOwnerId(ownerId);


        List<String> carIds = ownerCars.stream().map(Car::getId).toList();

        List<Booking> bookingsMade = bookingRepository.findByCarIdIn(carIds);

        return bookingsMade.stream().map(booking -> {

            User user = userRepository.findById(booking.getRenterId()).get();
            Car car = carService.getCarById(booking.getCarId());

            return new GetMadeOwnerCarsDTO(
                    booking.getCarId(),
                    car.getTitle(),
                    car.getCoverImageUrl(),
                    booking.getRenterId(),
                    user.getLastName() + " " + user.getFirstName(),
                    user.getProfilePicUrl(),
                    user.getPhoneNumber(),
                    user.getEmail(),
                    user.getCountry(),
                    booking.getId(),
                    booking.getCreatedAt(),
                    booking.getStatus(),
                    booking.getStartDate(),
                    booking.getEndDate(),
                    car.getRentalPricePerDay()
            );
        }).toList();
    }

    public List<GetBookingsMadeByRenterDTO> getBookingsMadeByRenter(String renterId) {
        List<Booking> bookings = bookingRepository.findByRenterIdOrderByCreatedAtDesc(renterId);

        return bookings.stream().map(booking -> {
            Car car = carRepository.findById(booking.getCarId())
                    .orElseThrow(() -> new NotFoundException("Car not found with id: " + booking.getCarId()));

            User owner = userRepository.findById(car.getOwnerId())
                    .orElseThrow(() -> new NotFoundException("Owner not found with id: " + car.getOwnerId()));

            return new GetBookingsMadeByRenterDTO(
                    car.getId(),
                    car.getTitle(),
                    car.getCoverImageUrl(),
                    owner.getUserId(),
                    owner.getFirstName() + " " + owner.getLastName(),
                    owner.getProfilePicUrl(),
                    owner.getPhoneNumber(),
                    owner.getEmail(),
                    owner.getCountry(),
                    booking.getId(),
                    booking.getCreatedAt(),
                    booking.getStatus(),
                    booking.getStartDate(),
                    booking.getEndDate(),
                    car.getRentalPricePerDay()
            );
        }).toList();
    }

    public void cancelBooking(String bookingId, String userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getRenterId().equals(userId)) {
            throw new ForbiddenException("You can only cancel your own bookings");
        }

        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.APPROVED) {
            throw new BadRequestException("Only pending or approved bookings can be canceled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
    }

    public void markCarAsReturned(String bookingId, String ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        Car car = carRepository.findById(booking.getCarId())
                .orElseThrow(() -> new NotFoundException("Car not found"));

        if (!car.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("Only the car owner can mark it as returned");
        }

        booking.setStatus(BookingStatus.RETURNED);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        car.setAvailable(true);
        car.setUpdatedAt(LocalDateTime.now());
        carRepository.save(car);
    }

    public void rejectBooking(String bookingId, String ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        Car car = carRepository.findById(booking.getCarId())
                .orElseThrow(() -> new NotFoundException("Car not found"));

        if (!car.getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("You are not authorized to reject this booking");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Only pending bookings can be rejected");
        }

        booking.setStatus(BookingStatus.REJECTED);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
    }



}

