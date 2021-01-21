package com.task.multidatasource.model.database;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.task.configuration.DefaultTaskConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchConfigurer extends DefaultTaskConfigurer
{

    public BatchConfigurer(@Qualifier("mysqlDataSource") DataSource dataSource) 
    {
        super(dataSource);
    }

}
