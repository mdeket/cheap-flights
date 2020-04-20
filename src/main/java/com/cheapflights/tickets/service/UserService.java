package com.cheapflights.tickets.service;

import com.cheapflights.tickets.config.security.AuthorityConstants;
import com.cheapflights.tickets.domain.dto.UserDTO;
import com.cheapflights.tickets.domain.model.User;
import com.cheapflights.tickets.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername().toLowerCase());
        user.setFirstName("Milan");
        user.setLastName("Deket");
        user.setSalt("123asdf`1asd");

        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encryptedPassword);
        user.setRole(AuthorityConstants.ROLE_ADMIN);
        userRepository.save(user);
        return user;
    }
}
