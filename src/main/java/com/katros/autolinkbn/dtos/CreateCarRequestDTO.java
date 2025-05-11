package com.katros.autolinkbn.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCarRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @Min(value = 1886, message = "Invalid year for car")
    private int year;

    @NotBlank(message = "Color is required")
    private String color;

    @NotBlank(message = "Transmission is required")
    private String transmission;

    @NotBlank(message = "Fuel type is required")
    private String fuelType;

    @Min(value = 0, message = "Mileage must be non-negative")
    private int mileage;

    @Min(value = 1, message = "Seat count must be at least 1")
    private int seatCount;

    @NotBlank(message = "Body type is required")
    private String bodyType;

    @NotBlank(message = "Plate number is required")
    private String plateNumber;

    private boolean forRent;
    private boolean forSale;

    private BigDecimal rentalPricePerDay;

    private BigDecimal salePrice;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Address is required")
    private String address;
}
