package com.example.generator.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Deprecated
public class JdbcConfig {

    private String URL;
    private String PORT;
    private String USERNAME;
    private String PASSWORD;
    private String DATABASE;
    private String DRIVER;

    public DataSource dataSource(){
        var url = String.format("jdbc:postgresql://%s:%s/%s",URL,PORT,DATABASE);
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DRIVER);
        dataSource.setUrl(url);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        return dataSource;
    }
    public JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(dataSource());
    }


}
