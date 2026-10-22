package com.webchat.backend.config;

import java.net.URI;
import java.net.URISyntaxException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.zaxxer.hikari.HikariDataSource;

@Configuration(proxyBeanMethods = false)
public class PostgresDataSourceConfiguration {

    private final Environment environment;

    public PostgresDataSourceConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    DataSource dataSource() {
        String configuredUrl = environment.getRequiredProperty("spring.datasource.url");
        HikariDataSource dataSource = new HikariDataSource();

        if (configuredUrl.startsWith("postgresql://") || configuredUrl.startsWith("postgres://")) {
            configureFromPostgresUri(dataSource, configuredUrl);
        } else {
            dataSource.setJdbcUrl(configuredUrl);
            dataSource.setUsername(environment.getProperty("spring.datasource.username"));
            dataSource.setPassword(environment.getProperty("spring.datasource.password"));
        }

        return dataSource;
    }

    private void configureFromPostgresUri(HikariDataSource dataSource, String configuredUrl) {
        URI uri;
        try {
            uri = new URI(configuredUrl);
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException("POSTGRESQL_CONNECTION is not a valid PostgreSQL URI", exception);
        }

        String authority = uri.getRawAuthority();
        if (uri.getHost() == null || authority == null) {
            throw new IllegalArgumentException("POSTGRESQL_CONNECTION must include a PostgreSQL host");
        }

        String userInfo = uri.getUserInfo();
        String[] credentials = userInfo == null ? new String[0] : userInfo.split(":", 2);
        String username = environment.getProperty("POSTGRESQL_USERNAME");
        String password = environment.getProperty("POSTGRESQL_PASSWORD");

        if (username == null && credentials.length > 0) {
            username = credentials[0];
        }
        if (password == null && credentials.length > 1) {
            password = credentials[1];
        }

        String jdbcAuthority = authority.contains("@")
                ? authority.substring(authority.indexOf('@') + 1)
                : authority;
        StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
                .append(jdbcAuthority);
        if (uri.getRawPath() != null) {
            jdbcUrl.append(uri.getRawPath());
        }
        if (uri.getRawQuery() != null) {
            jdbcUrl.append('?').append(uri.getRawQuery());
        }
        dataSource.setJdbcUrl(jdbcUrl.toString());
        dataSource.setUsername(username);
        dataSource.setPassword(password);
    }
}
