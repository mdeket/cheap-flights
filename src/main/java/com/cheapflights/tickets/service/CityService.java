package com.cheapflights.tickets.service;

import com.cheapflights.tickets.domain.dto.CityDTO;
import com.cheapflights.tickets.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public Collection<CityDTO> findAll(Integer numberOfComments) {
        return null;
    }
}
