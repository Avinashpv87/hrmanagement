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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.saika.hrmanagement.common.constant.EStatus;
import com.saika.hrmanagement.common.constant.EWorkAuthType;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mani
 */
@Document(collection = "employee_work_status")
@Data
public class EmployeeWorkStatus {

	@Id
	private String id;

	private EWorkAuthType visaType;

	private EStatus visaStatus;

	@ReadOnlyProperty
	@DocumentReference(lookup = "{ 'employeeWorkStatusId' : ?#{#self._id} }")
	private List<EmployeeWorkStatusDocument> employeeWorkStatusDocument;

	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId employeeDetailId;

	private Boolean isDeleted;

	private LocalDateTime createdOn;

	private String createdBy;

	private LocalDateTime updatedOn;

	private String updateBy;

}
