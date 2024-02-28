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

/**
 * @author mani
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EmployeeWorkStatusRequest {

    @Schema(description = "id required only for PUT methods.",
            example = "id required only for PUT methods", required = false)
    private String id;

    private String employeeId;

    private EWorkAuthType visaType;

    private EStatus visaStatus;

}
