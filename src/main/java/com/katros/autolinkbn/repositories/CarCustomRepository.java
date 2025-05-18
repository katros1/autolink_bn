package com.katros.autolinkbn.repositories;

import com.katros.autolinkbn.entities.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarCustomRepository {
    Page<Car> findCarsWithFilters(String ownerId, Boolean forSale, Boolean forRent, String title, Pageable pageable);
}
