package com.task.multidatasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Configuration;

import com.task.multidatasource.service.FileProcessingService;

import lombok.extern.slf4j.Slf4j;

@EnableTask
@Configuration
@Slf4j
public class TaskRunner implements CommandLineRunner {
	
	@Autowired
	private FileProcessingService job;

	public void run(String... args) throws Exception {
		log.debug("Task is callled");
		job.execute(args);
	}
}
