package com.cheapflights.tickets.service;

import com.cheapflights.tickets.domain.dto.CommentDTO;
import com.cheapflights.tickets.domain.model.City;
import com.cheapflights.tickets.domain.model.Comment;
import com.cheapflights.tickets.domain.model.User;
import com.cheapflights.tickets.repository.CityRepository;
import com.cheapflights.tickets.repository.CommentRepository;
import com.cheapflights.tickets.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
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

    public CommentDTO save(CommentDTO commentDTO, Long userId, Long cityId) {
        Optional<City> city = cityRepository.findById(cityId);
        if (city.isEmpty()) {
            throw new EntityNotFoundException(String.format("City with id [%d] couldn't be found.", cityId));
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException(String.format("User with id [%d] couldn't be found.", cityId));
        }

        Comment comment = Comment.builder()
                .author(user.get())
                .text(commentDTO.getText())
                .city(city.get())
                .timestamp(commentDTO.getTimestamp())
                .build();
        return commentMapper.toDTO(commentRepository.save(comment));
    }
}
