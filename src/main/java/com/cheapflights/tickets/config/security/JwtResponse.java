package com.cheapflights.tickets.config.security;

import lombok.Data;

import java.io.Serializable;

@Data
public class JwtResponse implements Serializable {
    private final String accessToken;

    public JwtResponse(String accessToken) {
        this.accessToken = accessToken;
    }

}