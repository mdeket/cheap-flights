package com.cheapflights.tickets.domain.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Airport {
    private Long id;
    private String name;
    private String city;
    private String country;

    // TODO: Can this be optional?
    private String iata;

    // TODO: Can this be optional?
    private String icao;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer altitude;
    private Float timezoneUtc;

    // TODO: Convert to ENUM
    private String dst;
    private String timezoneOlson;
    private String type;
    private String source;
}
