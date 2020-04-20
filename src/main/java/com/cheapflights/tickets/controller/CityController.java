package com.cheapflights.tickets.controller;

import com.cheapflights.tickets.domain.dto.CityDTO;
import com.cheapflights.tickets.domain.model.City;
import com.cheapflights.tickets.repository.CityRepository;
import com.cheapflights.tickets.service.CityService;
import com.cheapflights.tickets.service.mapper.CityMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/cities")
public class CityController {

    private final CityRepository cityRepository;
    private final CityService cityService;
    private final CityMapper cityMapper;

    public CityController(CityRepository cityRepository, CityService cityService, CityMapper cityMapper) {
        this.cityRepository = cityRepository;
        this.cityService = cityService;
        this.cityMapper = cityMapper;
    }

    @GetMapping
    public ResponseEntity<Collection<CityDTO>> getAllCities(@RequestParam(required = false) Optional<Integer> comments,
                                                            @RequestParam(required = false, defaultValue = "") String name) {
        Collection<CityDTO> cities = comments.map(numberOfComments -> cityService.findAll(numberOfComments, name))
                .orElse(cityService.findAll());
        return ResponseEntity.ok().body(cities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CityDTO> getOneCity(@PathVariable Long id) {
        return cityRepository.findById(id)
                .map(city -> ResponseEntity.ok(cityMapper.toDTO(city)))
                .orElseThrow(EntityNotFoundException::new);
    }

    @PostMapping
    public ResponseEntity<CityDTO> addCity(@RequestBody CityDTO cityDTO) {
        final City city = cityMapper.toEntity(cityDTO);
        City persistedCity = cityRepository.save(city);
        return ResponseEntity.status(HttpStatus.CREATED).body(cityMapper.toDTO(persistedCity));
    }
}
