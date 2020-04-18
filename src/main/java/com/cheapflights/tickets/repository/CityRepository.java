package com.cheapflights.tickets.repository;

import com.cheapflights.tickets.domain.model.City;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends CrudRepository<City, Long> {
}
