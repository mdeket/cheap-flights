package com.cheapflights.tickets.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class ImportDataService implements CommandLineRunner {

    public void loadAirports() {
        File file = null;
        try {
            file = ResourceUtils.getFile("classpath:airports.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(file == null) {
            throw new RuntimeException("Airports.txt can't be found in resource folder.");
        }

        try (LineIterator it = FileUtils.lineIterator(file, "UTF-8")) {
            while (it.hasNext()) {
                String line = it.nextLine();
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String... args) {
        loadAirports();
    }
}
