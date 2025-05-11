package com.katros.autolinkbn.repositories;

import com.katros.autolinkbn.entities.ResetPasswordRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetPasswordRepository extends MongoRepository<ResetPasswordRequest, String> {

    @Query("{'email': ?0}")
    Optional<ResetPasswordRequest> findOtpByEmail(String email);
}
