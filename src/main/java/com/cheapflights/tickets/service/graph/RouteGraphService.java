package com.cheapflights.tickets.service.graph;

import com.cheapflights.tickets.domain.model.graph.Route;
import com.cheapflights.tickets.repository.graph.RouteGraphRepository;
import org.springframework.stereotype.Service;

@Service
public class RouteGraphService {

    private final RouteGraphRepository routeGraphRepository;

    public RouteGraphService(RouteGraphRepository routeGraphRepository) {
        this.routeGraphRepository = routeGraphRepository;
    }

    public Iterable<Route> saveAll(Iterable<Route> routes) {
        return routeGraphRepository.saveAll(routes);
    }
}
