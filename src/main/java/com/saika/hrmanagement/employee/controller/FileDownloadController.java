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
package com.saika.hrmanagement.employee.controller;

import com.saika.hrmanagement.common.entity.EmployeeTimesheetDocument;
import com.saika.hrmanagement.common.entity.EmployeeWorkStatusDocument;
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.employee.repository.EmployeeTimesheetDocumentRepository;
import com.saika.hrmanagement.employee.repository.EmployeeWorkStatusDocumentRepository;
import com.saika.hrmanagement.employee.response.CustomApplicationResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author mani
 */
@RestController
@CrossOrigin
@Tag(name = "FileDownload", description = "The FileDownload Management API")
@RequestMapping(path = "/file-download", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "Bearer Authentication")
public class FileDownloadController implements CustomApplicationResponse {

    @Autowired
    private EmployeeTimesheetDocumentRepository employeeTimesheetDocumentRepository;

    @Autowired
    private EmployeeWorkStatusDocumentRepository employeeWorkStatusDocumentRepository;

    @GetMapping("/timesheet/{id}")
    public ResponseEntity<ByteArrayResource> timesheetDownload(@PathVariable String id) throws IOException {
        EmployeeTimesheetDocument employeeTimesheetDocument = employeeTimesheetDocumentRepository.findById(id).orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND, id +" Not Found"));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(employeeTimesheetDocument.getFileContentType() ))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + employeeTimesheetDocument.getFileName() + "\"")
                .body(new ByteArrayResource(employeeTimesheetDocument.getFileContent()));
    }

    @GetMapping("/work-status/{id}")
    public ResponseEntity<ByteArrayResource> employeeWorkDocumentDownload(@PathVariable String id) throws IOException {
        EmployeeWorkStatusDocument employeeWorkStatusDocument = employeeWorkStatusDocumentRepository.findById(id).orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND, id +" Not Found"));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(employeeWorkStatusDocument.getFileContentType() ))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + employeeWorkStatusDocument.getFileName() + "\"")
                .body(new ByteArrayResource(employeeWorkStatusDocument.getFileContent()));
    }
}

