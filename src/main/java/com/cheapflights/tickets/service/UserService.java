package com.cheapflights.tickets.service;

import com.cheapflights.tickets.domain.dto.UserDTO;
import com.cheapflights.tickets.domain.model.User;
import com.cheapflights.tickets.repository.UserRepository;
import com.cheapflights.tickets.service.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User save(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);

        // TODO: use salt
        user.setSalt("123asdf`1asd");

        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encryptedPassword);
        return userRepository.save(user);
    }
}
