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

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author mani
 */
@Data
public class SetupUserPasswordRequest {

	@NotBlank(message = "password should not be null or empty")
	@Size(min = 8, max = 16, message = "password must be between 8 and 16 characters")
	private String password;

	@NotBlank(message = "token should not be null or empty")
	private String token;

	@NotBlank(message = "username should not be null or empty")
	@Size(min = 6, message = "user must be minimum 6 characters")
	private String userName;

}
