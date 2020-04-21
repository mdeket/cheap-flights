package com.cheapflights.tickets.controller;

import com.cheapflights.tickets.config.security.JwtRequest;
import com.cheapflights.tickets.config.security.JwtResponse;
import com.cheapflights.tickets.config.security.TokenProvider;
import com.cheapflights.tickets.domain.dto.UserDTO;
import com.cheapflights.tickets.domain.model.User;
import com.cheapflights.tickets.service.UserService;
import com.cheapflights.tickets.service.mapper.UserMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserService userService, UserMapper userMapper) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();

        // TODO: To constants
        httpHeaders.add("Authorization", "Bearer " + jwt);
        return new ResponseEntity<>(new JwtResponse(jwt), httpHeaders, HttpStatus.OK);
    }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        User user = userService.save(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDTO(user));
    }
}
