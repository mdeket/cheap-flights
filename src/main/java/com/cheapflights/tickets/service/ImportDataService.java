package com.cheapflights.tickets.service;

import com.cheapflights.tickets.domain.model.graph.Airport;
import com.cheapflights.tickets.domain.model.graph.Route;
import com.cheapflights.tickets.repository.graph.AirportRepository;
import com.cheapflights.tickets.repository.graph.RouteRepository;
import lombok.extern.java.Log;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.function.Function;

@Service
@Log
public class ImportDataService implements CommandLineRunner {

    private final AirportRepository airportRepository;
    private final RouteRepository routeRepository;
    private Function<CSVRecord, Airport> airportMapper = (CSVRecord csvRecord) -> Airport.builder()
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
    private Function<CSVRecord, Route> routeMapper = (CSVRecord csvRecord) -> Route.builder()
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

    public ImportDataService(AirportRepository airportRepository, RouteRepository routeRepository) {
        this.airportRepository = airportRepository;
        this.routeRepository = routeRepository;
    }

    // TODO: SpringBoot Batch?
    public void loadAirports() {
        log.info("Loading airports...");
        File airportsFile = loadFile("classpath:airports.txt");
        parseAndSaveData(airportRepository, airportsFile, airportMapper);
    }

    @Override
    public void run(String... args) {
        log.info("Started command line runner from ImportDataService.");
        loadAirports();
        loadRoutes();
    }

    private void loadRoutes() {
        log.info("Loading routes...");
        File routesFile = loadFile("classpath:routes.txt");
        parseAndSaveData(routeRepository, routesFile, routeMapper);
    }

    private File loadFile(String path) {
        try {
            return ResourceUtils.getFile(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("%s can't be found.", path), e);
        }
    }

    private void parseAndSaveData(CrudRepository repository, File file, Function mapFunction) {
        log.info(String.format("Parsing %s", file.getPath()));
        double elapsedTimeInSecond;
        try {
            long start = System.nanoTime();
            CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.ORACLE);
            parser.getRecords().stream()
                    .parallel()
                    .filter(record -> Objects.nonNull(record.get(2)))
                    .map(mapFunction)
                    .forEach(repository::save);
            long end = System.nanoTime();
            elapsedTimeInSecond = (double) (end - start) / 1_000_000_000;
        } catch (IOException e) {
            throw new RuntimeException("Could'n instantiate csv parser.);", e);
        }
        log.info(String.format("Successfully loaded %s in %s seconds.", file.getPath(), elapsedTimeInSecond));
    }

}
