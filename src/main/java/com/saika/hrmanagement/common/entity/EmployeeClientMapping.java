/*
 * Copyright 2022 the original author or authors.
 * Licensed under the Saika Technologies Inc License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.saika.com/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.saika.hrmanagement.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.saika.hrmanagement.common.payload.CommonStatus;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mani
 */
@Document(collection = "employee_client_mapping")
@Data
public class EmployeeClientMapping {

	@Id
	private String id;

	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId employeeDetailId;

	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId clientDetailId;

	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId endClientDetailId;

	@ReadOnlyProperty
	@DocumentReference(lookup = "{ '_id' : ?#{#self.clientDetailId} }")
	@DBRef
	private ClientDetail clientDetail;

	@ReadOnlyProperty
	@DocumentReference(lookup = "{ '_id' : ?#{#self.endClientDetailId} }")
	private ClientDetail endClientDetail;

	@ReadOnlyProperty
	@DocumentReference(lookup = "{ '_id' : ?#{#self.employeeDetailId} }")
	@JsonIgnoreProperties("employeeClientMappings")
	private EmployeeDetail employeeDetail;

	private String designation;

	@DateTimeFormat(pattern = "MM-dd-yyyy")
	private LocalDate startDate;

	@DateTimeFormat(pattern = "MM-dd-yyyy")
	private LocalDate endDate;

	private int  projectAllocation;

	private CommonStatus isContractEnded;

	private Boolean isDeleted;

	private LocalDateTime createdOn;

	private String createdBy;

	private LocalDateTime updatedOn;

	private String updatedBy;
	
}
