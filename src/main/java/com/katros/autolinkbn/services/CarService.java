package com.katros.autolinkbn.services;

import com.katros.autolinkbn.dtos.RatingRequestDTO;
import com.katros.autolinkbn.dtos.RatingResponseDTO;
import com.katros.autolinkbn.entities.Car;
import com.katros.autolinkbn.entities.Rating;
import com.katros.autolinkbn.exceptions.BadRequestException;
import com.katros.autolinkbn.exceptions.NotFoundException;
import com.katros.autolinkbn.exceptions.UnauthorizedException;
import com.katros.autolinkbn.repositories.CarCustomRepository;
import com.katros.autolinkbn.repositories.CarRepository;
import com.katros.autolinkbn.services.customvalidations.FileValidationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;

    private final CarCustomRepository carCustomRepository;

    private final CloudinaryService cloudinaryService;

    private final FileValidationService fileValidationService;

    public CarService(CarRepository carRepository, CarCustomRepository carCustomRepository, CloudinaryService cloudinaryService, FileValidationService fileValidationService) {
        this.carRepository = carRepository;
        this.carCustomRepository = carCustomRepository;
        this.cloudinaryService = cloudinaryService;
        this.fileValidationService = fileValidationService;
    }

    public Car createCar(Car car, MultipartFile coverImage, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw new BadRequestException("At least one image is required to create a car listing.");
        }

        if (car.isForRent() && (car.getRentalPricePerDay() == null || car.getRentalPricePerDay().compareTo(BigDecimal.ZERO) <= 0)) {
            throw new BadRequestException("Rental price must be provided and greater than zero when the car is for rent.");
        }

        if (car.isForSale() && (car.getSalePrice() == null || car.getSalePrice().compareTo(BigDecimal.ZERO) <= 0)) {
            throw new BadRequestException("Sale price must be provided and greater than zero when the car is for sale.");
        }

        fileValidationService.validateImageFile(coverImage);
        String coverImageUrl = cloudinaryService.uploadImageFile(coverImage, car.getTitle() + "cover_image");

        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            fileValidationService.validateImageFile(image);
            String url = cloudinaryService.uploadImageFile(image, car.getTitle());
            imageUrls.add(url);
        }

        car.setImageUrls(imageUrls);
        car.setCoverImageUrl(coverImageUrl);
        car.setCreatedAt(LocalDateTime.now());
        return carRepository.save(car);
    }


    public Car getCarById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new BadRequestException("Car ID must not be empty.");
        }

        return carRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Car not found with ID: " + id));
    }

    public Page<Car> getAllCars(Pageable pageable) {
        return carRepository.findAll(pageable);
    }

    public Page<Car> getFilteredCars(String title, String bodyType, Boolean forRent, Boolean forSale, Pageable pageable) {
        // Handle all filter combinations
        if (Boolean.TRUE.equals(forRent) && Boolean.TRUE.equals(forSale)) {
            // Invalid or conflicting scenario, return empty or all?
            return Page.empty(pageable);
        }

        if (Boolean.TRUE.equals(forRent)) {
            if (title != null && bodyType != null) {
                return carRepository.findByForRentTrueAndTitleContainingIgnoreCaseAndBodyTypeIgnoreCase(title, bodyType, pageable);
            } else if (title != null) {
                return carRepository.findByForRentTrueAndTitleContainingIgnoreCase(title, pageable);
            } else if (bodyType != null) {
                return carRepository.findByForRentTrueAndBodyTypeIgnoreCase(bodyType, pageable);
            } else {
                return carRepository.findByForRentTrue(pageable);
            }
        }

        if (Boolean.TRUE.equals(forSale)) {
            if (title != null && bodyType != null) {
                return carRepository.findByForSaleTrueAndTitleContainingIgnoreCaseAndBodyTypeIgnoreCase(title, bodyType, pageable);
            } else if (title != null) {
                return carRepository.findByForSaleTrueAndTitleContainingIgnoreCase(title, pageable);
            } else if (bodyType != null) {
                return carRepository.findByForSaleTrueAndBodyTypeIgnoreCase(bodyType, pageable);
            } else {
                return carRepository.findByForSaleTrue(pageable);
            }
        }

        // If neither forRent nor forSale is specified, apply title/bodyType filtering globally
        if (title != null && bodyType != null) {
            return carRepository.findByTitleContainingIgnoreCaseAndBodyTypeIgnoreCase(title, bodyType, pageable);
        } else if (title != null) {
            return carRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else if (bodyType != null) {
            return carRepository.findByBodyTypeIgnoreCase(bodyType, pageable);
        } else {
            return carRepository.findAll(pageable);
        }
    }

    public Page<Car> getCarsByOwnerWithFilters(String ownerId, Boolean forSale, Boolean forRent, String title, Pageable pageable) {
        return carCustomRepository.findCarsWithFilters(ownerId, forSale, forRent, title, pageable);
    }

    public Page<Car> searchByTitle(String title, Pageable pageable) {
        return carRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    public Page<Car> filterByBodyType(String bodyType, Pageable pageable) {
        return carRepository.findByBodyTypeIgnoreCase(bodyType, pageable);
    }

    public Page<Car> searchByTitleAndBodyType(String title, String bodyType, Pageable pageable) {
        return carRepository.findByTitleContainingIgnoreCaseAndBodyTypeIgnoreCase(title, bodyType, pageable);
    }

    public Page<Car> getRentalCarsFiltered(String title, String bodyType, Pageable pageable) {
        if (title != null && bodyType != null) {
            return carRepository.findByForRentTrueAndTitleContainingIgnoreCaseAndBodyTypeIgnoreCase(title, bodyType, pageable);
        } else if (title != null) {
            return carRepository.findByForRentTrueAndTitleContainingIgnoreCase(title, pageable);
        } else if (bodyType != null) {
            return carRepository.findByForRentTrueAndBodyTypeIgnoreCase(bodyType, pageable);
        } else {
            return carRepository.findByForRentTrue(pageable);
        }
    }

    public Page<Car> getSalesCarsFiltered(String title, String bodyType, Pageable pageable) {
        if (title != null && bodyType != null) {
            return carRepository.findByForSaleTrueAndTitleContainingIgnoreCaseAndBodyTypeIgnoreCase(title, bodyType, pageable);
        } else if (title != null) {
            return carRepository.findByForSaleTrueAndTitleContainingIgnoreCase(title, pageable);
        } else if (bodyType != null) {
            return carRepository.findByForSaleTrueAndBodyTypeIgnoreCase(bodyType, pageable);
        } else {
            return carRepository.findByForSaleTrue(pageable);
        }
    }

    public Car updateCar(String id, Car updatedCar) {
        if (id == null || id.trim().isEmpty()) {
            throw new BadRequestException("Car ID must not be empty.");
        }

        Car existing = getCarById(id);
        updatedCar.setId(id);
        updatedCar.setCreatedAt(existing.getCreatedAt());
        updatedCar.setUpdatedAt(LocalDateTime.now());
        return carRepository.save(updatedCar);
    }

    public void deleteCar(String id) {
        if (!carRepository.existsById(id)) {
            throw new NotFoundException("Cannot delete. Car not found with ID: " + id);
        }
        carRepository.deleteById(id);
    }

    public Car rateCar(String carId, String userId, RatingRequestDTO dto) {
        Car car = getCarById(carId);

        // Prevent duplicate ratings by same user (optional)
        car.getRatings().removeIf(r -> r.getUserId().equals(userId));

        Rating rating = new Rating();
        rating.setUserId(userId);
        rating.setStars(dto.getStars());
        rating.setComment(dto.getComment());
        rating.setRatedAt(LocalDateTime.now());

        car.getRatings().add(rating);

        // Recalculate average
        double avg = car.getRatings().stream()
                .mapToInt(Rating::getStars)
                .average()
                .orElse(0.0);
        car.setAverageRating(avg);

        return carRepository.save(car);
    }

    public List<RatingResponseDTO> getCarRatings(String carId) {
        Car car = getCarById(carId);
        return car.getRatings().stream()
                .map(r -> new RatingResponseDTO(r.getUserId(), r.getStars(), r.getComment(), r.getRatedAt()))
                .toList();
    }

    public Car save(Car car) {
        return carRepository.save(car);
    }

    public void updateAvailability(String carId, String ownerId, boolean available) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Car not found with ID: " + carId));

        if (!car.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedException("You are not authorized to modify this car.");
        }

        car.setAvailable(available);
        car.setUpdatedAt(LocalDateTime.now());
        carRepository.save(car);
    }

}
