package com.cheapflights.tickets.service.mapper.graph;

import com.cheapflights.tickets.domain.model.graph.Route;
import lombok.extern.java.Log;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Log
public class RouteGraphMapper {

    public Route fromCsvRecord(CSVRecord csvRecord) {
        return Route.builder()
                .airline(csvRecord.get(0))
                .airlineId(csvRecord.get(1))
                .sourceAirport(csvRecord.get(2))
                .sourceAirportId(loadSourceAirportIdFromCsvRecord(csvRecord))
                .destinationAirport(csvRecord.get(4))
                .destinationAirportId(loadDestinationAirportIdFromCsvRecord(csvRecord))
                .codeshare(csvRecord.get(6))
                .stops(loadNumberOfStopsFromCsvRecord(csvRecord))
                .equipment(csvRecord.get(8))
                .price(loadPriceFromCsvRecord(csvRecord))
                .build();
    }

    private BigDecimal loadPriceFromCsvRecord(CSVRecord csvRecord) {
        if (csvRecord.get(9) != null) {
            return new BigDecimal(csvRecord.get(9));
        } else {
            log.warning(String.format("Price missing for route with source/destination [%s/%s], setting max price.", csvRecord.get(2), csvRecord.get(4)));
            return BigDecimal.valueOf(Double.MAX_VALUE);
        }
    }

    private Integer loadNumberOfStopsFromCsvRecord(CSVRecord csvRecord) {
        return StringUtils.isEmpty(csvRecord.get(7)) ? null : Integer.valueOf(csvRecord.get(7));
    }

    private Long loadSourceAirportIdFromCsvRecord(CSVRecord csvRecord) {
        return csvRecord.get(3) != null ? Long.valueOf(csvRecord.get(3)) : null;
    }

    private Long loadDestinationAirportIdFromCsvRecord(CSVRecord csvRecord) {
        return csvRecord.get(5) != null ? Long.valueOf(csvRecord.get(5)) : null;
    }
}
