package com.starchat.configuration;

import com.intersystems.jdbc.IRISConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class Config {

    @Value("#{systemEnvironment['DATABASE_CONNECTION_URI']}")
    private String connectionUri;

    @Value("#{systemEnvironment['DATABASE_USER']}")
    private String databaseUser;

    @Value("#{systemEnvironment['DATABASE_PASSWORD']}")
    private String databasePassword;

    @Bean
    public IRISConnection getIrisConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.intersystems.jdbc.IRISDriver");
        return (IRISConnection) DriverManager.getConnection(connectionUri, databaseUser, databasePassword);
    }
}
