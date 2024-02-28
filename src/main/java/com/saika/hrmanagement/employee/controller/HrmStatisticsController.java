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

import com.saika.hrmanagement.common.constant.EWorkAuthType;
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.common.payload.Status;
import com.saika.hrmanagement.employee.repository.EmployeeTimesheetRepository;
import com.saika.hrmanagement.employee.repository.UserDetailRepository;
import com.saika.hrmanagement.employee.response.*;
import com.saika.hrmanagement.employee.service.impl.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mani
 */

@RestController
@CrossOrigin
@Tag(name = "Statistics", description = "The Hrm Statistic API")
@RequestMapping(path = "/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "Bearer Authentication")
public class HrmStatisticsController implements CustomApplicationResponse {

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private EmployeeTimesheetRepository employeeTimesheetRepository;

    /**
     *
     * @return
     */
    @Operation(summary = "Get Hrm Statistic for logged in employee", description = "Get Hrm Statistic Details for logged in employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = StatisticsResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @GetMapping("/employee")
    public ResponseEntity<Object> summaryOfHrmStatisticsEmployee() throws CustomApplicationException{
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationStatisticsResponse response = new ApplicationStatisticsResponse();
        TimesheetSummary timesheetSummary = new TimesheetSummary();
        timesheetSummary.setApproved(employeeTimesheetRepository.countStatusByEmployeeId(Status.APPROVE.name(), new ObjectId(user.getId())));
        timesheetSummary.setRejected(employeeTimesheetRepository.countStatusByEmployeeId(Status.REJECT.name(), new ObjectId(user.getId())));
        timesheetSummary.setSubmitted(employeeTimesheetRepository.countStatusByEmployeeId(Status.SUBMIT.name(), new ObjectId(user.getId())));
        timesheetSummary.setSaved(employeeTimesheetRepository.countStatusByEmployeeId(Status.SAVE.name(), new ObjectId(user.getId())));
        response.setTimesheetSummary(timesheetSummary);
        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, response));
    }

    /**
     *
     * @return
     */
    @Operation(summary = "Get Hrm Statistic", description = "Get Hrm Statistic Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = StatisticsResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN')")
    @GetMapping
    public ResponseEntity<Object> summaryOfHrmStatistics() throws CustomApplicationException{
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ApplicationStatisticsResponse response = new ApplicationStatisticsResponse();

        EmployeeSummary employeeSummary = new EmployeeSummary();
        employeeSummary.setActive(userDetailRepository.countIsActive(true));
        employeeSummary.setEnrolled(userDetailRepository.countIsEnrolled(true));
        employeeSummary.setDeleted(userDetailRepository.countIsDelete(true));
        employeeSummary.setNotActive(userDetailRepository.countIsActive(false));
        employeeSummary.setNotEnrolled(userDetailRepository.countIsEnrolled(false));
        employeeSummary.setTotalVisit(userDetailRepository.sumTotalVisits());

        Map<String, Long> workType = new HashMap<>();
        workType.put("h1b", userDetailRepository.countWorkAuthType(EWorkAuthType.H1B));
        workType.put("opt", userDetailRepository.countWorkAuthType(EWorkAuthType.OPT));
        workType.put("cpt", userDetailRepository.countWorkAuthType(EWorkAuthType.CPT));
        workType.put("h4", userDetailRepository.countWorkAuthType(EWorkAuthType.H4));
        workType.put("f1", userDetailRepository.countWorkAuthType(EWorkAuthType.F1));
        workType.put("l1", userDetailRepository.countWorkAuthType(EWorkAuthType.L1));
        workType.put("l2", userDetailRepository.countWorkAuthType(EWorkAuthType.L2));
        workType.put("citizen", userDetailRepository.countWorkAuthType(EWorkAuthType.Citizen));
        workType.put("gc", userDetailRepository.countWorkAuthType(EWorkAuthType.GC));
        workType.put("b1", userDetailRepository.countWorkAuthType(EWorkAuthType.B1));
        employeeSummary.setWorkAuthTypeCount(workType);

        Map<String, Long> employeeDesignationCount = new HashMap<>();
        employeeDesignationCount.put("JavaDev", userDetailRepository.countDesignation("Java Developer"));
        employeeDesignationCount.put(".NetDev", userDetailRepository.countDesignation(".Net Developer"));
        employeeDesignationCount.put("ApplicationDeveloper", userDetailRepository.countDesignation("Application Developer"));
        employeeDesignationCount.put("SoftwareDeveloper", userDetailRepository.countDesignation("Software Developer"));
        employeeDesignationCount.put("MobileAppDev", userDetailRepository.countDesignation("MobileApp Developer"));
        employeeDesignationCount.put("FullStackDeveloper", userDetailRepository.countDesignation("FullStack Developer"));
        employeeDesignationCount.put("QA", userDetailRepository.countDesignation("QA"));
        employeeDesignationCount.put("DevOps", userDetailRepository.countDesignation("DevOps"));
        employeeDesignationCount.put("Scrum", userDetailRepository.countDesignation("Scrum"));
        employeeDesignationCount.put("Manager", userDetailRepository.countDesignation("Manager"));
        employeeDesignationCount.put("HR", userDetailRepository.countDesignation("HR"));
        employeeDesignationCount.put("Others", userDetailRepository.countDesignation("Others"));
        employeeSummary.setEmployeeDesignationCount(employeeDesignationCount);

        TimesheetSummary timesheetSummary = new TimesheetSummary();
        timesheetSummary.setApproved(employeeTimesheetRepository.countStatus(Status.APPROVE.name()));
        timesheetSummary.setRejected(employeeTimesheetRepository.countStatus(Status.REJECT.name()));
        timesheetSummary.setSubmitted(employeeTimesheetRepository.countStatus(Status.SUBMIT.name()));
        timesheetSummary.setSaved(employeeTimesheetRepository.countStatus(Status.SAVE.name()));

        response.setTimesheetSummary(timesheetSummary);
        response.setEmployeeSummary(employeeSummary);

        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, response));
    }

    private static class StatisticsResponse extends CustomSuccessApplicationResponse<ApplicationStatisticsResponse> {
        public ApplicationStatisticsResponse responseData;
    }
}
