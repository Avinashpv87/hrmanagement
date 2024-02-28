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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author mani
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AppMetadataRequest {

    @Schema(description = "id required only for PUT methods.",
            example = "id required only for PUT methods", required = false)
    private String id;

    @NotBlank
    @NotBlank(message = "label or key name should not be null or empty")
    @Size(min = 3, message = "label or key must be minimum 3 characters")
    private String labelKey;

    @NotBlank
    @NotBlank(message = "value code or id should not be null or empty")
    @Size(min = 2, message = "value code must be minimum 2 characters")
    private String value;

    @NotBlank
    @NotBlank(message = "group code or id should not be null or empty")
    @Size(min = 2, message = "group code must be minimum 2 characters")
    private String group;



    private String description;
    
}


