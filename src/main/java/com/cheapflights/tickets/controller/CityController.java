package com.cheapflights.tickets.controller;

import com.cheapflights.tickets.domain.dto.CityDTO;
import com.cheapflights.tickets.domain.model.City;
import com.cheapflights.tickets.repository.CityRepository;
import com.cheapflights.tickets.service.CityMapper;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;

@RestController
@RequestMapping("/api/v1/cities")
public class CityController {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    public CityController(CityRepository cityRepository, CityMapper cityMapper) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
    }

    @GetMapping
    public ResponseEntity<Collection<CityDTO>> getAllCities() {
        Collection<City> cities = IteratorUtils.toList(cityRepository.findAll().iterator());
        return ResponseEntity.ok().body(cityMapper.toDTO(cities));
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
