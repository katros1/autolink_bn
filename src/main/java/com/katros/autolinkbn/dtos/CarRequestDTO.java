package com.katros.autolinkbn.dtos;

import lombok.Data;

@Data
public class CarRequestDTO {
    private String title;
    private String description;
    private String make;
    private String model;
    private int year;
    private double price;
    private String ownerId;
}
