package com.cheapflights.tickets.service;

import com.cheapflights.tickets.domain.dto.UserDTO;
import com.cheapflights.tickets.domain.model.City;
import com.cheapflights.tickets.domain.model.User;
import com.cheapflights.tickets.domain.model.graph.Airport;
import com.cheapflights.tickets.domain.model.graph.Route;
import com.cheapflights.tickets.exception.AirportsNotImportedException;
import com.cheapflights.tickets.repository.CityRepository;
import com.cheapflights.tickets.repository.CommentRepository;
import com.cheapflights.tickets.repository.UserRepository;
import com.cheapflights.tickets.repository.graph.AirportRepository;
import com.cheapflights.tickets.repository.graph.RouteRepository;
import com.cheapflights.tickets.service.mapper.AirportMapper;
import com.cheapflights.tickets.service.mapper.RouteMapper;
import lombok.extern.java.Log;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final UserService userService;
    private final CityRepository cityRepository;
    private final CommentRepository commentRepository;
    private final AirportMapper airportMapper;
    private final RouteMapper routeMapper;
    private final Map<Long, Airport> airportMapByExternalId;
    private final Map<String, Airport> airportMapByIata;
    private final Map<String, Airport> airportMapByIcao;

    public ImportDataService(AirportRepository airportRepository, RouteRepository routeRepository, UserRepository userRepository, UserService userService, CityRepository cityRepository, CommentRepository commentRepository, AirportMapper airportMapper, RouteMapper routeMapper) {
        this.airportRepository = airportRepository;
        this.routeRepository = routeRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.cityRepository = cityRepository;
        this.commentRepository = commentRepository;
        this.airportMapper = airportMapper;
        this.routeMapper = routeMapper;
        this.airportMapByExternalId = new HashMap<>();
        this.airportMapByIata = new HashMap<>();
        this.airportMapByIcao = new HashMap<>();
    }

    @Override
    public void run(String... args) {
        log.info("Started importing data.");
        userRepository.deleteAll();
        cityRepository.deleteAll();
        airportRepository.deleteAll();
        routeRepository.deleteAll();
//        loadAirports();
//        loadRoutes();
//        loadCities(airportRepository.findAll());
//
        User author = loadUser();
//        City city = IteratorUtils.toList(cityRepository.findAll().iterator()).stream().findFirst().get();
//        Comment comment = Comment.builder()
//                .city(city)
//                .author(author)
//                .text("Lorem ipsum")
//                .build();
//        commentRepository.save(comment);


        log.info("Finished importing data.");
    }

    private User loadUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("miland");
        userDTO.setPassword("test");
        return userService.createUser(userDTO);
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

    public void loadAirports(MultipartFile file) {
        log.info(String.format("Parsing %s", file.getName()));
        double elapsedTimeInSecond;
        try {
            long start = System.nanoTime();
            CSVParser parser = CSVParser.parse(file.getInputStream(), Charset.defaultCharset(), CSVFormat.ORACLE);

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
            loadCities(airports);
            long end = System.nanoTime();
            elapsedTimeInSecond = (double) (end - start) / 1_000_000_000;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't instantiate csv parser.", e);
        }

        log.info(String.format("Successfully loaded airports in %s seconds.", elapsedTimeInSecond));
    }

    public void loadRoutes(MultipartFile file) {
        if(airportRepository.count() == 0) {
            throw new AirportsNotImportedException("Please upload airports before routes.");
        }
        log.info(String.format("Parsing %s", file.getName()));
        List<Route> routes = new ArrayList<>();

        long start = System.nanoTime();
        try {
            CSVParser parser = CSVParser.parse(file.getInputStream(), Charset.defaultCharset(), CSVFormat.ORACLE);
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

}
