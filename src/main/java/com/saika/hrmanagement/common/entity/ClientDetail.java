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

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author mani
 */

@Document(collection = "client_detail")
@Data
public class ClientDetail {

	@Id
	private String id;

	@Indexed(unique = true)
	private String name;

	private String code;

	@Indexed(unique = true)
	private String email;

	private String description;

	private String addressLine1;

	private String addressLine2;

	private String city;

	private String state;

	private Integer postalCode;

	private String country;

	private String primaryPhoneNumber;

	private String secondaryPhoneNumber;

	@DateTimeFormat(pattern = "MM-dd-yyyy")
	private LocalDate relationshipDate;

	private Boolean isActive;

	private LocalDateTime createdOn;

	private String createdBy;

	private LocalDateTime updatedOn;

	private String updatedBy;
	
}
