package com.cheapflights.tickets.controller;


import com.cheapflights.tickets.domain.model.graph.Route;
import com.cheapflights.tickets.repository.graph.RouteRepository;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/routes")
public class RouteController {

    private final RouteRepository routeRepository;

    public RouteController(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @GetMapping
    public List<Route> getRoutes() {
        return IteratorUtils.toList(routeRepository.findAll().iterator());
    }
}