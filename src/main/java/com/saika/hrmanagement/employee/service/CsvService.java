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
package com.saika.hrmanagement.employee.service;

import com.saika.hrmanagement.common.entity.EmployeeDetail;
import com.saika.hrmanagement.common.entity.EmployeeTimesheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author mani
 */
@Slf4j
@Service
public class CsvService {
    private static final String[] TIMESHEET_HEADERS = {"Name", "Period Start", "Period End", "Email", "Total Hours", "Status", "Approved By", "Created On", "UpdatedOn" };
    private static final String[] EMPLOYEE_HEADERS = {"ID", "FirstName", "Last Name", "Email", "Primary PhoneNumber",
            "DateOfJoin", "Designation", "WorkAuthType", "WageType", "WageStatus",
            "Comments", "AddressLine1", "AddressLine2", "City",
            "State", "Country",
            "Created On", "Updated On", "ReasonForExit", "Active", "Deleted", "Enrolled"};

    private static final CSVFormat TIMESHEET_HEADERS_FORMAT = CSVFormat.DEFAULT.withHeader(TIMESHEET_HEADERS);
    private static final CSVFormat EMPLOYEE_HEADERS_FORMAT = CSVFormat.DEFAULT.withHeader(EMPLOYEE_HEADERS);

    //load data into csv
    public ByteArrayInputStream loadTimesheet(final List<EmployeeTimesheet> timesheetList) {
        return writeDataToCsv(timesheetList);
    }

    public ByteArrayInputStream loadEmployee(final List<EmployeeDetail> employeeDetails) {
        return writeEmployeeDataToCsv(employeeDetails);
    }

    //write data to csv
    private ByteArrayInputStream writeDataToCsv(final List<EmployeeTimesheet> timesheetList) {
        log.info("Writing data to the csv printer");
        try (final ByteArrayOutputStream stream = new ByteArrayOutputStream();
             final CSVPrinter printer = new CSVPrinter(new PrintWriter(stream), TIMESHEET_HEADERS_FORMAT)) {
            for (final EmployeeTimesheet timesheet : timesheetList) {
                final List<String> data = Arrays.asList(
                        String.valueOf(timesheet.getEmployeeName()),
                        String.valueOf(timesheet.getStart()),
                        String.valueOf(timesheet.getEnd()),
                        String.valueOf(timesheet.getEmployeeDetail().getEmail()),
                        String.valueOf(timesheet.getTotalHrs()),
                        String.valueOf(timesheet.getStatus()+"ED"),
                        String.valueOf(timesheet.getApprovedBy()),
                        String.valueOf(timesheet.getCreatedOn()),
                        String.valueOf(timesheet.getUpdatedOn()));
                printer.printRecord(data);
            }
            printer.flush();
            return new ByteArrayInputStream(stream.toByteArray());
        } catch (final IOException e) {
            throw new RuntimeException("Csv writing error: " + e.getMessage());
        }
    }

    //write data to csv
    private ByteArrayInputStream writeEmployeeDataToCsv(final List<EmployeeDetail> employeeDetails) {
        log.info("Writing data to the csv printer");
        try (final ByteArrayOutputStream stream = new ByteArrayOutputStream();
             final CSVPrinter printer = new CSVPrinter(new PrintWriter(stream), EMPLOYEE_HEADERS_FORMAT)) {
            for (final EmployeeDetail employeeDetail : employeeDetails) {
                final List<String> data = Arrays.asList(
                        String.valueOf(employeeDetail.getEmployeeIdentityNumber()),
                        String.valueOf(employeeDetail.getFirstName()),
                        String.valueOf(employeeDetail.getLastName()),
                        String.valueOf(employeeDetail.getEmail()),
                        String.valueOf(employeeDetail.getPrimaryPhoneNumber()),
                        String.valueOf(employeeDetail.getDateOfJoin()),
                        String.valueOf(Objects.nonNull(employeeDetail.getDesignation()) ? employeeDetail.getDesignation() : "-"),
                        String.valueOf(Objects.nonNull(employeeDetail.getWorkAuthType()) ? employeeDetail.getWorkAuthType() : "-"),
                        String.valueOf(Objects.nonNull(employeeDetail.getWageType()) ? employeeDetail.getWageType() : "-"),
                        String.valueOf(Objects.nonNull(employeeDetail.getWorkStatus()) ? employeeDetail.getWorkStatus() : "-"),
                        String.valueOf(Objects.nonNull(employeeDetail.getComments()) ? employeeDetail.getComments() : "-"),
                        String.valueOf(Objects.nonNull(employeeDetail.getAddressLine1()) ? employeeDetail.getAddressLine1() : "-"),
                        String.valueOf(Objects.nonNull(employeeDetail.getAddressLine2()) ? employeeDetail.getAddressLine2() : "-"),
                        String.valueOf(Objects.nonNull(employeeDetail.getCity()) ? employeeDetail.getCity() : "-"),
                        String.valueOf(Objects.nonNull(employeeDetail.getState()) ? employeeDetail.getState() : "-"),
                        String.valueOf(Objects.nonNull(employeeDetail.getCountry()) ? employeeDetail.getCountry() : "-"),
                        String.valueOf(Objects.nonNull(employeeDetail.getCreatedOn()) ? employeeDetail.getCreatedOn() : "-"),
                        String.valueOf(Objects.nonNull(employeeDetail.getUpdatedOn()) ? employeeDetail.getUpdatedOn() : "-"),
                        String.valueOf(Objects.nonNull(employeeDetail.getTerminationReason()) ? employeeDetail.getTerminationReason() : "-"),
                        String.valueOf(Objects.nonNull(employeeDetail.getIsActive()) ? employeeDetail.getIsActive() : "-"),
                        String.valueOf(Objects.nonNull(employeeDetail.getIsDelete()) ? employeeDetail.getIsDelete() : "-"),
                        String.valueOf(Objects.nonNull(employeeDetail.getIsEnrolled()) ? employeeDetail.getIsEnrolled() : "-")
                        );
                printer.printRecord(data);
            }
            printer.flush();
            return new ByteArrayInputStream(stream.toByteArray());
        } catch (final IOException e) {
            throw new RuntimeException("Csv writing error: " + e.getMessage());
        }
    }

}
