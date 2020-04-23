package com.cheapflights.tickets.repository.graph;

import com.cheapflights.tickets.domain.model.graph.Route;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteGraphRepository extends Neo4jRepository<Route, Long> {
}

