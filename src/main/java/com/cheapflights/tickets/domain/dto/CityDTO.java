package com.cheapflights.tickets.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CityDTO {
    private Long id;
    private String name;
    private String country;
    private String description;
    private List<CommentDTO> comments;
    private List<AirportDTO> airports;
}
