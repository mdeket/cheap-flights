package com.cheapflights.tickets.service;

import com.cheapflights.tickets.domain.dto.CommentDTO;
import com.cheapflights.tickets.domain.model.City;
import com.cheapflights.tickets.domain.model.Comment;
import com.cheapflights.tickets.domain.model.User;
import com.cheapflights.tickets.exception.UpdateEntityException;
import com.cheapflights.tickets.repository.CityRepository;
import com.cheapflights.tickets.repository.CommentRepository;
import com.cheapflights.tickets.repository.UserRepository;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;

    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper, UserRepository userRepository, CityRepository cityRepository) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
    }

    public CommentDTO save(CommentDTO commentDTO, Long userId) {
        Optional<City> city = cityRepository.findById(commentDTO.getCity());
        if (city.isEmpty()) {
            throw new EntityNotFoundException(String.format("City with id [%d] couldn't be found.", commentDTO.getCity()));
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException(String.format("User with id [%d] couldn't be found.", userId));
        }

        Comment comment = Comment.builder()
                .author(user.get())
                .text(commentDTO.getText())
                .createdAt(commentDTO.getCreatedAt())
                .modifiedAt(commentDTO.getModifiedAt())
                .city(city.get())
                .build();

        if(commentDTO.getId() != null) {
            comment.setId(commentDTO.getId());
        }

        return commentMapper.toDTO(commentRepository.save(comment));
    }

    public CommentDTO update(CommentDTO commentDTO, Long userId) {
        if(commentDTO.getId() == null) {
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
