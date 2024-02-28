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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mani
 */
public interface CustomApplicationResponse {

    default  Map<String, Object> constructSuccessApplicationResponse(HttpStatus httpStatus, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("statusMessage", httpStatus);
        response.put("statusCode", httpStatus.value());
        response.put("responseData", data);
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }
    default  Map<String, Object> constructFailureApplicationResponse(HttpStatus httpStatus, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "failure");
        response.put("statusMessage", httpStatus);
        response.put("statusCode", httpStatus.value());
        //response.put("responseData", data);
        response.put("error", data);
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }

    default  String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.writeValueAsString(object);
    }
}