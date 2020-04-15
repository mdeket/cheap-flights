package com.cheapflights.tickets.service;

import com.cheapflights.tickets.domain.model.graph.Airport;
import com.cheapflights.tickets.repository.graph.AirportRepository;
import lombok.extern.java.Log;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

@Service
@Log
public class ImportDataService implements CommandLineRunner {

    private final AirportRepository airportRepository;

    public ImportDataService(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    // TODO: SpringBoot Batch?
    public void loadAirports() {
        log.info("Loading airports...");
        File airportsFile;
        try {
            airportsFile = ResourceUtils.getFile("classpath:airports.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Airports.txt can't be found in resource folder.", e);
        }

        try {
            CSVParser parser = CSVParser.parse(airportsFile, Charset.defaultCharset(), CSVFormat.ORACLE);
            parser.getRecords().stream()
                    .filter(record -> Objects.nonNull(record.get(2)))
                    .map(Airport::csvRecordToAirport)
                    .forEach(airportRepository::save);
        } catch (IOException e) {
            throw new RuntimeException("Could'n instantiate csv parser.);", e);
        }
        log.info("Successfully loaded airports.");
    }

    @Override
    public void run(String... args) {
        log.info("Started command line runner from ImportDataService.");
        loadAirports();
    }

}
