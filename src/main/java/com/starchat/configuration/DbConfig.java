package com.starchat.configuration;

import com.intersystems.jdbc.IRIS;
import com.intersystems.jdbc.IRISConnection;
import com.intersystems.jdbc.IRISDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Configuration
public class DbConfig {

    @Value("#{systemEnvironment['SSL_CONFIG_FILE_PATH']}")
    private String sslConfigFilePath;

    @Value("#{systemEnvironment['DB_NAMESPACE']}")
    private String dbNamespace;

    @Value("#{systemEnvironment['DB_HOST']}")
    private String dbHost;

    @Value("#{systemEnvironment['DB_PORT']}")
    private int dbPort;

    @Value("#{systemEnvironment['DB_USER']}")
    private String dbUser;

    @Value("#{systemEnvironment['DB_PASSWORD']}")
    private String dbPassword;

    @Value("#{systemEnvironment['SECURITY_LEVEL']}")
    private String securityLevel;

    @Bean(destroyMethod = "close")
    public IRISConnection getIrisConnection() throws SQLException {
        return (IRISConnection) irisDataSource().getConnection();
    }

    @Bean(destroyMethod = "close")
    public IRIS getIrisNativeInterface(IRISConnection dbConnection) throws SQLException {
        return IRIS.createIRIS(dbConnection);
    }

    @Bean
    public IRISDataSource irisDataSource() throws SQLException {

        if (sslConfigFilePath != null) {
            System.setProperty("com.intersystems.SSLConfigFile", sslConfigFilePath);
        }

        IRISDataSource dataSource = new IRISDataSource();
        dataSource.setServerName(dbHost);
        dataSource.setPortNumber(dbPort);
        dataSource.setUser(dbUser);
        dataSource.setPassword(dbPassword);
        dataSource.setDatabaseName(dbNamespace);

        if (securityLevel != null) {
            dataSource.setConnectionSecurityLevel(Integer.parseInt(securityLevel));
        }
        return dataSource;
    }
}
