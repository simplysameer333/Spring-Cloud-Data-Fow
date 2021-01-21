package com.task.multidatasource.mapper;

import java.net.MalformedURLException;
import java.net.URL;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.task.multidatasource.model.DataModel;
import com.task.multidatasource.model.database.DataSourceConfiguration;

@Configuration
@EnableBatchProcessing
@Import({ DataSourceConfiguration.class })
public class JobConfig {
	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	@Qualifier("mysqlDataSource")
	private DataSource dataSource;

	@Value("${app.input}")
	private String path;

	@Bean
	public FlatFileItemReader<DataModel> customerItemReader() throws MalformedURLException {
		FlatFileItemReader<DataModel> reader = new FlatFileItemReader<>();
		reader.setLinesToSkip(1);
		// reader.setResource(new
		// FileSystemResource("C:\\Users\\User\\Desktop\\CODD\\CodeBase\\input.csv"));

		if (this.path.startsWith("http")) {
			URL resource = new URL(this.path);
			reader.setResource((Resource) new UrlResource(resource));
		} else {
			reader.setResource((Resource) new ClassPathResource(this.path));
		}

		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames(new String[] { "id", "first_name", "last_name", "minutes", "data_usage" });

		DefaultLineMapper<DataModel> customerLineMapper = new DefaultLineMapper<>();
		customerLineMapper.setLineTokenizer(tokenizer);
		customerLineMapper.setFieldSetMapper(new CustomerFieldSetMapper());
		customerLineMapper.afterPropertiesSet();

		reader.setLineMapper(customerLineMapper);

		return reader;
	}

	@Bean
	public JdbcBatchItemWriter<DataModel> customerItemWriter() {
		JdbcBatchItemWriter<DataModel> writer = new JdbcBatchItemWriter<>();
		writer.setDataSource(this.dataSource);
		writer.setSql(
				"INSERT INTO TAKARA_STAGE (id, first_name, last_name, minutes, data_usage) VALUES (:id, :firstName, :lastName, :minutes, :dataUsage)");
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
		writer.afterPropertiesSet();

		return writer;
	}

	@Bean
	public Step step1() throws MalformedURLException {
		return stepBuilderFactory.get("step1").<DataModel, DataModel>chunk(10).reader(customerItemReader())
				.writer(customerItemWriter()).build();
	}

	@Bean
	public Job job() throws MalformedURLException {
		return jobBuilderFactory.get("job").incrementer(new RunIdIncrementer()).start(step1()).build();
	}
}
