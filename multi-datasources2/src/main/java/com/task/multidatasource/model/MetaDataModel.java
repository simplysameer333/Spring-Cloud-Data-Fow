package com.task.multidatasource.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MetaDataModel {
	private String tableName;
	private String fileName;
	private ColumnMapping mapping;
	private String delimeter;
}

@Data
class ColumnMapping {
	private String sourceColumnName;
	private String taggetColumnName;
	private String[] operation;
}
