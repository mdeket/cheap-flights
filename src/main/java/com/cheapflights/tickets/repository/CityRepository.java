package com.cheapflights.tickets.repository;

import com.cheapflights.tickets.domain.model.City;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends CrudRepository<City, Long> {

//    @Query(value = "SELECT c.* FROM CITY c INNER JOIN (SELECT * FROM COMMENT com ",nativeQuery = true)
//    List<City> findAllWithComments();

}
