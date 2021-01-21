package com.task.multidatasource.model.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class DataSourceConfiguration {

	@Value("classpath:schema.sql")
    private Resource schemaScript;
	
    @Value("${spring.datasource.url}")
    private String dataflowUrl;
    
    @Value("${spring.datasource.username}")
    private String dataflowUsername;
    
    @Value("${spring.datasource.password}")
    private String dataflowPassword;    
	
    @Value("${task.spring.datasource.url}")
    private String taskDBUrl;
    
    @Value("${task.spring.datasource.username}")
    private String taskDBUsername;
    
    @Value("${task.spring.datasource.password}")
    private String taskDBPassword;    
    
    @Bean (name = "mysqlDataSource")
    @ConfigurationProperties(prefix="task.spring.datasource")
    public javax.sql.DataSource secondaryDataSource() {
    	final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new org.mariadb.jdbc.Driver());
        dataSource.setUrl(taskDBUrl);
        dataSource.setUsername(taskDBUsername);
        dataSource.setPassword(taskDBPassword);
        DatabasePopulatorUtils.execute(databasePopulator(), dataSource);
        return dataSource;
    }

    // note the new name: dataSource -> this is the name springBatch is looking for
    @Bean
    @Primary
    @ConfigurationProperties(prefix="spring.datasource")
    public javax.sql.DataSource dataSource() {
    	final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new org.mariadb.jdbc.Driver());
        dataSource.setUrl(dataflowUrl);
        dataSource.setUsername(dataflowUsername);
        dataSource.setPassword(dataflowPassword);
        return dataSource;
    }
    
    private DatabasePopulator databasePopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(schemaScript);
        return populator;
    }
}
