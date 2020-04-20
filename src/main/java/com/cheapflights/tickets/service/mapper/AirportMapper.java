package com.cheapflights.tickets.service.mapper;

import com.cheapflights.tickets.domain.model.graph.Airport;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AirportMapper  {

    public Airport fromCsvRecord(CSVRecord csvRecord) {
        return Airport.builder()
                .airportExternalId(Long.valueOf(csvRecord.get(0)))
                .name(csvRecord.get(1))
                .city(csvRecord.get(2))
                .country(csvRecord.get(3))
                .iata(csvRecord.get(4))
                .icao(csvRecord.get(5))
                .latitude(loadLatitudeFromCsvRecord(csvRecord))
                .longitude(loadLongitudeFromCsvRecord(csvRecord))
                .altitude(loadAltitudeFromCsvRecord(csvRecord))
                .timezoneUtc(loadTimezoneUtcFromCsvRecord(csvRecord))
                .dst(csvRecord.get(10))
                .timezoneOlson(csvRecord.get(11))
                .build();
    }

    private Integer loadAltitudeFromCsvRecord(CSVRecord csvRecord) {
        return csvRecord.get(8) != null ? Integer.valueOf(csvRecord.get(8)) : null;
    }

    private BigDecimal loadLatitudeFromCsvRecord(CSVRecord csvRecord) {
        return csvRecord.get(6) != null ? new BigDecimal(csvRecord.get(6)) : null;
    }

    private BigDecimal loadLongitudeFromCsvRecord(CSVRecord csvRecord) {
        return csvRecord.get(7) != null ? new BigDecimal(csvRecord.get(7)) : null;
    }

    private Float loadTimezoneUtcFromCsvRecord(CSVRecord csvRecord) {
        return csvRecord.get(9) != null ? Float.valueOf(csvRecord.get(9)) : null;
    }
}
