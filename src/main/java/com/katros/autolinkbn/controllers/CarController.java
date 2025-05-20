package com.katros.autolinkbn.controllers;

import com.katros.autolinkbn.dtos.*;
import com.katros.autolinkbn.entities.Car;
import com.katros.autolinkbn.entities.User;
import com.katros.autolinkbn.exceptions.NotFoundException;
import com.katros.autolinkbn.repositories.UserRepository;
import com.katros.autolinkbn.services.CarService;
import com.katros.autolinkbn.utils.CarMapper;
import com.katros.autolinkbn.utils.CustomResponse;
import com.katros.autolinkbn.utils.JwtHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    private final UserRepository userRepository;
    private final CarService carService;
    private final CarMapper carMapper;
    private final JwtHandler jwtHandler;
    private final HttpServletRequest httpServletRequest;

    public CarController(UserRepository userRepository, CarService carService, CarMapper carMapper, JwtHandler jwtHandler, HttpServletRequest httpServletRequest) {
        this.userRepository = userRepository;
        this.carService = carService;
        this.carMapper = carMapper;
        this.jwtHandler = jwtHandler;
        this.httpServletRequest = httpServletRequest;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<CustomResponse<Car>> createCar(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("brand") String brand,
            @RequestParam("model") String model,
            @RequestParam("year") int year,
            @RequestParam("color") String color,
            @RequestParam("transmission") String transmission,
            @RequestParam("fuelType") String fuelType,
            @RequestParam("mileage") int mileage,
            @RequestParam("seatCount") int seatCount,
            @RequestParam("bodyType") String bodyType,
            @RequestParam("plateNumber") String plateNumber,
            @RequestParam("forRent") boolean forRent,
            @RequestParam("forSale") boolean forSale,
            @RequestParam(required = false, name = "rentalPricePerDay") BigDecimal rentalPricePerDay,
            @RequestParam(required = false, name = "salePrice") BigDecimal salePrice,
            @RequestParam("city") String city,
            @RequestParam("state") String state,
            @RequestParam("country") String country,
            @RequestParam("address") String address,
            @RequestPart("coverImage") MultipartFile coverImage,
            @RequestPart("images") List<MultipartFile> images,
            HttpServletRequest request
    ) {
        String userId = jwtHandler.extractUserId(request.getHeader("Authorization").split(" ")[1]);

        // Manually build the Car entity
        Car car = new Car();
        car.setTitle(title);
        car.setDescription(description);
        car.setBrand(brand);
        car.setModel(model);
        car.setYear(year);
        car.setColor(color);
        car.setTransmission(transmission);
        car.setFuelType(fuelType);
        car.setMileage(mileage);
        car.setSeatCount(seatCount);
        car.setBodyType(bodyType);
        car.setPlateNumber(plateNumber);
        car.setForRent(forRent);
        car.setForSale(forSale);
        car.setRentalPricePerDay(rentalPricePerDay);
        car.setSalePrice(salePrice);
        car.setCity(city);
        car.setState(state);
        car.setCountry(country);
        car.setAddress(address);
        car.setOwnerId(userId);

        Car savedCar = carService.createCar(car, coverImage, images);
        return ResponseEntity.ok(CustomResponse.successResponse("Car created successfully", HttpStatus.OK.value(), savedCar));
    }



    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<CarResponseDTO>> getCar(@PathVariable String id) {
        Car car = carService.getCarById(id);

        User ownerData = userRepository.findById(car.getOwnerId())
                .orElseThrow(() -> new NotFoundException("User  not found"));

        CarResponseDTO responseDTO = new CarResponseDTO();
        BeanUtils.copyProperties(car, responseDTO);

        responseDTO.setOwnerName(ownerData.getLastName() + " " + ownerData.getFirstName());
        responseDTO.setOwnerPhoneNumber(ownerData.getPhoneNumber());

        return ResponseEntity.ok(CustomResponse.successResponse("Car fetched successfully", HttpStatus.OK.value(), responseDTO));
    }

    @GetMapping
    public ResponseEntity<CustomResponse<PaginatedResponse<CarResponseDTO>>> getAllCars(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String bodyType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Car> carPage;

        if (title != null && bodyType != null) {
            carPage = carService.searchByTitleAndBodyType(title, bodyType, pageable);
        } else if (title != null) {
            carPage = carService.searchByTitle(title, pageable);
        } else if (bodyType != null) {
            carPage = carService.filterByBodyType(bodyType, pageable);
        } else {
            carPage = carService.getAllCars(pageable);
        }

        List<CarResponseDTO> carDTOs = carPage.getContent().stream()
                .map(carMapper::toDto)
                .toList();

        PaginatedResponse<CarResponseDTO> response = new PaginatedResponse<>();
        response.setContent(carDTOs);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(carPage.getTotalElements());
        response.setTotalPages(carPage.getTotalPages());

        return ResponseEntity.ok(CustomResponse.successResponse("Cars fetched", HttpStatus.OK.value(), response));
    }

    @GetMapping("/filtered")
    public ResponseEntity<CustomResponse<PaginatedResponse<CarResponseDTO>>> getCars(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String bodyType,
            @RequestParam(required = false) Boolean forRent,
            @RequestParam(required = false) Boolean forSale,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Car> carPage = carService.getFilteredCars(title, bodyType, forRent, forSale, pageable);

        List<CarResponseDTO> carDTOs = carPage.getContent().stream()
                .map(carMapper::toDto)
                .toList();

        PaginatedResponse<CarResponseDTO> response = new PaginatedResponse<>();
        response.setContent(carDTOs);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(carPage.getTotalElements());
        response.setTotalPages(carPage.getTotalPages());

        return ResponseEntity.ok(CustomResponse.successResponse("Cars fetched", HttpStatus.OK.value(), response));
    }


    @GetMapping("/owner/filtered")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<CustomResponse<PaginatedResponse<CarResponseDTO>>> getCarsByOwner(
//            @RequestParam String ownerId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean forRent,
            @RequestParam(required = false) Boolean forSale,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        String ownerId = jwtHandler.extractUserId(request.getHeader("Authorization").split(" ")[1]);

        Page<Car> carPage = carService.getCarsByOwnerWithFilters(ownerId, forSale, forRent, title, pageable);

        List<CarResponseDTO> carDTOs = carPage.getContent()
                .stream()
                .map(carMapper::toDto)
                .toList();

        PaginatedResponse<CarResponseDTO> response = new PaginatedResponse<>();
        response.setContent(carDTOs);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(carPage.getTotalElements());
        response.setTotalPages(carPage.getTotalPages());

        return ResponseEntity.ok(CustomResponse.successResponse("Owner's cars fetched", HttpStatus.OK.value(), response));
    }


    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse<Car>> updateCar(@PathVariable String id, @RequestBody Car car) {
        Car updatedCar = carService.updateCar(id, car);
        return ResponseEntity.ok(CustomResponse.successResponse("Car updated successfully", HttpStatus.OK.value(), updatedCar));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<String>> deleteCar(@PathVariable String id) {
        carService.deleteCar(id);
        return ResponseEntity.ok(CustomResponse.successResponse("Car deleted successfully", HttpStatus.OK.value(), null));
    }

    @PostMapping("/{carId}/rate")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<CustomResponse<String>> rateCar(
            @PathVariable String carId,
            @Valid @RequestBody RatingRequestDTO dto) {

        String userId = jwtHandler.extractUserId(httpServletRequest.getHeader("Authorization").split(" ")[1]);

        carService.rateCar(carId, userId, dto);
        return ResponseEntity.ok(CustomResponse.successResponse("Car rated successfully", HttpStatus.OK.value()));
    }

    @GetMapping("/{carId}/ratings")
    public ResponseEntity<CustomResponse<List<RatingResponseDTO>>> getCarRatings(@PathVariable String carId) {
        List<RatingResponseDTO> ratings = carService.getCarRatings(carId);
        return ResponseEntity.ok(CustomResponse.successResponse("Car ratings retrieved", HttpStatus.OK.value(), ratings));
    }

    @PutMapping("/{carId}/availability")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<CustomResponse<String>> updateCarAvailability(
            @PathVariable String carId,
            @RequestParam boolean available,
            HttpServletRequest request) {

        String ownerId = jwtHandler.extractUserId(request.getHeader("Authorization").split(" ")[1]);
        carService.updateAvailability(carId, ownerId, available);

        String status = available ? "available" : "unavailable";
        return ResponseEntity.ok(CustomResponse.successResponse("Car marked as " + status, HttpStatus.OK.value()));
    }

}

