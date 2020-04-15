package com.cheapflights.tickets.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableNeo4jRepositories(basePackages = "com.cheapflights.tickets.repository.graph")
@EnableTransactionManagement
public class GraphDbConfig {}