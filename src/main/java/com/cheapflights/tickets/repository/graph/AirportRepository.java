package com.cheapflights.tickets.repository.graph;

import com.cheapflights.tickets.domain.model.graph.Airport;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportRepository extends Neo4jRepository<Airport, Long> {}

