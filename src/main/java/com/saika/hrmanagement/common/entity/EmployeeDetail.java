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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.saika.hrmanagement.common.constant.EStatus;
import com.saika.hrmanagement.common.constant.EWorkAuthType;
import lombok.Data;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Mani
 */
@Document(collection = "employee_detail")
@Data
public class EmployeeDetail {

	@Id
	private String id;

	private String employeeIdentityNumber;

	private String userName;

	@JsonIgnore
	private String password;

	private String firstName;

	private String lastName;

	private String email;

	private String designation;

	@DateTimeFormat(pattern = "MM-dd-yyyy")
	private LocalDate dateOfJoin;

	@DateTimeFormat(pattern = "MM-dd-yyyy")
	private LocalDate lastDateOfEmployment;

	private int  projectAllocation;

	private String primaryPhoneNumber;

	private String secondaryPhoneNumber;

	private String addressLine1;

	private String addressLine2;

	private String city;

	private String state;

	private Integer postalCode;

	private String country;

	@JsonIgnore
	private String passwordResetToken;

	private LocalDateTime lastLoginDateTime;
	
	private Boolean isActive;

	private Boolean isEnrolled;

	@JsonIgnore
	private String isActiveToken;

	@JsonIgnore
	private String enrollToken;

	@DBRef
	private Set<Role> roles = new HashSet<>();

	@ReadOnlyProperty
	@DocumentReference(lookup = "{ 'employeeDetailId' : ?#{#self._id} }")
	@JsonIgnoreProperties("employeeDetail")
	private List<EmployeeClientMapping> employeeClientMappings;

	@ReadOnlyProperty
	@DocumentReference(lookup = "{ 'employeeDetailId' : ?#{#self._id} }")
	private List<EmployeeWorkStatus> employeeWorkStatuses;

	private LocalDateTime createdOn;

	private String createdBy;

	private LocalDateTime updatedOn;

	private String updatedBy;

	private Long visits;

	private Boolean isDelete;

	private EWorkAuthType workAuthType;

	private EStatus workStatus;

	private  String employementType;

	private  String wageType;

	private  List<String> skills;

	private String terminationReason;

	private String comments;
}
