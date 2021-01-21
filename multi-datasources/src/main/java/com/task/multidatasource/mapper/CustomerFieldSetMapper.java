package com.task.multidatasource.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.task.multidatasource.model.DataModel;

public class CustomerFieldSetMapper implements FieldSetMapper<DataModel> {
  public DataModel mapFieldSet(FieldSet fieldSet) throws BindException {
    return DataModel.builder()
      .id(Long.valueOf(fieldSet.readLong("id")))
      .firstName(fieldSet.readRawString("first_name"))
      .lastName(fieldSet.readRawString("last_name"))
      .dataUsage(Long.valueOf(fieldSet.readLong("data_usage")))
      .minutes(Long.valueOf(fieldSet.readLong("minutes")))
      .build();
  }
}
