package com.katros.autolinkbn.controllers;

import com.katros.autolinkbn.dtos.BookingRequestDTO;
import com.katros.autolinkbn.dtos.BookingResponseDTO;
import com.katros.autolinkbn.entities.Booking;
import com.katros.autolinkbn.entities.User;
import com.katros.autolinkbn.services.BookingService;
import com.katros.autolinkbn.services.UserService;
import com.katros.autolinkbn.utils.CustomResponse;
import com.katros.autolinkbn.utils.JwtHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    private final JwtHandler jwtHandler;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public BookingController(BookingService bookingService, JwtHandler jwtHandler, HttpServletRequest httpServletRequest) {
        this.bookingService = bookingService;
        this.jwtHandler = jwtHandler;
        this.httpServletRequest = httpServletRequest;
    }

    @PostMapping("/{carId}/book")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<CustomResponse<String>> bookCar(
            @PathVariable String carId,
            @Valid @RequestBody BookingRequestDTO dto) {

        String renterId = jwtHandler.extractUserId(httpServletRequest.getHeader("Authorization").split(" ")[1]);

        bookingService.createBooking(carId, renterId, dto);

        return ResponseEntity.ok(CustomResponse.successResponse("Booking request sent", HttpStatus.OK.value()));
    }


    @PutMapping("/{bookingId}/approve")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<CustomResponse<String>> approveBooking(@PathVariable String bookingId) {
        bookingService.approveBooking(bookingId);
        return ResponseEntity.ok(CustomResponse.successResponse("Booking approved", HttpStatus.OK.value()));
    }

    @GetMapping("/owner")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<CustomResponse<List<Booking>>> getBookingsForCarOwner(HttpServletRequest request) {
        String ownerId = jwtHandler.extractUserId(request.getHeader("Authorization").split(" ")[1]);
        List<Booking> bookings = bookingService.getBookingsForOwner(ownerId);
        return ResponseEntity.ok(CustomResponse.successResponse("Bookings fetched", HttpStatus.OK.value(), bookings));
    }

    @PutMapping("/{bookingId}/cancel")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<CustomResponse<String>> cancelBooking(
            @PathVariable String bookingId,
            HttpServletRequest request) {

        String userId = jwtHandler.extractUserId(request.getHeader("Authorization").split(" ")[1]);
        bookingService.cancelBooking(bookingId, userId);
        return ResponseEntity.ok(CustomResponse.successResponse("Booking cancelled", HttpStatus.OK.value()));
    }

    @PutMapping("/{bookingId}/return")
    @PreAuthorize("hasAuthority('OWNER')") // or 'ADMIN' if only admins can do this
    public ResponseEntity<CustomResponse<String>> markBookingAsReturned(
            @PathVariable String bookingId,
            HttpServletRequest request) {

        String ownerId = jwtHandler.extractUserId(request.getHeader("Authorization").split(" ")[1]);
        bookingService.markCarAsReturned(bookingId, ownerId);
        return ResponseEntity.ok(CustomResponse.successResponse("Car marked as returned", HttpStatus.OK.value()));
    }

    @PutMapping("/{bookingId}/reject")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<CustomResponse<String>> rejectBooking(
            @PathVariable String bookingId,
            HttpServletRequest request) {

        String ownerId = jwtHandler.extractUserId(request.getHeader("Authorization").split(" ")[1]);
        bookingService.rejectBooking(bookingId, ownerId);
        return ResponseEntity.ok(CustomResponse.successResponse("Booking rejected", HttpStatus.OK.value()));
    }
}
