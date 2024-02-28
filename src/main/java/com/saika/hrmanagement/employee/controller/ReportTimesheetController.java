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

import com.saika.hrmanagement.common.entity.ClientDetail;
import com.saika.hrmanagement.common.entity.EmployeeDetail;
import com.saika.hrmanagement.common.entity.EmployeeTimesheet;
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.common.model.ApplicationMailContract;
import com.saika.hrmanagement.common.payload.ReportClientRequest;
import com.saika.hrmanagement.common.payload.ReportEmployeeRequest;
import com.saika.hrmanagement.common.payload.ReportTimesheetRequest;
import com.saika.hrmanagement.common.payload.Status;
import com.saika.hrmanagement.employee.repository.ClientDetailRepository;
import com.saika.hrmanagement.employee.repository.EmployeeDetailRepository;
import com.saika.hrmanagement.employee.repository.EmployeeTimesheetRepository;
import com.saika.hrmanagement.employee.response.CustomApplicationResponse;
import com.saika.hrmanagement.employee.service.CsvService;
import com.saika.hrmanagement.employee.service.MailService;
import com.saika.hrmanagement.employee.service.impl.UserDetailsImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mani
 */
@RestController
@CrossOrigin
@Tag(name = "Report Timesheet", description = "The Report Management API")
@RequestMapping(path = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "Bearer Authentication")
public class ReportTimesheetController  implements CustomApplicationResponse {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private EmployeeTimesheetRepository employeeTimesheetRepository;

    @Autowired
    private EmployeeDetailRepository employeeDetailRepository;

    @Autowired
    private ClientDetailRepository clientDetailRepository;

    @Autowired
    private CsvService csvService;

    @Autowired
    @Qualifier("mailServiceImpl")
    private MailService mailService;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.sign}")
    private String sign;

    @Value("${app.email.location}")
    private String location;

    @Value("${app.email.subject.reminder}")
    private String reminder;

    /**
     * @param
     * @return
     */
    @PostMapping("/timesheet")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MANAGER', 'TIMESHEET_ADMIN')")
    public ResponseEntity<Resource> generateReportForEmployeeTimesheetByFilter(@Valid @RequestBody ReportTimesheetRequest reportTimesheetRequest) throws CustomApplicationException {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<ObjectId> ids = new ArrayList<>();
        if (Objects.nonNull(reportTimesheetRequest.getEmployeePkIds()) && !reportTimesheetRequest.getEmployeePkIds().isEmpty()) {
            ids = reportTimesheetRequest.getEmployeePkIds().stream().map(id -> new ObjectId(id)).collect(Collectors.toList());
        }
        List<Status> statuses = new ArrayList<>();
        //if(reportTimesheetRequest.getStatus() != null && reportTimesheetRequest.getStatus().size() >0 ) {
          //  reportTimesheetRequest.getStatus().stream().map(status -> statuses.add(status)).collect(Collectors.toList());
        //}

        if (reportTimesheetRequest.getApproved()) {
            statuses.add(Status.APPROVE);
        }
        if (reportTimesheetRequest.getRejected()) {
            statuses.add(Status.REJECT);
        }
        if (reportTimesheetRequest.getSubmitted()) {
            statuses.add(Status.SUBMIT);
        }
        if (reportTimesheetRequest.getSaved()) {
            statuses.add(Status.SAVE);
        }
        if (statuses.size() == 0) {
            statuses.add(Status.SUBMIT);
            statuses.add(Status.REJECT);
            statuses.add(Status.APPROVE);
        }
        List<EmployeeTimesheet> timesheetsByDate = null;
        if (!CollectionUtils.isEmpty(ids)) {
            timesheetsByDate = employeeTimesheetRepository.findTimesheetByDateOrEmployeeIdAndStatus(reportTimesheetRequest.getStartDate(), reportTimesheetRequest.getEndDate(), ids, statuses);
        } else {
            timesheetsByDate = employeeTimesheetRepository.findTimesheetByDateOrEmployeeAllAndStatus(reportTimesheetRequest.getStartDate(), reportTimesheetRequest.getEndDate(), statuses);
        }
         String fileName = user.getFirstName()+"_"+user.getLastName()+reportTimesheetRequest.getStartDate() +"_"+reportTimesheetRequest.getEndDate() + "_Timesheet.csv";
        final InputStreamResource resource = new InputStreamResource(csvService.loadTimesheet(timesheetsByDate));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,  "attachment; filename="+ fileName)
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }

    @PostMapping("/employee")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN', 'MANAGER')")
    public ResponseEntity<Resource> generateReportForEmployeeByFilter(@Valid @RequestBody ReportEmployeeRequest reportEmployeeRequest) throws CustomApplicationException {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ObjectId> ids = new ArrayList<>();
        if (Objects.nonNull(reportEmployeeRequest.getEmployeePkIds()) && !reportEmployeeRequest.getEmployeePkIds().isEmpty()) {
            ids = reportEmployeeRequest.getEmployeePkIds().stream().map(id -> new ObjectId(id)).collect(Collectors.toList());
        }

        List<EmployeeDetail> employeeDetailList = null;//employeeDetailRepository.findEmployeeByDateOrEmployeeIdOrStatus(reportEmployeeRequest.getStartDate(), reportEmployeeRequest.getEndDate(), ids, reportEmployeeRequest.getActive() == true ? reportEmployeeRequest.getActive() : null, reportEmployeeRequest.getEnrolled() == true ? reportEmployeeRequest.getEnrolled() : null , reportEmployeeRequest.getDeleted() == true ? reportEmployeeRequest.getDeleted() : null);
        if (!CollectionUtils.isEmpty(ids)) {
            employeeDetailList = employeeDetailRepository.findEmployeeByDateOrEmployeeIdAndStatus(reportEmployeeRequest.getStartDate(), reportEmployeeRequest.getEndDate(), ids, reportEmployeeRequest.getActive() == true ? reportEmployeeRequest.getActive() : null, reportEmployeeRequest.getEnrolled() == true ? reportEmployeeRequest.getEnrolled() : null , reportEmployeeRequest.getDeleted() == true ? reportEmployeeRequest.getDeleted() : null);
        } else {
            employeeDetailList = employeeDetailRepository.findEmployeeByDate(reportEmployeeRequest.getStartDate(), reportEmployeeRequest.getEndDate());
        }
        String fileName = user.getFirstName()+"_"+user.getLastName()+"_Employee"+ ".csv";
        final InputStreamResource resource = new InputStreamResource(csvService.loadEmployee(employeeDetailList));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,  "attachment; filename="+ fileName)
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);

    }


    @PostMapping("/employee/reminder")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN', 'MANAGER')")
    public ResponseEntity<Object> sendReminderEmailToEmployee(@Valid @RequestBody ReportEmployeeRequest reportEmployeeRequest) throws CustomApplicationException {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ObjectId> ids = new ArrayList<>();
        if (Objects.nonNull(reportEmployeeRequest.getEmployeePkIds()) && !reportEmployeeRequest.getEmployeePkIds().isEmpty()) {
            ids = reportEmployeeRequest.getEmployeePkIds().stream().map(id -> new ObjectId(id)).collect(Collectors.toList());
        }
        List<EmployeeDetail> employeeDetailList = null;
        if(!CollectionUtils.isEmpty(ids)) {
             employeeDetailList = employeeDetailRepository.fetchAllEmailByIdAndIsActive(ids);
        } else {
            employeeDetailList = employeeDetailRepository.findAllByIsActive();
        }

        List<String> emails = new ArrayList<>();
        if (Objects.nonNull(employeeDetailList) && !employeeDetailList.isEmpty()){
            emails = employeeDetailList.stream().map(e -> e.getEmail()).collect(Collectors.toList());
        }

        if (!emails.isEmpty()) {
            ApplicationMailContract mailContract = new ApplicationMailContract();
            mailContract.setFromEmail(fromEmail);
            mailContract.setSendTo(user.getEmail());
            mailContract.setBccEmail(emails.stream().collect(Collectors.joining(";")));
            mailContract.setSubject(Objects.nonNull(reminder) ? reminder : "Reminder Email!");
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("emailContent", reportEmployeeRequest.getEmailContent());
            model.put("sign", sign);
            model.put("location", location);
            mailContract.setEmailContent(model);
            mailContract.setHtmlTemplate("reminder-email");

            mailService.sendCustomMail(mailContract);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(constructSuccessApplicationResponse(HttpStatus.CREATED, "Email has been sent! As Requested!"));

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(constructSuccessApplicationResponse(HttpStatus.NOT_FOUND, "Email has not sent! As Requested!"));

    }

    @PostMapping("/client")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MANAGER', 'TIMESHEET_ADMIN')")
    public ResponseEntity<Object> generateReportForClientByFilter(@Valid @RequestBody ReportClientRequest reportClientRequest) throws CustomApplicationException {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ClientDetail> clientDetailList = clientDetailRepository.findAll();
        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, clientDetailList));
    }

}
