package com.starchat;

import com.caretdev.liquibase.database.core.IRISDatabase;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebsiteApplication {

    public static void main(String[] args) throws LiquibaseException {
        IRISDatabase database = (IRISDatabase) DatabaseFactory.getInstance().openDatabase("jdbc:IRIS://127.0.0.1:1972/USER?useSSL=false", "_SYSTEM", "1423", null, null);
        Liquibase liquibase = new Liquibase("liquibase/changelog.xml", new ClassLoaderResourceAccessor(), database);
        liquibase.update("");
        SpringApplication.run(WebsiteApplication.class, args);
    }

}
