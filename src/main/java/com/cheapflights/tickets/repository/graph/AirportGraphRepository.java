package com.cheapflights.tickets.repository.graph;

import com.cheapflights.tickets.domain.model.graph.Airport;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AirportGraphRepository extends Neo4jRepository<Airport, Long> {

    @Query(value = "MATCH (from:Airport{airportExternalId: $fromId}), (end:Airport{airportExternalId: $toId}) CALL apoc.algo.dijkstra(from, end, 'ROUTE>', 'price') YIELD path, weight as totalPrice RETURN nodes(path) AS airports, totalPrice")
    List<Map<String, Object>> findCheapestFlight(@Param("fromId") Long fromExternalAirportId, @Param("toId") Long toExternalAirportId);

    List<Airport> findByCity(String cityName);
}

