package com.webchat.backend.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import com.zaxxer.hikari.HikariDataSource;

class PostgresDataSourceConfigurationTests {

    @Test
    void convertsProviderUriToJdbcUrlAndDecodesCredentials() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("spring.datasource.url",
                        "postgresql://chat%40user:p%40ss%3Aword@db.example.test:5432/chat"
                                + "?sslmode=require&channel_binding=require");

        try (HikariDataSource dataSource =
                (HikariDataSource) new PostgresDataSourceConfiguration(environment).dataSource()) {
            assertEquals("jdbc:postgresql://db.example.test:5432/chat"
                    + "?sslmode=require&channel_binding=require", dataSource.getJdbcUrl());
            assertEquals("chat@user", dataSource.getUsername());
            assertEquals("p@ss:word", dataSource.getPassword());
        }
    }

    @Test
    void separateCredentialsOverrideCredentialsInProviderUri() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("spring.datasource.url", "postgresql://uri-user:uri-pass@db.example.test/chat")
                .withProperty("POSTGRESQL_USERNAME", "configured-user")
                .withProperty("POSTGRESQL_PASSWORD", "configured-pass");

        try (HikariDataSource dataSource =
                (HikariDataSource) new PostgresDataSourceConfiguration(environment).dataSource()) {
            assertEquals("configured-user", dataSource.getUsername());
            assertEquals("configured-pass", dataSource.getPassword());
        }
    }
}
