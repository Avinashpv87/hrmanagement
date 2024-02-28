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

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author mani
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SignupRequest {


    @Schema(description = "id required only for PUT methods.",
            example = "id required only for PUT methods", required = false)
    private String id;

	@NotBlank(message = "username should not be null or empty")
    @Size(min = 6, message = "user must be minimum 6 characters")
    private String userName;
    
	@NotBlank(message = "password should not be null or empty")
    @Size(min = 8, max = 16, message = "password must be between 8 and 16 characters")
    private String password;

	@NotBlank(message = "firstName should not be null or empty")
    @Valid
    private String firstName;

	@NotBlank(message = "lastName should not be null or empty")
    @Valid
    private String lastName;

    @Email(message = "email should be valid")
    @NotBlank(message = "emailId should not be null or empty")
    @Valid
    private String email;

    @Digits(message="PhoneNumber should contain 10 digits.", fraction = 0, integer = 10)
    @Valid
    private String primaryPhoneNumber;

    @Digits(message="Secondary PhoneNumber should contain 10 digits.", fraction = 0, integer = 10)
    @Valid
    private String secondaryPhoneNumber;

    private List<String> roles;

    @NotBlank(message = "addressLine1 should not be null or empty")
    @Valid
    private String addressLine1;

    private String addressLine2;

    @Valid
    private String city;

    @NotBlank(message = "state should not be null or empty")
    @Valid
    private String state;

    @Digits(message="postal code should contain 5 digits.", fraction = 0, integer = 5)
    @Valid
    private Integer postalCode;

    @NotBlank(message = "country should not be null or empty")
    @Valid
    private String country;
    
    
}
