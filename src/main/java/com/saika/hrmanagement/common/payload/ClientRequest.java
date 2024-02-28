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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * @author mani
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ClientRequest {

    @Schema(description = "id required only for PUT methods.",
            example = "id required only for PUT methods", required = false)
    private String id;

    @NotBlank
    @NotBlank(message = "client name should not be null or empty")
    @Size(min = 2, message = "client name must be minimum 6 characters")
    private String name;

    @NotBlank
    @NotBlank(message = "client code or id should not be null or empty")
    @Size(min = 2, message = "client code must be minimum 2 characters")
    private String code;

    private String description;

    @Email(message = "email should be valid")
    //@NotBlank(message = "emailId should not be null or empty")
    private String email;

    //@Digits(message="PhoneNumber should contain 10 digits.", fraction = 0, integer = 10)
    private String primaryPhoneNumber;

    //@Digits(message="Secondary PhoneNumber should contain 10 digits.", fraction = 0, integer = 10)
    private String secondaryPhoneNumber;

    //@NotBlank(message = "addressLine1 should not be null or empty")
    private String addressLine1;

    private String addressLine2;

    private String city;

    //@NotBlank(message = "state should not be null or empty")
    private String state;

    //@Digits(message="postal code should contain 5 digits.", fraction = 0, integer = 5)
    private Integer postalCode;

    private String country;

    //@DateTimeFormat(pattern = "MM-dd-yyyy")
    private LocalDate relationshipDate;

    private Boolean isActive;
    
    
}
