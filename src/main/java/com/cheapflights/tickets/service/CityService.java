package com.cheapflights.tickets.service;

import com.cheapflights.tickets.domain.dto.CityDTO;
import com.cheapflights.tickets.domain.dto.CommentDTO;
import com.cheapflights.tickets.repository.CityRepository;
import com.cheapflights.tickets.service.mapper.CityMapper;
import com.cheapflights.tickets.service.mapper.CommentMapper;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final CommentMapper commentMapper;

    public CityService(CityRepository cityRepository, CityMapper cityMapper, CommentMapper commentMapper) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
        this.commentMapper = commentMapper;
    }

    public Collection<CityDTO> findAll(Integer numberOfComments) {
        Map<Long, CityDTO> cityWithComments = new HashMap<>();
        cityRepository.findAllWithComments(numberOfComments)
                .forEach(tuple -> {
                    CityDTO cityDTO = cityMapper.toDTO(tuple);
                    CommentDTO commentDTO = commentMapper.toDTO(tuple);

                    if (!cityWithComments.containsKey(cityDTO.getId())) {
                        cityDTO.getComments().add(commentDTO);
                        cityWithComments.put(cityDTO.getId(), cityDTO);
                    } else {
                        CityDTO existingCity = cityWithComments.get(cityDTO.getId());
                        existingCity.getComments().add(commentDTO);
                        cityWithComments.replace(cityDTO.getId(), existingCity);
                    }
                });
        return cityWithComments.values();
    }
}
