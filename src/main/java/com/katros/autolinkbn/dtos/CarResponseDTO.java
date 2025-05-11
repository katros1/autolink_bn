package com.katros.autolinkbn.dtos;

import com.katros.autolinkbn.enums.CarStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class CarResponseDTO {
    private String id;
    private String ownerId;

    private String title;
    private String description;
    private String brand;
    private String model;
    private int year;
    private String color;
    private String transmission;
    private String fuelType;
    private int mileage;
    private int seatCount;
    private String bodyType;
    private String plateNumber;

    private boolean forRent;
    private boolean forSale;
    private BigDecimal rentalPricePerDay;
    private BigDecimal salePrice;

    private String city;
    private String state;
    private String country;
    private String address;

    private String coverImageUrl;
    private List<String> imageUrls;

    private boolean isAvailable;
    private CarStatus status;

    private double averageRating;
    private int totalRatings;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


