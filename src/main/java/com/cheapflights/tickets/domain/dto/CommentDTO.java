package com.cheapflights.tickets.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDTO {
    private Long id;
    private Long author;
    private Long city;
    private String text;
    private LocalDateTime timestamp;
}
