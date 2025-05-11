package com.katros.autolinkbn.repositories;

import com.katros.autolinkbn.entities.EmailOTP;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailOTPRepository extends MongoRepository<EmailOTP, String> {
    Optional<EmailOTP> findByEmail(String email);
    void deleteByEmail(String email);
}
