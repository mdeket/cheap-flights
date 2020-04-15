package com.cheapflights.tickets.service;

import lombok.extern.java.Log;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

@Service
@Log
public class ImportDataService implements CommandLineRunner {

    public void loadAirports() {
        log.info("Loading airports...");
        File file = null;
        try {
            file = ResourceUtils.getFile("classpath:airports.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (file == null) {
            throw new RuntimeException("Airports.txt can't be found in resource folder.");
        }

        CSVParser parser = null;
        try {
            parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.ORACLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (parser == null) {
            throw new RuntimeException("Could'n instantiate csv parser.");
        }

        for (CSVRecord csvRecord : parser) {
            System.out.println(csvRecord);
        }

        log.info("Successfully loaded airports.");
    }

    @Override
    public void run(String... args) {
        log.info("Started command line runner.");
        loadAirports();

    }
}
