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

import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.employee.response.CustomApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

/**
 * @author mani
 */
@RestController
@CrossOrigin
@Tag(name = "A-Test", description = "The Test API")
public class TestController implements CustomApplicationResponse {

    /**
     *
     * @return
     */
    @GetMapping("/test")
    @Operation(summary = "Test App", description = "Test App is Running!", tags = { "A-Test" })
    public ResponseEntity<Object> welcomeToTimeSheet() {
        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, " Welcome To Timesheet App"));
    }

    @Operation(summary = "Add Employee Timesheet Details", description = "Add Required Employee Timesheet Details.")
    @Transactional
    @PostMapping(value = "test/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> addEmployeeTimesheetUpload1(@RequestPart(name="files", required=false) MultipartFile[] file, @RequestPart("etsRequest") String etsRequest)  throws CustomApplicationException {
        System.out.println("files = " + file[0]);
        System.out.println("files = " + file.length);
        System.out.println("etsRequest = " + etsRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(constructSuccessApplicationResponse(HttpStatus.CREATED, "Testing file upload"));

    }
}
