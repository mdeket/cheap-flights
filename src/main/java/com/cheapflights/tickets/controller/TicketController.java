package com.cheapflights.tickets.controller;

import com.cheapflights.tickets.service.graph.AirportGraphService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final AirportGraphService airportGraphService;

    public TicketController(AirportGraphService airportGraphService) {
        this.airportGraphService = airportGraphService;
    }

    /**
     * Endpoint for finding the cheapest ticket between two airports.
     *
     * @param from Source city id
     * @param to   Destination city id
     * @return Path from one city to another including total price
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> findCheapestFlight(@RequestParam Long from, @RequestParam Long to) {
        return ResponseEntity.ok(airportGraphService.findCheapestFlight(from, to));
    }
}
