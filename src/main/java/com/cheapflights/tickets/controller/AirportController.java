package com.cheapflights.tickets.controller;


import com.cheapflights.tickets.domain.model.graph.Airport;
import com.cheapflights.tickets.repository.graph.AirportRepository;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/airports")
public class AirportController {

    private final AirportRepository airportRepository;

    public AirportController(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @GetMapping
    public List<Airport> getMovieTitles() {
        return IteratorUtils.toList(airportRepository.findAll().iterator());
    }
}