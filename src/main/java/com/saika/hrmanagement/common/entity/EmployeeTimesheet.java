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
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.saika.hrmanagement.common.payload.Status;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mani
 */
@Document(collection = "employee_timesheet")
@Data
public class EmployeeTimesheet {
	@Id
	private String id;

	private List<DateTaskComment> dateTaskComment;

	private String totalHrs;

	@DateTimeFormat(pattern = "MM-dd-yyyy")
	@NotNull(message = "Please provide a date.")
	private LocalDate start;//Date;

	@DateTimeFormat(pattern = "MM-dd-yyyy")
	@NotNull(message = "Please provide a date.")
	private LocalDate end;//Date;

	private Boolean isTimesheetDeleted;

	private String employeeName;

	private String approverPkId;

	private String approvedBy;

	private String clientId;

	@ReadOnlyProperty
	@DocumentReference(lookup = "{ 'employeeTimesheetId' : ?#{#self._id} }")
	private List<EmployeeTimesheetDocument> employeeTimesheetDocument;

	private LocalDateTime createdOn;

	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId employeeId;

	@ReadOnlyProperty
	@DocumentReference(lookup = "{ '_id' : ?#{#self.employeeId} }")
	@JsonIncludeProperties("email")
	@JsonIgnoreProperties({"employeeClientMappings", "employeeWorkStatuses" })
	private EmployeeDetail employeeDetail;

	private String createdBy;

	private LocalDateTime updatedOn;

	private String updatedBy;

	private String comments;

	private List<String> markers;

	private Status status;

}
