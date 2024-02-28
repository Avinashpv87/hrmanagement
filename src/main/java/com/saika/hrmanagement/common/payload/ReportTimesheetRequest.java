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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * @author mani
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ReportTimesheetRequest {

        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<String> employeePkIds;

        @DateTimeFormat(pattern = "MM-dd-yyyy")
        private LocalDate startDate;

        @DateTimeFormat(pattern = "MM-dd-yyyy")
        private LocalDate endDate;

        private List<Status> status;

        private Boolean approved;

        private Boolean rejected;

        private Boolean saved;

        private Boolean submitted;

}
