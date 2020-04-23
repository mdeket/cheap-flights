package com.cheapflights.tickets.repository;

import com.cheapflights.tickets.domain.model.Airport;
import org.springframework.data.repository.CrudRepository;

public interface AirportRepository extends CrudRepository<Airport, Long> {
}
