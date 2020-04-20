package com.cheapflights.tickets.repository;

import com.cheapflights.tickets.domain.model.City;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;

@Repository
public interface CityRepository extends CrudRepository<City, Long> {

    @Query(value = "SELECT c.id as id, c.name as name, c.country as country, c.description as description, " +
            "com.id as commentId, com.text as text, com.created_at as createdAt, com.modified_at as modifiedAt " +
            "FROM city AS c " +
            "LEFT JOIN comment AS com ON com.id IN " +
            "(SELECT com1.id FROM comment AS com1 WHERE c.id=com1.city LIMIT ?1);"
            , nativeQuery = true)
    List<Tuple> findAllWithComments(int numberOfComments);

}
