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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.saika.hrmanagement.common.constant.EDocType;
import com.saika.hrmanagement.common.constant.EStatus;
import com.saika.hrmanagement.common.constant.EWorkAuthType;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Mani
 */
@Document(collection = "employee_work_status_document")
@Data
public class EmployeeWorkStatusDocument {

	@Id
	private String id;

	//private String type;

	private EWorkAuthType workAuthType;

	private EDocType docType;

	private EStatus status;

	private LocalDate startDate;

	private LocalDate endDate;

	private String fileName;

	private String comments;

	private String fileContentType;

	@JsonIgnore
	private byte[] fileContent;

	private long fileSize;

	private Boolean isDeleted;

	private boolean visibleToEmployee;

	//@JsonSerialize(using = ToStringSerializer.class)
	//private ObjectId employeeWorkStatusId;

	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId employeePkId;

	private LocalDateTime createdOn;

	private String createdBy;

	private LocalDateTime updatedOn;

	private String updatedBy;

}
