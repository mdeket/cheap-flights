package com.cheapflights.tickets.service;

import com.cheapflights.tickets.domain.model.City;
import com.cheapflights.tickets.domain.model.User;
import com.cheapflights.tickets.domain.model.graph.Airport;
import com.cheapflights.tickets.domain.model.graph.Route;
import com.cheapflights.tickets.repository.CityRepository;
import com.cheapflights.tickets.repository.UserRepository;
import com.cheapflights.tickets.repository.graph.AirportRepository;
import com.cheapflights.tickets.repository.graph.RouteRepository;
import lombok.extern.java.Log;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log
public class ImportDataService implements CommandLineRunner {

    private final AirportRepository airportRepository;
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final AirportMapper airportMapper;
    private final RouteMapper routeMapper;
    private final Map<Long, Airport> airportMapByExternalId;
    private final Map<String, Airport> airportMapByIata;
    private final Map<String, Airport> airportMapByIcao;

    public ImportDataService(AirportRepository airportRepository, RouteRepository routeRepository, UserRepository userRepository, CityRepository cityRepository, AirportMapper airportMapper, RouteMapper routeMapper) {
        this.airportRepository = airportRepository;
        this.routeRepository = routeRepository;
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
        this.airportMapper = airportMapper;
        this.routeMapper = routeMapper;
        this.airportMapByExternalId = new HashMap<>();
        this.airportMapByIata = new HashMap<>();
        this.airportMapByIcao = new HashMap<>();
    }

    @Override
    public void run(String... args) {
        log.info("Started importing data.");
        cityRepository.deleteAll();
        airportRepository.deleteAll();
        routeRepository.deleteAll();
        loadAirports();
        loadRoutes();
        loadCities(airportRepository.findAll());
        loadUser();
        log.info("Finished importing data.");
    }

    private void loadUser() {
        User user = new User();
        user.setUsername("miland");
        user.setPassword("test");
        userRepository.save(user);
    }


    private void loadCities(Iterable<Airport> airports) {
        log.info("Loading cities...");
        Set<City> cities = new HashSet<>();
        IteratorUtils.toList(airports.iterator()).forEach(airport -> {
            City city = City.builder()
                    .name(airport.getCity())
                    .country(airport.getCountry())

                    // There is no description of the city in the dataset. Set airport name as description.
                    .description(airport.getName())
                    .build();
            cities.add(city);
        });

        cityRepository.saveAll(cities);
        log.info("Successfully loaded cities.");
    }

    public void loadAirports() {
        log.info("Loading airports...");
        File airportsFile = loadFile("classpath:airports.txt");

        log.info("Parsing airports.txt.");
        double elapsedTimeInSecond;
        try {
            long start = System.nanoTime();
            CSVParser parser = CSVParser.parse(airportsFile, Charset.defaultCharset(), CSVFormat.ORACLE);

            // Load all airports, filter out the ones without airport name, city or country
            List<Airport> collection = parser.getRecords().stream()
                    .parallel()
                    .filter(record -> StringUtils.isNotBlank(record.get(1)) && StringUtils.isNotBlank(record.get(2)) && StringUtils.isNotBlank(record.get(3)))
                    .map(airportMapper::fromCsvRecord)
                    .collect(Collectors.toList());

            Iterable<Airport> airports = airportRepository.saveAll(collection);

            // Save them in three separate maps which will later be used when loading routes, since some routes don't
            // have an airport id only iata/icao.
            airports.iterator().forEachRemaining(airport -> {
                airportMapByExternalId.put(airport.getAirportExternalId(), airport);
                if (airport.getIata() != null) {
                    airportMapByIata.put(airport.getIata(), airport);
                }

                if (airport.getIcao() != null) {
                    airportMapByIcao.put(airport.getIcao(), airport);
                }
            });
            long end = System.nanoTime();
            elapsedTimeInSecond = (double) (end - start) / 1_000_000_000;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't instantiate csv parser.", e);
        }

        log.info(String.format("Successfully loaded airports in %s seconds.", elapsedTimeInSecond));
    }

    public void loadRoutes() {
        log.info("Loading routes...");
        File routesFile = loadFile("classpath:routes.txt");
        List<Route> routes = new ArrayList<>();

        long start = System.nanoTime();
        try {
            CSVParser parser = CSVParser.parse(routesFile, Charset.defaultCharset(), CSVFormat.ORACLE);
            routes = parser.getRecords().parallelStream()
                    .parallel()
                    .map(routeMapper::fromCsvRecord)
                    .peek(this::assignAirportsToRoute)
                    .filter(route -> route.getDestination() != null && route.getSource() != null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info(String.format("Saving %d routes to database.", routes.size()));
        routeRepository.saveAll(routes);

        long end = System.nanoTime();
        double elapsedTimeInSecond = (double) (end - start) / 1_000_000_000;
        log.info(String.format("Done loading routes in %s seconds.", elapsedTimeInSecond));
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

    private File loadFile(String path) {
        try {
            return ResourceUtils.getFile(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("%s can't be found.", path), e);
        }
    }

}
