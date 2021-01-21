package com.task.multidatasource;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.task.configuration.EnableTask;

@EnableTask
public class TaskRunner implements CommandLineRunner {
	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job job;

	public void run(String... args) throws Exception {
		JobParameters jobParameters = (new JobParametersBuilder()).addDate("date", new Date())
				.addLong("JobId", Long.valueOf(System.currentTimeMillis()))
				.addLong("time", Long.valueOf(System.currentTimeMillis())).toJobParameters();
		JobExecution execution = this.jobLauncher.run(this.job, jobParameters);
		System.out.println("STATUS :: " + execution.getStatus());
	}

}
