package com.cheapflights.tickets.config.security;

import java.io.Serializable;

public class JwtResponse implements Serializable {
    private final String jwttoken;

    public JwtResponse(String accessToken) {
        this.jwttoken = accessToken;
    }

}