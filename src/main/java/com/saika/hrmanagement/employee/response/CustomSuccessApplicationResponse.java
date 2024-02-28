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
package com.saika.hrmanagement.employee.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author mani
 * @param <T>
 */
@NoArgsConstructor
@Data
public class CustomSuccessApplicationResponse<T> {

    @Schema(description = "status of response.",
            example = "success or failure", required = false)
    private String status;
    @Schema(description = "status message.",
            example = "status message of api response", required = false)
    private String statusMessage;
    @Schema(description = "status code of api response",
            example = "status code 200, 201, 204, 400, 401, 403, 404, 500, 503", required = true)
    private int statusCode;
    @Schema(description = "response entity, pojo",
            example = "", required = false)
    private T responseData;
    @Schema(description = "response created timestamp",
            example = "", required = false)
    private LocalDateTime timestamp;

   /* CustomSuccessApplicationResponse(HttpStatus httpStatus, T data) {
        this.status = "success";
        this.statusMessage = httpStatus.name();
        this.statusCode = httpStatus.value();
        this.responseData = data;
        this.timestamp = LocalDateTime.now();
    }*/

}