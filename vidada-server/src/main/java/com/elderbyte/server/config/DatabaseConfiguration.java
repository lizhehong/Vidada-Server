package com.elderbyte.server.config;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableTransactionManagement
public class DatabaseConfiguration implements EnvironmentAware {

    private final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);

    private RelaxedPropertyResolver propertyResolver;

    private Environment env;

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
        this.propertyResolver = new RelaxedPropertyResolver(env, "spring.datasource.");
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingClass(name = "com.elderbyte.server.config.HerokuDatabaseConfiguration")
    @Profile("!" + Constants.SPRING_PROFILE_CLOUD)
    public DataSource dataSource() {
        log.debug("Configuring Datasource");

        // Read the application yml properties
        String url = propertyResolver.getProperty("url");
        String databaseName = propertyResolver.getProperty("databaseName");
        String user = propertyResolver.getProperty("username");
        String pass = propertyResolver.getProperty("password");
        String dataSourceClassName = propertyResolver.getProperty("dataSourceClassName");
        String serverName = propertyResolver.getProperty("serverName");

        if (url == null && databaseName == null) {
            log.error("Your database connection pool configuration is incorrect! The application" +
                    "cannot start. Please check your Spring profile, current profiles are: {}",
                Arrays.toString(env.getActiveProfiles()));

            throw new ApplicationContextException("Database connection pool is not configured correctly");
        }
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(dataSourceClassName);
        if (url == null || "".equals(url)) {
            config.addDataSourceProperty("databaseName", databaseName);
            config.addDataSourceProperty("serverName", serverName);
        } else {
            config.addDataSourceProperty("url", url);
        }
        config.addDataSourceProperty("user", user);
        config.addDataSourceProperty("password", pass);


        log.info(String.format("Database config: url: %s, db name: %s, server: %s, data source cls: %s, user: %s,",
            url, databaseName, serverName, dataSourceClassName, user));

        return new HikariDataSource(config);
    }
}
