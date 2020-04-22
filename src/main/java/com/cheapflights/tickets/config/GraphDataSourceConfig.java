package com.cheapflights.tickets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableNeo4jRepositories(basePackages = "com.cheapflights.tickets.repository.graph")
@EnableTransactionManagement
public class GraphDataSourceConfig {

    private final Neo4jConfig neo4jConfig;

    public GraphDataSourceConfig(Neo4jConfig neo4jConfig) {
        this.neo4jConfig = neo4jConfig;
    }

    @Bean
    public org.neo4j.ogm.config.Configuration configuration() {
        return new org.neo4j.ogm.config.Configuration.Builder()
                .credentials(neo4jConfig.getUsername(), neo4jConfig.getPassword())
                .uri(neo4jConfig.getUri())
                .build();
    }

}