package com.cheapflights.tickets.controller;

import com.cheapflights.tickets.domain.dto.CommentDTO;
import com.cheapflights.tickets.domain.model.Comment;
import com.cheapflights.tickets.service.CommentMapper;
import com.cheapflights.tickets.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    public CommentController(CommentService commentService, CommentMapper commentMapper) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    @PostMapping
    public ResponseEntity<CommentDTO> addComment(@RequestBody CommentDTO commentDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.save(commentDTO, 1l));
    }

    @GetMapping
    public ResponseEntity<Collection<CommentDTO>> getAllComments() {
        List<Comment> comments = new ArrayList<>(commentService.findAll());
        return ResponseEntity.ok(commentMapper.toDTO(comments));
    }

}
