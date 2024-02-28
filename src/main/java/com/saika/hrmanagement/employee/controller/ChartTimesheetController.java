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

import com.saika.hrmanagement.common.entity.*;
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.common.payload.ReportClientRequest;
import com.saika.hrmanagement.common.payload.ReportEmployeeRequest;
import com.saika.hrmanagement.common.payload.ReportTimesheetRequest;
import com.saika.hrmanagement.common.payload.Status;
import com.saika.hrmanagement.employee.repository.ClientDetailRepository;
import com.saika.hrmanagement.employee.repository.EmployeeDetailRepository;
import com.saika.hrmanagement.employee.repository.EmployeeTimesheetRepository;
import com.saika.hrmanagement.employee.response.ChartResponse;
import com.saika.hrmanagement.employee.response.CustomApplicationResponse;
import com.saika.hrmanagement.employee.service.impl.UserDetailsImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mani
 */
@RestController
@CrossOrigin
@Tag(name = "Chart Timesheet", description = "The Chart Management API")
@RequestMapping(path = "/chart", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "Bearer Authentication")
public class ChartTimesheetController implements CustomApplicationResponse {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private EmployeeTimesheetRepository employeeTimesheetRepository;

    /**
     * @param
     * @return
     */
    @GetMapping("/timesheet")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN','TIMESHEET_ADMIN', 'MANAGER')")
    public ResponseEntity<Object> generateChartForEmployeeTimesheetAll() throws CustomApplicationException {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Integer> months = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12);
        List<TimesheetChartAggregate>  chartAggregateList =  employeeTimesheetRepository.findTotalHoursByMonthAndYearAndApproveStatus();

        List<TimesheetChartAggregate>  approvedList = chartAggregateList.stream().filter(chartData -> Objects.equals(chartData.get_id().getStatus(), "APPROVE")).collect(Collectors.toList());
        List<TimesheetChartAggregate>  pendingList = chartAggregateList.stream().filter(chartData -> Objects.equals(chartData.get_id().getStatus(), "SUBMIT")).collect(Collectors.toList());

        List<TimesheetChartAggregate>  approvedList1 = chartAggregateList.stream().filter(chartData -> Objects.equals(chartData.get_id().getStatus(), "APPROVE")).collect(Collectors.toList());
        List<TimesheetChartAggregate>  pendingList1 = chartAggregateList.stream().filter(chartData -> Objects.equals(chartData.get_id().getStatus(), "SUBMIT")).collect(Collectors.toList());


        Map<String, List<ChartResponse>> chartMap = new HashMap<>();

        addMissingEmptyMonth(months, approvedList, LocalDate.now().getYear(), Status.APPROVE);
        addMissingEmptyMonth(months, pendingList, LocalDate.now().getYear(), Status.SUBMIT);
        List<ChartResponse> chartResponseListCurrentYear = getChartResponses(approvedList, pendingList, LocalDate.now().getYear());

        addMissingEmptyMonth(months, approvedList1, LocalDate.now().getYear() - 1, Status.APPROVE);
        addMissingEmptyMonth(months, pendingList1, LocalDate.now().getYear() - 1 , Status.SUBMIT);
        List<ChartResponse> chartResponseListPreviousYear = getChartResponses(approvedList1, pendingList1, LocalDate.now().getYear() -1);
        chartMap.put("this-year", chartResponseListCurrentYear);
        chartMap.put("last-year", chartResponseListPreviousYear);


        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, chartMap));
    }

    @GetMapping("/employee/timesheet")
    public ResponseEntity<Object> generateChartForAEmployeeTimesheet() throws CustomApplicationException {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Integer> months = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12);
        List<TimesheetChartAggregate>  chartAggregateList =  employeeTimesheetRepository.findTotalHoursByMonthAndYearAndApproveStatusForEmployee(new ObjectId(user.getId()));

        List<TimesheetChartAggregate>  approvedList = chartAggregateList.stream().filter(chartData -> Objects.equals(chartData.get_id().getStatus(), "APPROVE")).collect(Collectors.toList());
        List<TimesheetChartAggregate>  pendingList = chartAggregateList.stream().filter(chartData -> Objects.equals(chartData.get_id().getStatus(), "SUBMIT")).collect(Collectors.toList());

        List<TimesheetChartAggregate>  approvedList1 = chartAggregateList.stream().filter(chartData -> Objects.equals(chartData.get_id().getStatus(), "APPROVE")).collect(Collectors.toList());
        List<TimesheetChartAggregate>  pendingList1 = chartAggregateList.stream().filter(chartData -> Objects.equals(chartData.get_id().getStatus(), "SUBMIT")).collect(Collectors.toList());


        Map<String, List<ChartResponse>> chartMap = new HashMap<>();

        addMissingEmptyMonth(months, approvedList, LocalDate.now().getYear(), Status.APPROVE);
        addMissingEmptyMonth(months, pendingList, LocalDate.now().getYear(), Status.SUBMIT);
        List<ChartResponse> chartResponseListCurrentYear = getChartResponses(approvedList, pendingList, LocalDate.now().getYear());

        addMissingEmptyMonth(months, approvedList1, LocalDate.now().getYear() - 1, Status.APPROVE);
        addMissingEmptyMonth(months, pendingList1, LocalDate.now().getYear() - 1 , Status.SUBMIT);
        List<ChartResponse> chartResponseListPreviousYear = getChartResponses(approvedList1, pendingList1, LocalDate.now().getYear() -1);
        chartMap.put("this-year", chartResponseListCurrentYear);
        chartMap.put("last-year", chartResponseListPreviousYear);


        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, chartMap));
    }

    private static List<ChartResponse> getChartResponses(final List<TimesheetChartAggregate> approvedList, final List<TimesheetChartAggregate> pendingList, int year) {
        List<ChartResponse> chartResponseList = new ArrayList<>();
        ChartResponse approved = new  ChartResponse("Approved", "line", (getTotalHrsByStatus(approvedList, year).toArray(Integer[]::new)));
        ChartResponse pending = new  ChartResponse("Pending", "column", (getTotalHrsByStatus(pendingList, year).toArray(Integer[]::new)));
        chartResponseList.add(approved);
        chartResponseList.add(pending);
        return chartResponseList;
    }

    private static List<Integer> getTotalHrsByStatus(final List<TimesheetChartAggregate> list, final int year) {
        List<Integer> totalHours = list.stream().filter(a -> Objects.equals(Integer.parseInt(a.get_id().getYear()), year)).sorted(Comparator.comparing(m -> Integer.parseInt(m.get_id().getMonth()))).map(hrs -> Integer.parseInt(hrs.getTotalHours())).collect(Collectors.toList());
        return totalHours;
    }

    private void addMissingEmptyMonth(final List<Integer> months, final List<TimesheetChartAggregate> list, final int year, final Status status) {
        List<Integer> monthsExist = list.stream().filter(a -> Objects.equals(Integer.parseInt(a.get_id().getYear()), year)).map(m -> Integer.parseInt(m.get_id().getMonth())).collect(Collectors.toList());
        Set<Integer>  missingMonth = findDifference(months, monthsExist);
        if (missingMonth.size() >0 ) {
            list.addAll(generateEmptyDataIfMissingMonths(missingMonth, year, status.name()));
        }
    }

    private <T> Set<T> findDifference(List<T> first, List<T> second)
    {
        return first.stream()
                .filter(i -> !second.contains(i))
                .collect(Collectors.toSet());
    }

    private List<TimesheetChartAggregate> generateEmptyDataIfMissingMonths(final  Set<Integer>  missingMonth, int year, String status) {

        List<TimesheetChartAggregate> emptyTimesheetChartAggregate = missingMonth.stream().map( tca -> new TimesheetChartAggregate(  new IdValue(tca.toString(), year+"", status), "0",0)).collect(Collectors.toList());
        return  emptyTimesheetChartAggregate;
    }
}
