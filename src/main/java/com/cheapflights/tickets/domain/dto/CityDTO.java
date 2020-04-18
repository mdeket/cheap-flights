package com.cheapflights.tickets.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CityDTO {
    private Long id;
    private String name;
    private String description;
}
