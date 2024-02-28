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

import com.saika.hrmanagement.common.entity.EmployeeDetail;
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.common.model.ApplicationMailContract;
import com.saika.hrmanagement.common.payload.*;
import com.saika.hrmanagement.common.util.JwtTokenUtil;
import com.saika.hrmanagement.employee.repository.ClientDetailRepository;
import com.saika.hrmanagement.employee.repository.RoleRepository;
import com.saika.hrmanagement.employee.repository.UserDetailRepository;
import com.saika.hrmanagement.employee.response.CustomApplicationResponse;
import com.saika.hrmanagement.employee.response.CustomSuccessApplicationResponse;
import com.saika.hrmanagement.employee.response.CustomSuccessPagableResponse;
import com.saika.hrmanagement.employee.response.PageableResponse;
import com.saika.hrmanagement.employee.service.MailService;
import com.saika.hrmanagement.employee.service.impl.EmployeeDetailServiceImpl;
import com.saika.hrmanagement.employee.service.impl.UserDetailsImpl;
import com.saika.hrmanagement.employee.service.impl.util.EmployeeDetailRequestResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mani
 */
@RestController
@CrossOrigin
@Tag(name = "Employee", description = "The Employee API")
@RequestMapping(path = "/employee", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "Bearer Authentication")
public class EmployeeController  extends EmployeeDetailRequestResponseUtil implements CustomApplicationResponse {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private EmployeeDetailServiceImpl employeeDetailService;

    @Autowired
    private ClientDetailRepository clientDetailRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    @Qualifier("mailServiceImpl")
    private MailService mailService;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.subject.enrollment}")
    private String employeeEnrollment;

    @Value("${app.email.sign}")
    private String sign;

    @Value("${app.email.location}")
    private String location;

    @Value("${app.hostname}")
    private String appHostname;

    /**
     * @param
     * @return
     */
    @Operation(summary = "Get Employee Details by login", description = "Get Employee Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @GetMapping
    public ResponseEntity<Object> fetchEmployeeDetailById() throws CustomApplicationException{
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, employeeDetailService.getEmployeeDetailById(user.getId())));
    }

    @Operation(summary = "Get Employee Details by id ", description = "Get Employee Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> fetchEmployeeById(@PathVariable("id") String employeePkId) throws CustomApplicationException{

        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, employeeDetailService.getEmployeeDetailById(employeePkId)));
    }

    @Operation(summary = "Get Employee Details by id ", description = "Get Employee Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PostMapping("/id")
    public ResponseEntity<Object> fetchEmployeeById(final FindByIdRequest findByIdRequest) throws CustomApplicationException{

        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, employeeDetailService.getEmployeeDetailById(findByIdRequest.getId())));
    }

    /**
     * @param
     * @return
     */
    @Operation(summary = "Get Employee Details by id", description = "Get Employee Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @GetMapping("/profile")
    public ResponseEntity<Object> fetchEmployeeDetailProfileByLogin() throws CustomApplicationException{
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, employeeDetailService.getEmployeeDetailById(user.getId())));
    }

    @Operation(summary = "Get All Employee Details", description = "Get All Employee Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeDetailPageableResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Object> fetchAllEmployeeDetail(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam(defaultValue = "firstName",required = false) String sort,
                                                         @RequestParam(defaultValue = "ASC", required = false) String order,
                                                         @RequestParam(required = false) String search) throws CustomApplicationException {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sort));
        Page<EmployeeDetail> employeeDetailPage = employeeDetailService.getAllEmployeeDetails(search, pageable);
        List<EmployeeDetail>  EmployeeDetailList = employeeDetailPage.getContent();

        if (EmployeeDetailList.isEmpty()) {
            throw new CustomApplicationException(HttpStatus.NO_CONTENT, "No Client Data!");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", EmployeeDetailList);
        response.put("currentPage", employeeDetailPage.getNumber());
        response.put("totalCount", employeeDetailPage.getTotalElements());
        response.put("totalPages", employeeDetailPage.getTotalPages());
        response.put("currentPageSize", employeeDetailPage.getSize());

        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, response));
    }


    @Operation(summary = "Get All Employee Details for Client Mapping", description = "Get All Employee Details for Client Mapping.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeDetailPageableResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN')")
    @GetMapping("/all-active-emp")
    public ResponseEntity<Object> fetchAllEmployeeDetailForClientMapping() throws CustomApplicationException {

        List<EmployeeDetail> employeeDetailList = userDetailRepository.findAll();
        List<EmployeeDetail> filteredEmployee = null;
        if (!CollectionUtils.isEmpty(employeeDetailList)) {
            filteredEmployee = employeeDetailList.stream().filter(e -> Objects.equals(e.getIsActive(), true)).collect(Collectors.toList());
        }
        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, filteredEmployee));
    }

    @Operation(summary = "Get All Employee Details for Client Mapping", description = "Get All Employee Details for Client Mapping.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeDetailPageableResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN')")
    @GetMapping("/all-emp-no-pagination")
    public ResponseEntity<Object> fetchAllEmployeeswithoutPagination() throws CustomApplicationException {
        List<EmployeeDetail> employeeDetailList = userDetailRepository.findAll();
        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, employeeDetailList));
    }

    @Operation(summary = "Add Employee Details", description = "Add Required Employee Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN')")
    @PostMapping
    public ResponseEntity<Object> addEmployeeDetail(@Valid @RequestBody EmployeeDetailRequest employeeDetailRequest)  throws CustomApplicationException {
        List<EmployeeDetail> checkIfRegister = userDetailRepository.findAllByEmail(employeeDetailRequest.getEmail()).stream().filter(isDel -> !Objects.equals(isDel.getIsDelete(), true)).collect(Collectors.toList());
        if (!checkIfRegister.isEmpty()) {
            //if (checkIfRegister.stream().anyMatch(exist -> Objects.nonNull(exist.getIsActive()) && exist.getIsActive() == true)) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Email is already taken!"));
           // }
        }
        String enrollToken = jwtTokenUtil.generateActiveUserToken();
        EmployeeDetail employeeDetail = createUpdateEmployeeDetail(employeeDetailRequest, null, roleRepository, null, enrollToken);
       /*
        Optional<ClientDetail> clientDetails = clientDetailRepository.findById(employeeDetailRequest.getClientId());
        if (clientDetails.isPresent()) {
            employeeDetail.setClientDetail(clientDetails.get());
        }
        */
        EmployeeDetail employeeDetailCreate = employeeDetailService.createEmployeeDetail(employeeDetail);
        if (Objects.nonNull(employeeDetailCreate)) {
            try {
                ApplicationMailContract mailContract = new ApplicationMailContract();
                mailContract.setFromEmail(fromEmail);
                mailContract.setSendTo(employeeDetailCreate.getEmail());
                mailContract.setSubject(employeeEnrollment);
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("name", employeeDetailCreate.getFirstName() + " " + employeeDetailCreate.getLastName());
                model.put("url", appHostname + "/#/confirm-enroll?enrollToken=" + enrollToken);
                model.put("sign", sign);
                model.put("location", location);
                mailContract.setEmailContent(model);
                mailContract.setHtmlTemplate("enrolled-email");
                mailService.sendCustomMail(mailContract);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Sending email to enroll email address failed while registering user {} ", e.getCause());
                throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "While Sending Email Failed! Please Contact Admin!");
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(constructSuccessApplicationResponse(HttpStatus.CREATED, employeeDetailCreate));
    }

    @Operation(summary = "Update Employee Details", description = "Update Employee Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PutMapping
    public ResponseEntity<Object> updateEmployeeDetail(@Valid @RequestBody EmployeeDetailRequest updateEmployeeDetailRequest) {
        EmployeeDetail employeeDetailExist = employeeDetailService.getEmployeeDetailById(updateEmployeeDetailRequest.getId());
        EmployeeDetail employeeDetail = createUpdateEmployeeDetail(updateEmployeeDetailRequest, employeeDetailExist, roleRepository, null, null);
        return ResponseEntity.accepted().body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, employeeDetailService.updateEmployeeDetail(employeeDetail)));
    }

    @Operation(summary = "Update Employee Profile", description = "Update Employee Profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PutMapping("/profile")
    public ResponseEntity<Object> updateEmployeeProfile(@Valid @RequestBody EmployeeProfileRequest employeeProfileRequest) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmployeeDetail employeeDetailExist = employeeDetailService.getEmployeeDetailById(user.getId());
        EmployeeDetail employeeDetail = createUpdateEmployeeProfile(employeeProfileRequest, employeeDetailExist);
        return ResponseEntity.accepted().body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, employeeDetailService.updateEmployeeDetail(employeeDetail)));
    }

    @Operation(summary = "Soft Delete Employee Details", description = "Soft Delete Employee Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Object> softDeleteEmployeeDetail(@PathVariable("id") String id)  throws CustomApplicationException{
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmployeeDetail employeeDetailExist = employeeDetailService.getEmployeeDetailById(id);
        if (Objects.equals(user.getId(), employeeDetailExist.getId())) {
            return ResponseEntity.badRequest().body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "You can not delete your own data and it will impact current login!"));
        }
        employeeDetailService.deleteEmployeeDetail(employeeDetailExist);
        return ResponseEntity.accepted().body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, "Employee has successfully deleted!"));
    }


    @Operation(summary = "Soft Delete Employee Details", description = "Soft Delete Employee Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PutMapping("/terminate-employee")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Object> terminateEmployeeDetail(@Valid @RequestBody EmployeeReEnrollOrTerminateRequest terminateEmployeeDetailRequest)  throws CustomApplicationException{
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmployeeDetail employeeDetailExist = employeeDetailService.getEmployeeDetailById(terminateEmployeeDetailRequest.getEmployeeId());
        if (Objects.equals(user.getId(), employeeDetailExist.getId())) {
            return ResponseEntity.badRequest().body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "You can not delete your own data and it will impact current login!"));
        }
        return ResponseEntity.accepted().body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, employeeDetailService.reEnrollOrTerminateEmployee(employeeDetailExist, terminateEmployeeDetailRequest)));
    }

    @Operation(summary = "Update Password by Email", description = "Update Password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PutMapping(path = "/update-password")
    @Transactional
    public ResponseEntity<?> updatePassword(@Valid @RequestBody final SetupPasswordRequest setupPasswordRequest)  {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmployeeDetail employeeDetail = userDetailRepository.findByEmployeeId(user.getId());
        try {
            if (employeeDetail == null) {
                throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "User Not Found or Session Expired ");
            }
            employeeDetail.setPassword(new BCryptPasswordEncoder().encode(setupPasswordRequest.getPassword()));
            employeeDetail.setUpdatedOn(LocalDateTime.now());
            employeeDetail.setUpdatedBy(user.getId());
            userDetailRepository.save(employeeDetail);
        } catch (Exception e) {
            log.error("ValidateResetLink Failed ", e.getCause());
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Password Reset Failed! " +  e.getMessage() );
        }
        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, "Updated the password. Please login with new password"));

    }

    @Operation(summary = "Resend Enrollment Invite by Email", description = "Resend Enrollment Invite by Email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN')")
    @PutMapping(path = "/resend-enroll")
    @Transactional
    public ResponseEntity<?> resendEnrollInvite(@Valid @RequestBody final FindByIdRequest findByIdRequest)  {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmployeeDetail employeeDetail = userDetailRepository.findByEmployeeId(findByIdRequest.getId());
        String enrollToken = jwtTokenUtil.generateActiveUserToken();
        try {
            if (employeeDetail == null) {
                throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "User Not Found or Deleted! ");
            }
            if (Objects.equals(employeeDetail.getIsDelete(), true) && !Objects.equals(employeeDetail.getIsEnrolled(), null)) {
                throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "User may be deleted or alredy enrolled! Please Enroll again! ");
            }
            try {
                ApplicationMailContract mailContract = new ApplicationMailContract();
                mailContract.setFromEmail(fromEmail);
                mailContract.setSendTo(employeeDetail.getEmail());
                mailContract.setSubject("Resend Invite : "+ employeeEnrollment);
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("name", employeeDetail.getFirstName() + " " + employeeDetail.getLastName());
                model.put("url", appHostname + "/#/confirm-enroll?enrollToken=" + enrollToken);
                model.put("sign", sign);
                model.put("location", location);
                mailContract.setEmailContent(model);
                mailContract.setHtmlTemplate("enrolled-email");
                mailService.sendCustomMail(mailContract);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Re-Sending email to enroll email address failed while registering user {} ", e.getCause());
                throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "While Re-Sending Email Failed! Please Contact Admin!");
            }
            employeeDetail.setEnrollToken(enrollToken);
            employeeDetail.setUpdatedOn(LocalDateTime.now());
            employeeDetail.setUpdatedBy(user.getId());
            userDetailRepository.save(employeeDetail);
        } catch (Exception e) {
            log.error("resendEnrollInvite Failed ", e.getCause());
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Resend enrollment Failed! " +  e.getMessage() );
        }
        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, "Resend enrollment successful!"));

    }


    @Operation(summary = "Add Employee Details", description = "Add Required Employee Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN')")
    @PostMapping("/re-enroll")
    public ResponseEntity<Object> reEnrollEmployee(@Valid @RequestBody EmployeeReEnrollOrTerminateRequest employeeReEnrollOrTerminateRequest)  throws CustomApplicationException {
        EmployeeDetail  employeeDetailCheck = userDetailRepository.findByEmployeeId(employeeReEnrollOrTerminateRequest.getEmployeeId());
        //findAllByEmail(employeeDetailRequest.getEmail()).stream().filter(isDel -> !Objects.equals(isDel.getIsDelete(), true)).collect(Collectors.toList());
        if (Objects.isNull(employeeDetailCheck) || !employeeDetailCheck.getIsDelete()) {
            //if (checkIfRegister.stream().anyMatch(exist -> Objects.nonNull(exist.getIsActive()) && exist.getIsActive() == true)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Employee not terminated or Employee is active "));
            // }
        }
        String enrollToken = jwtTokenUtil.generateActiveUserToken();
        EmployeeDetailRequest employeeDetailRequest = constructEmployeeFromExistingRecord(employeeDetailCheck);
        employeeDetailRequest.setDateOfJoin(employeeReEnrollOrTerminateRequest.getDateOfEmployment());
        if (Objects.nonNull(employeeReEnrollOrTerminateRequest.getComments())) {
            employeeDetailRequest.setComments(employeeReEnrollOrTerminateRequest.getComments());
        }
        EmployeeDetail employeeDetail = createUpdateEmployeeDetail(employeeDetailRequest, null, roleRepository, null, enrollToken);
       /*
        Optional<ClientDetail> clientDetails = clientDetailRepository.findById(employeeDetailRequest.getClientId());
        if (clientDetails.isPresent()) {
            employeeDetail.setClientDetail(clientDetails.get());
        }
        */
        EmployeeDetail employeeDetailCreate = employeeDetailService.createEmployeeDetail(employeeDetail);
        if (Objects.nonNull(employeeDetailCreate)) {
            try {
                ApplicationMailContract mailContract = new ApplicationMailContract();
                mailContract.setFromEmail(fromEmail);
                mailContract.setSendTo(employeeDetailCreate.getEmail());
                mailContract.setSubject(employeeEnrollment);
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("name", employeeDetailCreate.getFirstName() + " " + employeeDetailCreate.getLastName());
                model.put("url", appHostname + "/#/confirm-enroll?enrollToken=" + enrollToken);
                model.put("sign", sign);
                model.put("location", location);
                mailContract.setEmailContent(model);
                mailContract.setHtmlTemplate("enrolled-email");
                mailService.sendCustomMail(mailContract);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Sending email to enroll email address failed while registering user {} ", e.getCause());
                throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "While Sending Email Failed! Please Contact Admin!");
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(constructSuccessApplicationResponse(HttpStatus.CREATED, employeeDetailCreate));
    }


    /**
     * this class created for open api documentation purpose
     */
    private static class EmployeeResponse extends CustomSuccessApplicationResponse<EmployeeDetail> {
        public EmployeeDetail responseData;
    }
    private static class EmployeeStringResponse extends CustomSuccessApplicationResponse<String> {
        public String responseData;
    }

    private static class EmployeeDetailPageableResponse extends CustomSuccessPagableResponse<PageableResponse<List<EmployeeDetail>>> {
        public PageableResponse<List<EmployeeDetail>> responseData;
    }

}
