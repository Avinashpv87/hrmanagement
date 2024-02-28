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
package com.saika.hrmanagement.common.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @author mani
 */
@Getter
@Setter
@JsonIgnoreProperties({"trace", "cause","stackTrace", "suppressed", "localizedMessage", "data", "httpStatus"})
public class CustomApplicationException extends RuntimeException {

    private HttpStatus httpStatus;
    private List<String> errors;
    private Object data;

    private String status;
    @Schema(description = "status message.",
            example = "status message of api response", required = false)
    private String statusMessage;
    @Schema(description = "status code of api response",
            example = "status code 200, 201, 204, 400, 401, 403, 404, 500, 503", required = true)
    private int statusCode;

    @Schema(description = "response created timestamp",
            example = "", required = false)
    private LocalDateTime timestamp;

    public CustomApplicationException(String message) {
        this(HttpStatus.BAD_REQUEST, message);
    }

    public CustomApplicationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public CustomApplicationException(HttpStatus httpStatus, String message) {
        this(httpStatus, message, Collections.singletonList(message), null);
    }

    public CustomApplicationException(HttpStatus httpStatus, String message, Object data) {
        this(httpStatus, message, Collections.singletonList(message), data);
    }

    public CustomApplicationException(HttpStatus httpStatus, String message, List<String> errors) {
        this(httpStatus, message, errors, null);
    }

    public CustomApplicationException(HttpStatus httpStatus, String message, List<String> errors, Object data) {
        super(message);
        this.httpStatus = httpStatus;
        this.errors = errors;
        this.data = data;
    }
}
