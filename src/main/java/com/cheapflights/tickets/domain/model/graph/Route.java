package com.cheapflights.tickets.domain.model.graph;

import lombok.Builder;
import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import java.math.BigDecimal;

@Data
@NodeEntity
@Builder
public class Route {

    @Id
    @GeneratedValue
    private Long id;
    private String airline;
    private String airlineId;
    private String sourceAirport;
    private String sourceAirportId;
    private String destinationAirport;
    private String destinationAirportId;
    private String codeshare;
    private Integer stops;
    private String equipment;
    private BigDecimal price;

}
