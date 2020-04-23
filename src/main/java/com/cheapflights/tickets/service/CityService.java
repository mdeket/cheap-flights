package com.cheapflights.tickets.service;

import com.cheapflights.tickets.domain.dto.AirportDTO;
import com.cheapflights.tickets.domain.dto.CityDTO;
import com.cheapflights.tickets.domain.dto.CommentDTO;
import com.cheapflights.tickets.repository.CityRepository;
import com.cheapflights.tickets.service.mapper.AirportMapper;
import com.cheapflights.tickets.service.mapper.CityMapper;
import com.cheapflights.tickets.service.mapper.CommentMapper;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.*;

@Service
public class CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final CommentMapper commentMapper;
    private final AirportMapper airportMapper;
    private final EntityManager entityManager;

    public CityService(CityRepository cityRepository, CityMapper cityMapper, CommentMapper commentMapper, AirportMapper airportMapper, EntityManager entityManager) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
        this.commentMapper = commentMapper;
        this.airportMapper = airportMapper;
        this.entityManager = entityManager;
    }

    public Collection<CityDTO> findAll(Optional<Integer> numberOfComments, String nameLike) {

        List<Tuple> data = numberOfComments.map(comments -> cityRepository.findAllWithCommentsWithCommentLimit(comments, nameLike))
                .orElseGet(() -> cityRepository.findAllWithComments(nameLike));

        Map<Long, CityDTO> cityWithComments = new HashMap<>();
//        cityRepository.findAllWithComments(numberOfComments, nameLike)
        data
                .forEach(tuple -> {
                    CityDTO cityDTO = cityMapper.toDTO(tuple);
                    CommentDTO commentDTO = commentMapper.toDTO(tuple);
                    AirportDTO airportDTO = airportMapper.toDTO(tuple);

                    if (!cityWithComments.containsKey(cityDTO.getId())) {
                        if (commentDTO.getId() != null && !cityDTO.getComments().contains(commentDTO)) {
                            cityDTO.getComments().add(commentDTO);
                        }
                        if (airportDTO.getExternalId() != null && !cityDTO.getAirports().contains(airportDTO)) {
                            cityDTO.getAirports().add(airportDTO);
                        }
                        cityWithComments.put(cityDTO.getId(), cityDTO);
                    } else {
                        CityDTO existingCity = cityWithComments.get(cityDTO.getId());
                        existingCity.getComments().add(commentDTO);
                        existingCity.getAirports().add(airportDTO);
                        cityWithComments.replace(cityDTO.getId(), existingCity);
                    }
                });
        return cityWithComments.values();
    }

}
