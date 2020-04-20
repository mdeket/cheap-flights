package com.cheapflights.tickets.domain.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.math.BigDecimal;
import java.util.List;

@Data
@NodeEntity
@Builder
public class Airport {

    @Id
    @GeneratedValue
    private Long id;
    private Long airportExternalId;
    private String name;
    private String city;
    private String country;

    private String iata;
    private String icao;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer altitude;
    private Float timezoneUtc;

    // TODO: Convert to ENUM
    private String dst;
    private String timezoneOlson;

    @JsonIgnoreProperties("airports")
    @Relationship(type = "ROUTE_TO")
    private List<Airport> airports;

}
