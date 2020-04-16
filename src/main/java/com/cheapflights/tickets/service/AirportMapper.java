package com.cheapflights.tickets.service;

import com.cheapflights.tickets.domain.model.graph.Airport;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class AirportMapper implements CsvRecordMapper<Airport> {

    @Override
    public Airport fromCsvRecord(CSVRecord csvRecord) {
        return Airport.builder()
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
