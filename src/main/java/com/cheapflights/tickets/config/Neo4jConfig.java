package com.cheapflights.tickets.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "neo4j")
@Configuration
@Data
public class Neo4jConfig {
    private String username;
    private String password;
    private String uri;
}
