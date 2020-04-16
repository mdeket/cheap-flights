package com.cheapflights.tickets.service;

import com.cheapflights.tickets.domain.model.graph.Route;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RouteMapper implements CsvRecordMapper<Route> {

    @Override
    public Route fromCsvRecord(CSVRecord csvRecord) {
        return Route.builder()
                .airline(csvRecord.get(0))
                .airlineId(csvRecord.get(1))
                .sourceAirport(csvRecord.get(2))
                .sourceAirportId(csvRecord.get(3))
                .destinationAirport(csvRecord.get(4))
                .destinationAirportId(csvRecord.get(5))
                .codeshare(csvRecord.get(6))
                .stops(StringUtils.isEmpty(csvRecord.get(7)) ? null : Integer.valueOf(csvRecord.get(7)))
                .equipment(csvRecord.get(8))
                .price(new BigDecimal(csvRecord.get(9)))
                .build();
    }
}
