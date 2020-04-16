package com.cheapflights.tickets.domain.model.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import java.math.BigDecimal;

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

}
