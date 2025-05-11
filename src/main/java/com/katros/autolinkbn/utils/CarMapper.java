package com.katros.autolinkbn.utils;

import com.katros.autolinkbn.dtos.CarRequestDTO;
import com.katros.autolinkbn.dtos.CarResponseDTO;
import com.katros.autolinkbn.dtos.CreateCarRequestDTO;
import com.katros.autolinkbn.entities.Car;
import com.katros.autolinkbn.enums.CarStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CarMapper {
    public CarResponseDTO toDto(Car car) {
        CarResponseDTO dto = new CarResponseDTO();
        BeanUtils.copyProperties(car, dto);
        return dto;
    }

    public Car toEntity(CarRequestDTO dto) {
        Car car = new Car();
        BeanUtils.copyProperties(dto, car);
        return car;
    }

    public Car fromDto(CreateCarRequestDTO dto) {
        Car car = new Car();
        car.setTitle(dto.getTitle());
        car.setDescription(dto.getDescription());
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setYear(dto.getYear());
        car.setColor(dto.getColor());
        car.setTransmission(dto.getTransmission());
        car.setFuelType(dto.getFuelType());
        car.setMileage(dto.getMileage());
        car.setSeatCount(dto.getSeatCount());
        car.setBodyType(dto.getBodyType());
        car.setPlateNumber(dto.getPlateNumber());
        car.setForRent(dto.isForRent());
        car.setForSale(dto.isForSale());
        car.setRentalPricePerDay(dto.getRentalPricePerDay());
        car.setSalePrice(dto.getSalePrice());
        car.setCity(dto.getCity());
        car.setState(dto.getState());
        car.setCountry(dto.getCountry());
        car.setAddress(dto.getAddress());
        car.setAvailable(true);
        car.setStatus(CarStatus.PENDING); // Or DEFAULT
        return car;
    }

}
