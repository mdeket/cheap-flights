package com.cheapflights.tickets.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "jwt")
@Configuration
@Data
public class JwtConfig {
    private Long tokenValidity;
    private String authoritiesKey;
    private String secret;
}
