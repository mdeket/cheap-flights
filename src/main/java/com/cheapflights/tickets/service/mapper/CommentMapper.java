package com.cheapflights.tickets.service.mapper;

import com.cheapflights.tickets.domain.dto.CommentDTO;
import com.cheapflights.tickets.domain.model.Comment;
import org.springframework.stereotype.Component;

import javax.persistence.Tuple;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {

    public CommentDTO toDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(comment.getAuthor().getId())
                .city(comment.getCity().getId())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build();
    }

    public CommentDTO toDTO(Tuple tuple) {

        Timestamp createdAt = tuple.get("createdAt", Timestamp.class);
        Timestamp modifiedAt = tuple.get("modifiedAt", Timestamp.class);
        Number commentId = tuple.get("commentId", Number.class);

        return CommentDTO.builder()
                .id(commentId != null ? commentId.longValue() : null)
                .text(tuple.get("text", String.class))
                .createdAt(createdAt != null ? createdAt.toLocalDateTime() : null)
                .modifiedAt(modifiedAt != null ? modifiedAt.toLocalDateTime() : null)
                .build();
    }

    public List<CommentDTO> toDTO(List<Comment> comments) {
        return comments.stream().map(this::toDTO).collect(Collectors.toList());
    }

}
