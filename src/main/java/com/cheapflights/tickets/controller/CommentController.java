package com.cheapflights.tickets.controller;

import com.cheapflights.tickets.domain.dto.CommentDTO;
import com.cheapflights.tickets.domain.model.Comment;
import com.cheapflights.tickets.service.CommentService;
import com.cheapflights.tickets.service.mapper.CommentMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
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

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.findById(id);
        return ResponseEntity.ok(commentMapper.toDTO(comment));
    }

    @GetMapping
    public ResponseEntity<Collection<CommentDTO>> getAllComments() {
        List<Comment> comments = new ArrayList<>(commentService.findAll());
        return ResponseEntity.ok(commentMapper.toDTO(comments));
    }

    @PostMapping
    public ResponseEntity<CommentDTO> addComment(@RequestBody CommentDTO commentDTO, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.save(commentDTO, user));
    }

    @PutMapping
    public ResponseEntity<CommentDTO> updateComment(@RequestBody CommentDTO commentDTO, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(commentService.update(commentDTO, user));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

}
