package com.katros.autolinkbn.repositories;

import com.katros.autolinkbn.entities.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByCarId(String carId);
    List<Booking> findByRenterId(String renterId);
    List<Booking> findByCarIdIn(List<String> carIds);

}
