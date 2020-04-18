package com.cheapflights.tickets.controller;

import com.cheapflights.tickets.repository.graph.AirportRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final AirportRepository airportRepository;

    public TicketController(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @GetMapping("/from/{fromId}/to/{toId}")
    public Object findCheapestFlight(@PathVariable Long fromId, @PathVariable Long toId) {
        return airportRepository.findCheapestFlight(fromId, toId);
    }

    @GetMapping("/from/{fromId}/to/{toId}/routes")
    public Object findCheapestFlightRoutes(@PathVariable Long fromId, @PathVariable Long toId) {
        return airportRepository.findCheapestFlightRoutes(fromId, toId);
    }
}
