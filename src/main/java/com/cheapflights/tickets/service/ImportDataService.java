package com.cheapflights.tickets.service;

import com.cheapflights.tickets.domain.model.City;
import com.cheapflights.tickets.domain.model.graph.Airport;
import com.cheapflights.tickets.domain.model.graph.Route;
import com.cheapflights.tickets.exception.AirportsNotImportedException;
import com.cheapflights.tickets.repository.AirportRepository;
import com.cheapflights.tickets.repository.CityRepository;
import com.cheapflights.tickets.repository.graph.AirportGraphRepository;
import com.cheapflights.tickets.repository.graph.RouteRepository;
import com.cheapflights.tickets.service.mapper.AirportGraphMapper;
import com.cheapflights.tickets.service.mapper.AirportMapper;
import com.cheapflights.tickets.service.mapper.RouteMapper;
import lombok.extern.java.Log;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Service
@Log
public class ImportDataService {

    private final AirportGraphRepository airportGraphRepository;
    private final AirportRepository airportRepository;
    private final RouteRepository routeRepository;
    private final CityRepository cityRepository;
    private final AirportGraphMapper airportGraphMapper;
    private final AirportMapper airportMapper;
    private final RouteMapper routeMapper;
    private final Map<Long, Airport> airportMapByExternalId;
    private final Map<String, Airport> airportMapByIata;
    private final Map<String, Airport> airportMapByIcao;
    private final String DATA_FOLDER = "uploads";

    public ImportDataService(AirportGraphRepository airportGraphRepository, AirportRepository airportRepository, RouteRepository routeRepository, CityRepository cityRepository, AirportGraphMapper airportGraphMapper, AirportMapper airportMapper, RouteMapper routeMapper) {
        this.airportGraphRepository = airportGraphRepository;
        this.airportRepository = airportRepository;
        this.routeRepository = routeRepository;
        this.cityRepository = cityRepository;
        this.airportGraphMapper = airportGraphMapper;
        this.airportMapper = airportMapper;
        this.routeMapper = routeMapper;
        this.airportMapByExternalId = new HashMap<>();
        this.airportMapByIata = new HashMap<>();
        this.airportMapByIcao = new HashMap<>();
    }

    private void loadCitiesAndAirports(Iterable<Airport> graphAirports) {
        log.info("Loading cities...");
        Map<String, List<com.cheapflights.tickets.domain.model.Airport>> citiesWithAirports = new HashMap<>();
        List<City> cities = new ArrayList<>();
        IteratorUtils.toList(graphAirports.iterator()).forEach(graphAirport -> {


            City city = City.builder()
                    .name(graphAirport.getCity())
                    .country(graphAirport.getCountry())
                    .airports(new ArrayList<>())
                    // There is no description of the city in the dataset. Set airport name as description.
                    .description(graphAirport.getName())
                    .build();

            com.cheapflights.tickets.domain.model.Airport airport =
                    com.cheapflights.tickets.domain.model.Airport.builder()
                        .externalId(graphAirport.getAirportExternalId())
                        .name(graphAirport.getName())
                        .build();


            if (citiesWithAirports.containsKey(city.getName())) {
                List<com.cheapflights.tickets.domain.model.Airport> airportList = citiesWithAirports.get(city.getName());
                airportList.add(airport);
            } else {
                cities.add(city);
                List<com.cheapflights.tickets.domain.model.Airport> airportList = new ArrayList<>();
                airportList.add(airport);
                citiesWithAirports.put(city.getName(), airportList);
            }
        });

        Iterable<City> savedCities = cityRepository.saveAll(cities);

        savedCities.iterator().forEachRemaining(city -> {

            List<com.cheapflights.tickets.domain.model.Airport> airports = citiesWithAirports.get(city.getName());
            airports.stream().forEach(airport -> airport.setCity(city));
            airportRepository.saveAll(airports);

        });
        log.info("Successfully loaded cities.");
    }

    /**
     * Saves file to uploads folder in project root.
     *
     * @param file
     * @return Saved file.
     */
    public File saveFile(MultipartFile file) {
        log.info("Saving file to UPLOADS folder.");
        Path path = Paths.get(DATA_FOLDER);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                log.log(Level.SEVERE, String.format("Failed when trying to create %s folder", DATA_FOLDER), e);
                e.printStackTrace();
            }
        }

        try (InputStream inputStream = file.getInputStream()) {
            File newFile = new File(DATA_FOLDER + "/" + file.getOriginalFilename());
            FileUtils.copyInputStreamToFile(inputStream, newFile);
            return newFile;
        } catch (IOException e) {
            log.log(Level.SEVERE, String.format("Saving file to %s folder.", DATA_FOLDER), e);
        }
        throw new RuntimeException("Input file was not successfully saved.");
    }

    /**
     * Deletes a file from uploads folder.
     */
    public void deleteFile(String fileName) {
        log.info(String.format("Deleting file %s from UPLOADS folder.", fileName));
        Path path = Paths.get(String.format("%s/%s", DATA_FOLDER, fileName));
        if (Files.exists(path)) {
            try {
                Files.deleteIfExists(path);
                log.info(String.format("File %s is successfully deleted.", fileName));
            } catch (IOException e) {
                log.severe(String.format("An error occurred when tried to delete a file %s", fileName));
                e.printStackTrace();
            }
        } else {
            log.info("Does not exist!" + path.toString());
        }
    }

    /**
     * Read file, creates Airport object first for graph database, then call loadCitiesAndAirports
     * which will then go through all airports that are saved in graph db, and create entities of Airports and City
     * for relational database.
     */
    @Async
    public CompletableFuture<Void> loadAirportsAndCities(File file) {
        log.info(String.format("Parsing %s", file.getName()));
        double elapsedTimeInSecond;
        try {
            long start = System.nanoTime();
            CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.ORACLE);

            // Load all airports, filter out the ones without airport name, city or country
            List<Airport> collection = parser.getRecords().stream()
                    .parallel()
                    .filter(record -> StringUtils.isNotBlank(record.get(1)) && StringUtils.isNotBlank(record.get(2)) && StringUtils.isNotBlank(record.get(3)))
                    .map(airportGraphMapper::fromCsvRecord)
                    .collect(Collectors.toList());

            Iterable<Airport> airportsGraph = airportGraphRepository.saveAll(collection);

            // Save them in three separate maps which will later be used when loading routes, since some routes don't
            // have an airport id only iata/icao.
            airportsGraph.iterator().forEachRemaining(airport -> {
                airportMapByExternalId.put(airport.getAirportExternalId(), airport);
                if (airport.getIata() != null) {
                    airportMapByIata.put(airport.getIata(), airport);
                }

                if (airport.getIcao() != null) {
                    airportMapByIcao.put(airport.getIcao(), airport);
                }

            });
            loadCitiesAndAirports(airportsGraph);
            long end = System.nanoTime();
            elapsedTimeInSecond = (double) (end - start) / 1_000_000_000;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't instantiate csv parser.", e);
        }

        log.info(String.format("Successfully loaded airports in %s seconds.", elapsedTimeInSecond));
        deleteFile(file.getName());
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> loadRoutes(File file) {
        if (airportGraphRepository.count() == 0) {
            throw new AirportsNotImportedException("Please upload airports before routes.");
        }

        log.info(String.format("Parsing %s", file.getName()));

        List<Route> routes;
        long start = System.nanoTime();
        try {
            CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.ORACLE);
            routes = parser.getRecords().stream()
                    .parallel()
                    .map(routeMapper::fromCsvRecord)
                    .peek(this::assignAirportsToRoute)
                    .filter(route -> route.getDestination() != null && route.getSource() != null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't instantiate csv parser.", e);
        }

        log.info(String.format("Saving %d routes to database.", routes.size()));
        routeRepository.saveAll(routes);

        long end = System.nanoTime();
        double elapsedTimeInSecond = (double) (end - start) / 1_000_000_000;
        log.info(String.format("Done loading routes in %s seconds.", elapsedTimeInSecond));
        deleteFile(file.getName());
        return CompletableFuture.completedFuture(null);
    }

    private void assignAirportsToRoute(Route route) {
        Airport destinationAirport = getAirportFromMapByIdOrIataIcao(route.getDestinationAirportId(), route.getDestinationAirport());
        Airport sourceAirport = getAirportFromMapByIdOrIataIcao(route.getSourceAirportId(), route.getSourceAirport());
        if (destinationAirport != null && sourceAirport != null) {
            route.setSource(sourceAirport);
            route.setDestination(destinationAirport);
        } else {
            log.warning(String.format("Couldn't find source/destination Airport. Skipping route with [%s].", route.toString()));
        }
    }

    private Airport getAirportFromMapByIdOrIataIcao(Long airportExternalId, String iataIcao) {
        Airport airport = null;
        if (airportExternalId != null) {
            airport = airportMapByExternalId.get(airportExternalId);
        } else if (iataIcao != null) {
            airport = airportMapByIata.get(iataIcao);
            if (airport == null) {
                airport = airportMapByIcao.get(iataIcao);
            }
        }
        return airport;
    }

}
