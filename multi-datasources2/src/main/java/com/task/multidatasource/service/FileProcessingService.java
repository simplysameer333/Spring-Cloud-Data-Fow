package com.task.multidatasource.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.multidatasource.model.MetaDataModel;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FileProcessingService {
	
	//TODO This needs to be removed read from json, create method in utility class
	String [] header = {"id", "first_name", "last_name", "minutes", "data_usage"};
	String [] readcolumn = {"id", "last_name", "minutes", "data_usage"};
	
	@Autowired
	@Qualifier("mysqlDataSource")
	private DataSource dataSource;
	
	private MetaDataModel[] jsonMetaData;

	@Value("${app.input}")
	private String path;
	
	@Value("${app.metafilename}")
	private String metafilename;
	
	/**
	 * Load Meta data Json from metadata model
	 * 
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@PostConstruct
	public void loadMetaData() throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		
		this.jsonMetaData = mapper.readValue(Paths.get(ClassLoader.getSystemResource(this.metafilename).toURI()).toFile(), MetaDataModel[].class);
		//mapper.readValue(new File(this.metafilename), new MetaDataModel[]());
		
		for (MetaDataModel fileMetaData : jsonMetaData) {
			log.info(fileMetaData.toString());
		}
	}
	
	/**
	 * This responsible for starting the process
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void execute(String... args) throws FileNotFoundException, IOException, URISyntaxException {
		log.info("Execute - {}", args.length);
		
		List<String[]> rows = customerItemReader();
		
		int rowsInserted = customerItemWriter(rows);
		log.info("Rows Inserted {}", rowsInserted);
	}
	
	private List<String[]> customerItemReader() throws FileNotFoundException, IOException, URISyntaxException {

		
		CsvParserSettings settings = new CsvParserSettings();
		settings.detectFormatAutomatically(); //detects the format 
		
		//extracts the headers from the input
		settings.setHeaderExtractionEnabled(true);

		//or give the header names yourself
		// if you use this it will override the headers read from the input (enabled above).
		settings.setHeaders(header);

		//now for the column selection
		settings.selectFields(readcolumn); //rows will contain only values of column "A" and "C"
		//or
		//settings.selectIndexes(0, 2); //rows will contain only values of columns at position 0 and 2
		
		File file = Paths.get(ClassLoader.getSystemResource(this.path).toURI()).toFile();

		List<String[]> rows = new CsvParser(settings).parseAll(file, "UTF-8");
		
		return rows;
	}
	
	/**
	 * This will call doa layer and apply the will run all method under transaction
	 * @param rows
	 * @return
	 */
	@Transactional // This could be move to dao if we have only one dao call
	private int customerItemWriter(List<String[]> rows) {
		// call dao layer insert and return rows and return count
		return 0;
	}	
	
}
