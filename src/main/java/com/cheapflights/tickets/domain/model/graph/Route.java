package com.cheapflights.tickets.domain.model.graph;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.neo4j.ogm.annotation.*;

import java.math.BigDecimal;

@RelationshipEntity
@Data
@Builder
@ToString(exclude = "id")
public class Route {

    @Id
    @GeneratedValue
    private Long id;

    private String airline;
    private String airlineId;
    private String sourceAirport;
    private Long sourceAirportId;
    private String destinationAirport;
    private Long destinationAirportId;
    private String codeshare;
    private Integer stops;
    private String equipment;
    private BigDecimal price;

    @StartNode
    private Airport source;

    @EndNode
    private Airport destination;
}
