package com.cheapflights.tickets.service;

import com.cheapflights.tickets.repository.graph.AirportRepository;
import com.cheapflights.tickets.repository.graph.RouteRepository;
import lombok.extern.java.Log;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log
public class ImportDataService implements CommandLineRunner {

    private final AirportRepository airportRepository;
    private final RouteRepository routeRepository;
    private final AirportMapper airportMapper;
    private final RouteMapper routeMapper;

    public ImportDataService(AirportRepository airportRepository, RouteRepository routeRepository, AirportMapper airportMapper, RouteMapper routeMapper) {
        this.airportRepository = airportRepository;
        this.routeRepository = routeRepository;
        this.airportMapper = airportMapper;
        this.routeMapper = routeMapper;
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

    private <T> void parseAndSaveData(CrudRepository<T, Long> repository, File file, CsvRecordMapper<T> mapper) {
        log.info(String.format("Parsing %s", file.getPath()));
        double elapsedTimeInSecond;
        try {
            long start = System.nanoTime();
            CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.ORACLE);
            List<T> collection = parser.getRecords().stream()
                    .parallel()
                    .filter(record -> Objects.nonNull(record.get(2)))
                    .map(mapper::fromCsvRecord)
                    .collect(Collectors.toList());
            repository.saveAll(collection);
            long end = System.nanoTime();
            elapsedTimeInSecond = (double) (end - start) / 1_000_000_000;
        } catch (IOException e) {
            throw new RuntimeException("Could'n instantiate csv parser.", e);
        }
        log.info(String.format("Successfully loaded %s in %s seconds.", file.getPath(), elapsedTimeInSecond));
    }

}
