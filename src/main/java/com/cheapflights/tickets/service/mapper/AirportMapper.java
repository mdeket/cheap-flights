package com.cheapflights.tickets.service.mapper;

import com.cheapflights.tickets.domain.dto.AirportDTO;
import com.cheapflights.tickets.domain.model.Airport;
import org.springframework.stereotype.Component;

import javax.persistence.Tuple;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AirportMapper {

    // TODO: use this!
    public Airport fromGraphAirport(com.cheapflights.tickets.domain.model.graph.Airport graphAirport) {
        return Airport.builder()
                .externalId(graphAirport.getAirportExternalId())
                .name(graphAirport.getCity())
                .build();
    }

    public AirportDTO toDTO(Tuple tuple) {
        String airportName = tuple.get("airportName", String.class);
        Number externalAirportId = tuple.get("externalAirportId", Number.class);

        return AirportDTO.builder()
                .name(airportName)
                .externalId(externalAirportId != null ? externalAirportId.longValue() : null)
                .build();
    }

    public AirportDTO toDTO(Airport airport){
        return AirportDTO.builder()
                .externalId(airport.getId())
                .name(airport.getName())
                .build();
    }

    public List<AirportDTO> toDTO (List<Airport> airports) {
        return airports.stream().map(this::toDTO).collect(Collectors.toList());
    }

}
