package com.katros.autolinkbn.repositories;

import com.katros.autolinkbn.entities.DeviceToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends MongoRepository<DeviceToken, String> {
    Optional<DeviceToken> findByUserId(String userId);
    Optional<DeviceToken> findByToken(String token);
}

