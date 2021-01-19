package com.task.outbound.mapper;

import java.net.MalformedURLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.task.outbound.model.DataModel;

@Configuration
public class JobConfig {
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Value("${app.output}")
	private String path;

	@Autowired
	private DataSource dataSource;

	@Bean
	public ItemReader<DataModel> itemReader(DataSource dataSource) {
		return new JdbcCursorItemReaderBuilder<DataModel>().name("sink").dataSource(dataSource)
				.sql("SELECT * FROM TAKARA_FINAL").rowMapper(new BeanPropertyRowMapper<>(DataModel.class)).build();
	}

	@Bean
	public FlatFileItemWriter<DataModel> writer() {
		// Create writer instance
		FlatFileItemWriter<DataModel> writer = new FlatFileItemWriter<>();

		// Set output file location
		FileSystemResource filepath = new FileSystemResource(path);
		writer.setResource(filepath);

		// All job repetitions should "append" to same output file
		writer.setAppendAllowed(true);

		// Name field values sequence based on object properties
		writer.setLineAggregator(new DelimitedLineAggregator<DataModel>() {
			{
				setDelimiter(",");
				setFieldExtractor(new BeanWrapperFieldExtractor<DataModel>() {
					{
						setNames(new String[] { "id", "firstName", "lastName", "minutes", "dataUsage" });
					}
				});
			}
		});
		System.out.println("filepath---->" + filepath.getPath());
		return writer;
	}

	@Bean
	public Step step1() throws MalformedURLException {
		return stepBuilderFactory.get("step1").<DataModel, DataModel>chunk(10).reader(itemReader(this.dataSource))
				.writer(writer()).build();
	}

	@Bean
	public Job job() throws MalformedURLException {
		return jobBuilderFactory.get("job").incrementer(new RunIdIncrementer()).start(step1()).build();
	}
}
