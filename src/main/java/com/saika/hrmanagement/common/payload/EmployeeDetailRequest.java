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
package com.saika.hrmanagement.common.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.saika.hrmanagement.common.constant.EStatus;
import com.saika.hrmanagement.common.constant.EWorkAuthType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

/**
 * @author mani
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EmployeeDetailRequest {

        @Schema(description = "id required only for PUT methods.",
                example = "id required only for PUT methods", required = false)
        private String id;

        @NotBlank(message = "firstName should not be null or empty")
        private String firstName;

        @NotBlank(message = "lastName should not be null or empty")
        private String lastName;

        @Email(message = "email should be valid")
        @NotBlank(message = "emailId should not be null or empty")
        private String email;

        private String employeeIdentityNumber;

        @Digits(message="PhoneNumber should contain 10 digits.", fraction = 0, integer = 10)
        @Valid
        private String primaryPhoneNumber;

        private String secondaryPhoneNumber;

        private List<String> roles;

        private String addressLine1;

        private String addressLine2;

        private String city;

        private String state;

        private Integer postalCode;

        private String country;

        @DateTimeFormat(pattern = "MM-dd-yyyy")
        private LocalDate dateOfJoin;

        @DateTimeFormat(pattern = "MM-dd-yyyy")
        private LocalDate lastDateOfEmployment;

        private int  projectAllocation;

        private EWorkAuthType workAuthType;

        private EStatus workStatus;

        private String designation;

        private String employementType;

        private  String wageType;

        private  List<String> skills;

        private String terminationReason;

        private String comments;

}
