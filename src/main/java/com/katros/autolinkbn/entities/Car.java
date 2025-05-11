package com.katros.autolinkbn.entities;

import com.katros.autolinkbn.enums.CarStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Document(collection = "cars")
public class Car {

    @Id
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

    private List<Rating> ratings = new ArrayList<>();
    private double averageRating;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}

