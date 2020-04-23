package com.cheapflights.tickets.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AirportDTO {
    private Long externalId;
    private String name;
}
