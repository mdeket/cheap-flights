package com.cheapflights.tickets.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ErrorDTO {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private String message;

    public ErrorDTO(String message) {
        this.message = message;
    }
}
