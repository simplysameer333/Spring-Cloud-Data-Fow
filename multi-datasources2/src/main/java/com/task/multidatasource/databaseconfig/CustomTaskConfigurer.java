package com.task.multidatasource.databaseconfig;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.task.configuration.DefaultTaskConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomTaskConfigurer extends DefaultTaskConfigurer
{
    public CustomTaskConfigurer(@Qualifier("mysqlDataSource") DataSource dataSource) 
    {
        super(dataSource);
    }

}
