package com.cheapflights.tickets.service;

import com.cheapflights.tickets.domain.dto.CityDTO;
import com.cheapflights.tickets.domain.dto.CommentDTO;
import com.cheapflights.tickets.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service

public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public Collection<CityDTO> findAll(Integer numberOfComments) {

        Map<Long, CityDTO> cityWithComments = new HashMap<>();
        cityRepository.findAllWithComments(numberOfComments)
                .forEach(tuple -> {
                    long cityId = tuple.get("id", Number.class).longValue();
                    CityDTO cityDTO = CityDTO.builder()
                            .id(cityId)
                            .country(tuple.get("country", String.class))
                            .name(tuple.get("name", String.class))
                            .comments(new ArrayList<>())
                            .description(tuple.get("description", String.class))
                            .build();

                    Timestamp createdAt = tuple.get("createdAt", Timestamp.class);
                    Timestamp modifiedAt = tuple.get("modifiedAt", Timestamp.class);
                    Number commentId = tuple.get("commentId", Number.class);

                    CommentDTO commentDTO = CommentDTO.builder()
                            .id(commentId != null ? commentId.longValue() : null)
                            .text(tuple.get("text", String.class))
                            .createdAt(createdAt != null ? createdAt.toLocalDateTime() : null)
                            .modifiedAt(modifiedAt != null ? modifiedAt.toLocalDateTime() : null)
                            .build();

                    if (!cityWithComments.containsKey(cityId)) {
                        cityDTO.getComments().add(commentDTO);
                        cityWithComments.put(cityId, cityDTO);
                    } else {
                        CityDTO existingCity = cityWithComments.get(cityId);
                        existingCity.getComments().add(commentDTO);
                        cityWithComments.replace(cityId, existingCity);
                    }
                });

        return cityWithComments.values();
    }
}
