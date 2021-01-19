package com.task.transformation.mapper;

import java.net.MalformedURLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.task.transformation.model.DataModel;

@Configuration
public class JobConfig {
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private DataSource dataSource;

	@Bean
	public ItemReader<DataModel> itemReader(DataSource dataSource) {
		return new JdbcCursorItemReaderBuilder<DataModel>().name("transform").dataSource(dataSource)
				.sql("SELECT * FROM TAKARA_STAGE").rowMapper(new BeanPropertyRowMapper<>(DataModel.class)).build();
	}

	@Bean
	public ItemWriter<DataModel> jdbcBillWriter(DataSource dataSource) {
		JdbcBatchItemWriter<DataModel> writer = new JdbcBatchItemWriterBuilder<DataModel>().beanMapped()
				.dataSource(dataSource)
				.sql("INSERT INTO TAKARA_FINAL (id, first_name, last_name, minutes, data_usage) VALUES (:id, UPPER(:firstName),UPPER( :lastName), :minutes, :dataUsage)")
				.build();
		return writer;
	}

	@Bean
	public Step step1() throws MalformedURLException {
		return stepBuilderFactory.get("step1").<DataModel, DataModel>chunk(10).reader(itemReader(this.dataSource))
				.writer(jdbcBillWriter(this.dataSource)).build();
	}

	@Bean
	public Job job() throws MalformedURLException {
		return jobBuilderFactory.get("job").incrementer(new RunIdIncrementer()).start(step1()).build();
	}
}
