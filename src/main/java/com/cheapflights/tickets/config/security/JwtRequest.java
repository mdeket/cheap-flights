package com.cheapflights.tickets.config.security;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class JwtRequest {

    @NotNull
    @Size(min = 4, max = 50)
    private String username;

    @NotNull
    @Size(min = 4, max = 128)
    private String password;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}