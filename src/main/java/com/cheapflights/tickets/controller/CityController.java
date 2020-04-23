package com.cheapflights.tickets.controller;

import com.cheapflights.tickets.domain.dto.CityDTO;
import com.cheapflights.tickets.domain.model.City;
import com.cheapflights.tickets.service.CityService;
import com.cheapflights.tickets.service.mapper.CityMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/cities")
public class CityController {

    private final CityService cityService;
    private final CityMapper cityMapper;

    public CityController(CityService cityService, CityMapper cityMapper) {
        this.cityService = cityService;
        this.cityMapper = cityMapper;
    }

    @GetMapping
    public ResponseEntity<Collection<CityDTO>> getAllCities(@RequestParam(required = false) Optional<Integer> comments,
                                                            @RequestParam(required = false, defaultValue = "") String name) {
        return ResponseEntity.ok().body(cityService.findAll(comments, name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CityDTO> getOneCity(@PathVariable Long id) {
        City city = cityService.findOne(id);
        return ResponseEntity.ok(cityMapper.toDTO(city));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<CityDTO> addCity(@RequestBody CityDTO cityDTO) {
        City city = cityService.save(cityDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cityMapper.toDTO(city));
    }
}
