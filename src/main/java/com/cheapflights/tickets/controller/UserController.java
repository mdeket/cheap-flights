package com.cheapflights.tickets.controller;

import com.cheapflights.tickets.config.security.JwtRequest;
import com.cheapflights.tickets.config.security.JwtResponse;
import com.cheapflights.tickets.config.security.JwtTokenUtil;
import com.cheapflights.tickets.config.security.JwtUserDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailService userDetailsService;

    public UserController(JwtTokenUtil jwtTokenUtil, JwtUserDetailService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
