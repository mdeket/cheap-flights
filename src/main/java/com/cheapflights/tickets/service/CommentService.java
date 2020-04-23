package com.cheapflights.tickets.service;

import com.cheapflights.tickets.domain.dto.CommentDTO;
import com.cheapflights.tickets.domain.model.City;
import com.cheapflights.tickets.domain.model.Comment;
import com.cheapflights.tickets.domain.model.User;
import com.cheapflights.tickets.exception.UpdateEntityException;
import com.cheapflights.tickets.repository.CommentRepository;
import com.cheapflights.tickets.service.mapper.CommentMapper;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final CityService cityService;

    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper, UserService userService, CityService cityService) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.userService = userService;
        this.cityService = cityService;
    }

    public CommentDTO save(CommentDTO commentDTO, Long userId) {
        City city = cityService.findOne(commentDTO.getCity());

        User user = userService.findById(userId);

        Comment comment = Comment.builder()
                .author(user)
                .text(commentDTO.getText())
                .createdAt(commentDTO.getCreatedAt())
                .modifiedAt(commentDTO.getModifiedAt())
                .city(city)
                .build();

        if (commentDTO.getId() != null) {
            comment.setId(commentDTO.getId());
        }

        return commentMapper.toDTO(commentRepository.save(comment));
    }

    public CommentDTO update(CommentDTO commentDTO, Long userId) {
        if (commentDTO.getId() == null) {
            throw new UpdateEntityException("Can't update comment with id missing.");
        }
        Comment comment = findById(commentDTO.getId());
        commentDTO.setCreatedAt(comment.getCreatedAt());
        commentDTO.setModifiedAt(comment.getModifiedAt());
        return save(commentDTO, userId);
    }

    public void deleteComment(Long id) {
        Comment comment = findById(id);
        commentRepository.delete(comment);
    }

    public Comment findById(Long id) {
        Optional<Comment> comment = commentRepository.findById(id);

        if (comment.isEmpty()) {
            throw new EntityNotFoundException(String.format("comment with id [%d] couldn't be found.", id));
        }
        return comment.get();
    }

    public Collection<Comment> findAll() {
        return IteratorUtils.toList(commentRepository.findAll().iterator());
    }

}
