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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.saika.hrmanagement.common.entity.DateTaskComment;
import com.saika.hrmanagement.common.entity.EmployeeTimesheet;
import com.saika.hrmanagement.common.entity.EmployeeTimesheetDocument;
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.common.model.ApplicationMailContract;
import com.saika.hrmanagement.common.payload.EmployeeTimesheetRequest;
import com.saika.hrmanagement.common.payload.EmployeeTimesheetStatusRequest;
import com.saika.hrmanagement.common.payload.FindByIdRequest;
import com.saika.hrmanagement.common.payload.Status;
import com.saika.hrmanagement.employee.controller.util.EmployeeTimesheetUtil;
import com.saika.hrmanagement.employee.repository.EmployeeTimesheetDocumentRepository;
import com.saika.hrmanagement.employee.repository.EmployeeTimesheetRepository;
import com.saika.hrmanagement.employee.response.CustomApplicationResponse;
import com.saika.hrmanagement.employee.response.CustomSuccessApplicationResponse;
import com.saika.hrmanagement.employee.response.CustomSuccessPagableResponse;
import com.saika.hrmanagement.employee.response.PageableResponse;
import com.saika.hrmanagement.employee.service.MailService;
import com.saika.hrmanagement.employee.service.impl.EmployeeTimesheetServiceImpl;
import com.saika.hrmanagement.employee.service.impl.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mani
 */
@RestController
@CrossOrigin
@Tag(name = "Employee Timesheet", description = "The EmployeeTimesheet Management API")
@RequestMapping(path = "/ets", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "Bearer Authentication")
public class EmployeeTimesheetController extends EmployeeTimesheetUtil implements CustomApplicationResponse {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private EmployeeTimesheetServiceImpl employeeTimesheetService;

    @Autowired
    private EmployeeTimesheetRepository employeeTimesheetRepository;

    @Autowired
    private EmployeeTimesheetDocumentRepository employeeTimesheetDocumentRepository;

    @Autowired
    @Qualifier("mailServiceImpl")
    private MailService mailService;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.sign}")
    private String sign;

    @Value("${app.email.location}")
    private String location;

    /**
     * @param
     * @return
     */
    @Operation(summary = "Get Employee Timesheet Details by id", description = "Get Employee Timesheet Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = TimesheetResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> fetchEmployeeTimesheetById(@PathVariable("id") String employeeTimesheetId) throws CustomApplicationException{

        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, employeeTimesheetService.getEmployeeTimesheetById(employeeTimesheetId)));
    }

    @Operation(summary = "Get Employee Timesheet  by Employee id", description = "Get Employee Timesheet By Employee Id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = TimesheetResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PostMapping("/employee/id")
    public ResponseEntity<Object> fetchEmployeeTimesheetByEmployeeId(@RequestBody FindByIdRequest  employeeId) throws CustomApplicationException{

        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, employeeTimesheetRepository.findAllTimesheetByEmployeeId(new ObjectId(employeeId.getId()))));
    }

    @Operation(summary = "Get Employee Timesheet  by Employee id and from and to date", description = "Get Employee Timesheet  by Employee id and from and to date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = TimesheetResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PostMapping("/employee/id/from-to")
    public ResponseEntity<Object> fetchEmployeeTimesheetByEmployeeIdAndFromToDate(@RequestBody FindByIdRequest  employeeId) throws CustomApplicationException {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (Objects.nonNull(employeeId.getStart())   && Objects.nonNull(employeeId.getEnd())) {
            List<EmployeeTimesheet> employeeTimesheetList = employeeTimesheetRepository.findAllTimesheetByEmployeeIdAndFormAndToDate(new ObjectId(user.getId()), employeeId.getStart(), employeeId.getEnd());
            List<EmployeeTimesheet> filteredEmployeeList = employeeTimesheetList.stream().filter(employeeTimesheet ->
                    (
                            (employeeTimesheet.getIsTimesheetDeleted() == null || employeeTimesheet.getIsTimesheetDeleted() != true)
                            && !(employeeTimesheet.getStatus().equals(Status.REJECTED) || employeeTimesheet.getStatus().equals(Status.REJECT))
                    )
                    ).collect(Collectors.toList());

            EmployeeTimesheet employeeTimesheet = filteredEmployeeList.size() > 0 ? filteredEmployeeList.get(0) : null;
            return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, employeeTimesheet == null ? "" : employeeTimesheet));
        }
        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.BAD_REQUEST, "Bad Request"));
    }

    @Operation(summary = "Get All Employee Timesheet Details", description = "Get All Employee Timesheet Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeTimesheetPageableResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Object> fetchAllEmployeeTimesheet(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(defaultValue = "updatedOn", required = false) String sort,
                                                            @RequestParam(defaultValue = "DESC", required = false) String order,
                                                            @RequestParam(required = false)  String search) throws CustomApplicationException {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sort));
        Page<EmployeeTimesheet> employeeTimesheetPage = null;

        if (Objects.nonNull(search) && !Objects.equals(search.trim(), "")) {
            employeeTimesheetPage =  employeeTimesheetRepository.getAllTimesheetBySearch(search, pageable);
        } else {
            employeeTimesheetPage =  employeeTimesheetRepository.findAll(pageable);
        }

        List<EmployeeTimesheet>  employeeTimesheetList = employeeTimesheetPage.getContent();

        if (employeeTimesheetList.isEmpty()) {
            throw new CustomApplicationException(HttpStatus.NO_CONTENT, "No Client Data!");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", employeeTimesheetList);
        response.put("currentPage", employeeTimesheetPage.getNumber());
        response.put("totalCount", employeeTimesheetPage.getTotalElements());
        response.put("totalPages", employeeTimesheetPage.getTotalPages());
        response.put("currentPageSize", employeeTimesheetPage.getSize());

        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, response));
    }

    @Operation(summary = "Get Logged In Employee's All Timesheet Details", description = "Get Logged In Employee's All Timesheet Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeTimesheetPageableResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @GetMapping("/employee/all")
    public ResponseEntity<Object> fetchAllEmployeeTimesheetByUser(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size,
                                                                  @RequestParam(defaultValue = "firstName", required = false) String sort,
                                                                  @RequestParam(defaultValue = "DESC", required = false) String order,
                                                                  @RequestParam(required = false) String search) throws CustomApplicationException {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sort));
        Page<EmployeeTimesheet> employeeTimesheetPage = null;

        if (Objects.nonNull(search) && !Objects.equals(search.trim(), "")) {
            employeeTimesheetPage =  employeeTimesheetRepository.getAllEmployeeTimesheetBySearch(user.getId(), search, pageable);
        } else {
            employeeTimesheetPage =  employeeTimesheetRepository.findAllByEmployeeDetailId(user.getId(), pageable);
        }

        List<EmployeeTimesheet>  employeeTimesheetList = employeeTimesheetPage.getContent();

        if (employeeTimesheetList.isEmpty()) {
            throw new CustomApplicationException(HttpStatus.NO_CONTENT, "No EmployeeTimesheet Data!");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", employeeTimesheetList);
        response.put("currentPage", employeeTimesheetPage.getNumber());
        response.put("totalCount", employeeTimesheetPage.getTotalElements());
        response.put("totalPages", employeeTimesheetPage.getTotalPages());
        response.put("currentPageSize", employeeTimesheetPage.getSize());

        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, response));
    }

    @Operation(summary = "Get Logged In Employee's All Timesheet Details", description = "Get Logged In Employee's All Timesheet Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeTimesheetPageableResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @GetMapping("/employee/all/id")
    public ResponseEntity<Object> fetchEmployeeTimesheetsByEmpId(@RequestParam(defaultValue = "0") String empId,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size,
                                                                  @RequestParam(defaultValue = "firstName", required = false) String sort,
                                                                  @RequestParam(defaultValue = "DESC", required = false) String order,
                                                                  @RequestParam(required = false) String search) throws CustomApplicationException {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sort));
        Page<EmployeeTimesheet> employeeTimesheetPage = null;

        if (Objects.nonNull(search) && !Objects.equals(search.trim(), "")) {
            employeeTimesheetPage =  employeeTimesheetRepository.getAllEmployeeTimesheetBySearch(empId, search, pageable);
        } else {
            employeeTimesheetPage =  employeeTimesheetRepository.findEmployeeTimesheetsByEmpId(new ObjectId(empId), pageable);
        }

        List<EmployeeTimesheet>  employeeTimesheetList = employeeTimesheetPage.getContent();

        if (employeeTimesheetList.isEmpty()) {
            throw new CustomApplicationException(HttpStatus.NO_CONTENT, "No EmployeeTimesheet Data!");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", employeeTimesheetList);
        response.put("currentPage", employeeTimesheetPage.getNumber());
        response.put("totalCount", employeeTimesheetPage.getTotalElements());
        response.put("totalPages", employeeTimesheetPage.getTotalPages());
        response.put("currentPageSize", employeeTimesheetPage.getSize());

        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, response));
    }

    @Operation(summary = "Update Employee Timesheet Status", description = "Update Employee Timesheet Status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN')")
    @PutMapping("/status")
    public ResponseEntity<Object> updateEmployeeTimesheetStatus(@Valid @RequestBody EmployeeTimesheetStatusRequest employeeTimesheetStatusRequest) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmployeeTimesheet employeeTimesheetExist = employeeTimesheetService.getEmployeeTimesheetById(employeeTimesheetStatusRequest.getId());
        if (Objects.equals(user.getId(), employeeTimesheetExist.getCreatedBy())) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(constructFailureApplicationResponse(HttpStatus.NOT_ACCEPTABLE, "Trying to approve your own timesheet, which is not allowed!"));
        }
        employeeTimesheetExist.setStatus(employeeTimesheetStatusRequest.getStatus());
        employeeTimesheetExist.setApprovedBy(user.getFirstName() +" "+ user.getLastName() );
        employeeTimesheetExist.setApproverPkId(user.getId());
        employeeTimesheetExist.setUpdatedBy(user.getId());
        employeeTimesheetExist.setUpdatedOn(LocalDateTime.now());
        if (Objects.nonNull(employeeTimesheetStatusRequest.getReason()) && !employeeTimesheetStatusRequest.getReason().isEmpty()) {
            employeeTimesheetExist.setComments(employeeTimesheetStatusRequest.getReason());
        }
        //todo we can move all email into one component!
        ApplicationMailContract mailContract = new ApplicationMailContract();
        mailContract.setFromEmail(fromEmail);
        mailContract.setCcEmail(user.getEmail());
        if (Objects.nonNull(employeeTimesheetExist.getEmployeeDetail().getEmail())) {
            mailContract.setSendTo(employeeTimesheetExist.getEmployeeDetail().getEmail());
        }
        if (employeeTimesheetExist.getStatus().name().equalsIgnoreCase("APPROVE")) {
            mailContract.setSubject("Timesheet Approved for the period From: "+employeeTimesheetExist.getStart() +" To: "+employeeTimesheetExist.getEnd());
            mailContract.setHtmlTemplate("timesheet-approve-email");
        } else if(employeeTimesheetExist.getStatus().name().equalsIgnoreCase("REJECT")) {
            mailContract.setSubject("Timesheet Rejected for the period From: "+employeeTimesheetExist.getStart() +" To: "+employeeTimesheetExist.getEnd());
            mailContract.setHtmlTemplate("timesheet-reject-email");
        }
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", employeeTimesheetExist.getEmployeeName());
        model.put("totalHours", employeeTimesheetExist.getTotalHrs());
        model.put("timesheet", employeeTimesheetExist);
        if (Objects.nonNull(employeeTimesheetExist.getComments()) && !employeeTimesheetExist.getComments().isEmpty()) {
            model.put("comments", employeeTimesheetExist.getComments());
        }
        if (Objects.nonNull(employeeTimesheetExist.getDateTaskComment())) {
            model.put("dateTaskComments", employeeTimesheetExist.getDateTaskComment());
        }
        model.put("sign", sign);
        model.put("location", location);
        mailContract.setEmailContent(model);

        try {
            mailService.sendCustomMailWithAttachment(mailContract, employeeTimesheetExist);
        } catch (MessagingException e) {
           e.printStackTrace();
        }

        return ResponseEntity.accepted().body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, employeeTimesheetService.updateEmployeeTimesheet(employeeTimesheetExist)));
    }

    @Operation(summary = "Soft Delete Employee Timesheet Details", description = "Soft Delete Employee Timesheet Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = TimesheetStringResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> softDeleteEmployeeTimesheet(@PathVariable("id") String clientId)  throws CustomApplicationException{
        EmployeeTimesheet employeeTimesheetExist = employeeTimesheetService.getEmployeeTimesheetById(clientId);
        if (employeeTimesheetExist.getStatus().equals(Status.APPROVE)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(constructFailureApplicationResponse(HttpStatus.NOT_ACCEPTABLE, "Trying to delete your approved timesheet, which is not allowed!"));
        }
        employeeTimesheetService.deleteEmployeeTimesheet(employeeTimesheetExist);
        return ResponseEntity.accepted().body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, "Employee Timesheet has successfully deleted!"));
    }

    @Operation(summary = "Add Employee Timesheet Details", description = "Add Required Employee Timesheet Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeTimesheetPageableResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @Transactional
    @PostMapping(value = "/plus-file-upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> addEmployeeTimesheetUpload(@Valid @RequestPart("etsRequest") String etsRequest, @RequestParam(name="files", required=false) MultipartFile[] files)  throws CustomApplicationException {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmployeeTimesheetRequest employeeTimesheetRequest = null;
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            employeeTimesheetRequest = mapper.readValue(etsRequest, EmployeeTimesheetRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructSuccessApplicationResponse(HttpStatus.BAD_REQUEST, "Invalid Request, Unable Parse Request!"));
        }
        //start date should be before end date or same day
        if (employeeTimesheetRequest.getEnd().isBefore(employeeTimesheetRequest.getStart())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Please check start and end date range!"));
        }
        if (Objects.nonNull(employeeTimesheetRequest.getDateTaskComment())) {
            //validate task date, and it should be with in start and end date
            List<LocalDate> localDates = employeeTimesheetRequest.getDateTaskComment().stream().map(DateTaskComment:: getDate).collect(Collectors.toList());
            for(LocalDate taskDate: localDates) {
                if (taskDate.isBefore(employeeTimesheetRequest.getStart()) || taskDate.isAfter(employeeTimesheetRequest.getEnd())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Please check task date range, not within start and end range!"));
                }
            }
        }
        //validate date range
        List<EmployeeTimesheet> employeeTimesheetInRange = employeeTimesheetRepository.findByDateBetween(employeeTimesheetRequest.getStart(), employeeTimesheetRequest.getEnd(), new ObjectId(user.getId())).stream()
                .filter(
                        isRejected ->  !(Objects.equals(isRejected.getIsTimesheetDeleted(), true)) &&
                                (!Objects.equals(isRejected.getStatus(), Status.REJECTED)) && !Objects.equals(isRejected.getStatus(), Status.REJECT))
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(employeeTimesheetInRange)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Already you have submitted timesheet for these dates, please check and submit again!"));
        }

        EmployeeTimesheet employeeTimesheetCreate = createUpdateEmployeeTimesheet(employeeTimesheetRequest, null);
        EmployeeTimesheet employeeTimesheet = employeeTimesheetService.createEmployeeTimesheet(employeeTimesheetCreate);
        List<EmployeeTimesheetDocument> etsDocs = createUpdateEmployeeTimesheetDocument(employeeTimesheet, files);
        employeeTimesheetDocumentRepository.saveAll(etsDocs);
        return ResponseEntity.status(HttpStatus.CREATED).body(constructSuccessApplicationResponse(HttpStatus.CREATED, employeeTimesheetService.getEmployeeTimesheetById(employeeTimesheet.getId())));
    }

    @Operation(summary = "Update Employee Timesheet Details", description = "Update Employee Timesheet Details.")
    @Transactional
    @PutMapping(value = "/plus-file-upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> updateEmployeeTimesheetUpload(@Valid @RequestPart("etsRequest") String etsRequest, @RequestPart(value = "existingFileIds", required = false) String existingFileIds, @RequestParam(name="files", required=false) MultipartFile[] files) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmployeeTimesheetRequest employeeTimesheetRequest = null;
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            employeeTimesheetRequest = mapper.readValue(etsRequest, EmployeeTimesheetRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Invalid Request, Unable Parse Request!"));
        }
        String[] fileIds = null;
        if (Objects.nonNull(existingFileIds)) {
            try{
                ObjectMapper mapperIds = new ObjectMapper();
                mapperIds.findAndRegisterModules();
                fileIds = mapperIds.readValue(existingFileIds, String[].class);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Invalid Request, Unable Parse Request!"));
            }
        }

        //start date should be before end date or same day
        if (employeeTimesheetRequest.getEnd().isBefore(employeeTimesheetRequest.getStart())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Please check start and end date range!"));
        }
        if (Objects.nonNull(employeeTimesheetRequest.getDateTaskComment())) {
            //validate task date, and it should be with in start and end date
            List<LocalDate> localDates = employeeTimesheetRequest.getDateTaskComment().stream().map(DateTaskComment:: getDate).collect(Collectors.toList());
            for(LocalDate taskDate: localDates) {
                if (taskDate.isBefore(employeeTimesheetRequest.getStart()) || taskDate.isAfter(employeeTimesheetRequest.getEnd())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Please check task date range, not within start and end range!"));
                }
            }
        }

        //validate date range since it is save and update need to make sure the date range
        List<EmployeeTimesheet> employeeTimesheetInRange = employeeTimesheetRepository.findByDateBetween(employeeTimesheetRequest.getStart(), employeeTimesheetRequest.getEnd(), new ObjectId(user.getId()))
                .stream().filter(saved -> Objects.equals(saved.getStatus(), Status.APPROVE)).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(employeeTimesheetInRange)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructSuccessApplicationResponse(HttpStatus.BAD_REQUEST, "Already you have submitted timesheet for these dates, please check and submit again!"));
        }

        EmployeeTimesheet employeeTimesheetExist = employeeTimesheetService.getEmployeeTimesheetById(employeeTimesheetRequest.getId());

        if (Objects.isNull(employeeTimesheetExist)) {
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(constructFailureApplicationResponse(HttpStatus.NO_CONTENT, "You are trying to update invalid records..."));
        }

        if (Objects.equals(employeeTimesheetExist.getIsTimesheetDeleted(), true)
                || Objects.equals(employeeTimesheetExist.getStatus().name().toLowerCase(), "Approve".toLowerCase())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(constructFailureApplicationResponse(HttpStatus.CONFLICT, "This timesheet can not be updated, submit new one!"));
        }

        if (!CollectionUtils.isEmpty(employeeTimesheetExist.getEmployeeTimesheetDocument())) {
            List<String> idExists = employeeTimesheetExist.getEmployeeTimesheetDocument().stream().map(EmployeeTimesheetDocument::getId).collect(Collectors.toList());
            List<String> incomingExistingIds = new ArrayList<String>();
            if(Objects.nonNull(fileIds)) {
                 incomingExistingIds = Arrays.asList(fileIds);
            }
            List<String> finalIncomingExistingIds = incomingExistingIds;
            List<String> removedIds = idExists.stream()
                    .filter(e -> !finalIncomingExistingIds.contains(e))
                    .collect(Collectors.toList());
          if (removedIds.size() > 0) {
              employeeTimesheetDocumentRepository.deleteAllById(removedIds);
          }

        }

        EmployeeTimesheet employeeTimesheetUpdate = createUpdateEmployeeTimesheet(employeeTimesheetRequest, employeeTimesheetExist);
        EmployeeTimesheet employeeTimesheet = employeeTimesheetService.createEmployeeTimesheet(employeeTimesheetUpdate);
        List<EmployeeTimesheetDocument> etsDocs = createUpdateEmployeeTimesheetDocument(employeeTimesheet, files);
        employeeTimesheetDocumentRepository.saveAll(etsDocs);

        return ResponseEntity.accepted().body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, employeeTimesheetService.getEmployeeTimesheetById(employeeTimesheet.getId())));
    }



    /**
     * this class created for open api documentation purpose
     */
    private static class TimesheetResponse extends CustomSuccessApplicationResponse<EmployeeTimesheet> {
        public EmployeeTimesheet responseData;
    }
    private static class TimesheetStringResponse extends CustomSuccessApplicationResponse<String> {
        public String responseData;
    }

    private static class EmployeeTimesheetPageableResponse extends CustomSuccessPagableResponse<PageableResponse<List<EmployeeTimesheet>>> {
        public PageableResponse<List<EmployeeTimesheet>> responseData;
    }

}
