package com.cheapflights.tickets.service.graph;

import com.cheapflights.tickets.repository.graph.AirportGraphRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AirportGraphService {

    private final AirportGraphRepository airportGraphRepository;

    public AirportGraphService(AirportGraphRepository airportGraphRepository) {
        this.airportGraphRepository = airportGraphRepository;
    }

    /**
     * Finds cheapest flight between two airports using dijkstra algorithm from neo4j plugin.
     * @param fromExternalId Source airport - externalAirportId field.
     * @param toExternalId Destination airport - externalAirportId field.
     * @return Total price and airports that are included in the route.
     */
    public List<Map<String, Object>> findCheapestFlight(long fromExternalId, long toExternalId) {
        return airportGraphRepository.findCheapestFlight(fromExternalId, toExternalId);
    }

}
