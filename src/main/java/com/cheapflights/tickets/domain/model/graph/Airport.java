package com.cheapflights.tickets.domain.model.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.csv.CSVRecord;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import java.math.BigDecimal;

@Data
@NodeEntity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airport {


    @Id
    @GeneratedValue
    private Long id;
    private Long airportExternalId;
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

    // TODO: Static?
    public static Airport csvRecordToAirport(CSVRecord csvRecord) {
        return builder()
                .airportExternalId(Long.valueOf(csvRecord.get(0)))
                .name(csvRecord.get(1))
                .city(csvRecord.get(2))
                .country(csvRecord.get(3))
                .iata(csvRecord.get(4))
                .icao(csvRecord.get(5))
                .latitude(new BigDecimal(csvRecord.get(6)))
                .longitude(new BigDecimal(csvRecord.get(7)))
                .altitude(Integer.valueOf(csvRecord.get(8)))
                .timezoneUtc(csvRecord.get(9) != null ? Float.valueOf(csvRecord.get(9)) : null)
                .dst(csvRecord.get(10))
                .timezoneOlson(csvRecord.get(11))
                .build();
    }
}
