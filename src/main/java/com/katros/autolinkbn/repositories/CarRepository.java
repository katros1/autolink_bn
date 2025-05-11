package com.katros.autolinkbn.repositories;

import com.katros.autolinkbn.entities.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CarRepository extends MongoRepository<Car, String> {
    List<Car> findByOwnerId(String ownerId);

    Page<Car> findByOwnerId(String ownerId, Pageable pageable);

    Page<Car> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Car> findByBodyTypeIgnoreCase(String bodyType, Pageable pageable);

    Page<Car> findByTitleContainingIgnoreCaseAndBodyTypeIgnoreCase(String title, String bodyType, Pageable pageable);

    Page<Car> findByForRentTrue(Pageable pageable);
    Page<Car> findByForRentTrueAndTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Car> findByForRentTrueAndBodyTypeIgnoreCase(String bodyType, Pageable pageable);
    Page<Car> findByForRentTrueAndTitleContainingIgnoreCaseAndBodyTypeIgnoreCase(String title, String bodyType, Pageable pageable);

    Page<Car> findByForSaleTrue(Pageable pageable);
    Page<Car> findByForSaleTrueAndTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Car> findByForSaleTrueAndBodyTypeIgnoreCase(String bodyType, Pageable pageable);
    Page<Car> findByForSaleTrueAndTitleContainingIgnoreCaseAndBodyTypeIgnoreCase(String title, String bodyType, Pageable pageable);


}
