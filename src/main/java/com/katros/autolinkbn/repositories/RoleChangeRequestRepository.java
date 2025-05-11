package com.katros.autolinkbn.repositories;

import com.katros.autolinkbn.entities.RoleChangeRequest;
import com.katros.autolinkbn.enums.RequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleChangeRequestRepository extends MongoRepository<RoleChangeRequest, String> {
    List<RoleChangeRequest> findByStatus(RequestStatus status);
}
