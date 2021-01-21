package com.task.multidatasource.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DataModel {
	private Long id;

	private String firstName;

	private String lastName;

	private Long minutes;

	private Long dataUsage;
}