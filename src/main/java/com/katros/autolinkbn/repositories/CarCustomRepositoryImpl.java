package com.katros.autolinkbn.repositories;

import com.katros.autolinkbn.entities.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CarCustomRepositoryImpl implements CarCustomRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<Car> findCarsWithFilters(String ownerId, Boolean forSale, Boolean forRent, String title, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (ownerId != null && !ownerId.isBlank()) {
            criteriaList.add(Criteria.where("ownerId").is(ownerId));
        }

        if (forSale != null) {
            criteriaList.add(Criteria.where("forSale").is(forSale));
        }

        if (forRent != null) {
            criteriaList.add(Criteria.where("forRent").is(forRent));
        }

        if (title != null && !title.isBlank()) {
            criteriaList.add(Criteria.where("title").regex(title, "i"));
        }

        Criteria finalCriteria = new Criteria();
        if (!criteriaList.isEmpty()) {
            finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        }

        Query query = new Query(finalCriteria).with(pageable);

        List<Car> cars = mongoTemplate.find(query, Car.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Car.class);

        return new PageImpl<>(cars, pageable, total);
    }
}