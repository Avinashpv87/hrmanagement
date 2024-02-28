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
import com.saika.hrmanagement.common.payload.SignupRequest;
import com.saika.hrmanagement.common.util.JwtTokenUtil;
import com.saika.hrmanagement.employee.controller.util.JwtAuthenticationControllerUtil;
import com.saika.hrmanagement.employee.repository.UserDetailRepository;
import com.saika.hrmanagement.employee.response.CustomApplicationResponse;
import com.saika.hrmanagement.employee.response.CustomSuccessApplicationResponse;
import com.saika.hrmanagement.employee.response.EmployeeDetailResponse;
import com.saika.hrmanagement.employee.service.MailService;
import com.saika.hrmanagement.employee.service.impl.RegisterServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Mani
 */
@RestController
@CrossOrigin
@Tag(name = "User", description = "The User Management API")
public class RegisterController extends JwtAuthenticationControllerUtil implements CustomApplicationResponse {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RegisterServiceImpl registerService;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    @Qualifier("mailServiceImpl")
    private MailService mailService;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.subject.reset}")
    private String passwordResetSubject;

    @Value("${app.email.subject.register}")
    private String activateUserSubject;

    @Value("${app.email.sign}")
    private String sign;

    @Value("${app.email.location}")
    private String location;

    @Value("${app.hostname}")
    private String appHostname;

    /**
     *
     * @param request
     * @return
     */
    @Operation(summary = "Register", description = "Registration Timesheet App")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequest request) {

        List<EmployeeDetail> checkIfUserNameOrEmailTaken =  userDetailRepository.findAllByUserNameOrEmail(request.getUserName(), request.getEmail());
        if (!checkIfUserNameOrEmailTaken.isEmpty()) {
            if (checkIfUserNameOrEmailTaken.stream().anyMatch(exist -> ((Objects.equals(exist.getEmail(), request.getEmail()) || Objects.equals(exist.getUserName(), request.getUserName())) && exist.getIsActive()))) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Error: Username/Email is already taken!"));
            }
        }
        String activeUserToken = jwtTokenUtil.generateActiveUserToken();
        EmployeeDetailResponse employeeDetailResponse = registerService.registerUser(request, activeUserToken);
        if (null == employeeDetailResponse) {
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Registration Failed! Please Contact Administrator!");
        }
        try {
            ApplicationMailContract mailContract = new ApplicationMailContract();
            mailContract.setFromEmail(fromEmail);
            mailContract.setSendTo(request.getEmail());
            mailContract.setSubject(activateUserSubject);
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("name", request.getFirstName() + " " + request.getLastName());
            model.put("url", appHostname + "/#/activate-user?activeUserToken=" + activeUserToken);
            model.put("sign", sign);
            model.put("location", location);
            mailContract.setEmailContent(model);
            mailContract.setHtmlTemplate("activate-user-email");
            mailService.sendCustomMail(mailContract);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Sending email to registered email address failed while registering user {} ", e.getCause());
            throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "While Sending Email Failed! Please Contact Admin!");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(constructSuccessApplicationResponse(HttpStatus.CREATED, "User Successfully Registered and Email has been sent to the registered email address!"));
    }

    /**
     *
     * @param activeToken
     * @return
     */
    @Operation(summary = "Activate User by Token", description = "Activate User by Email Token After Registratin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PutMapping(path = "/activate-user")
    @Transactional
    public ResponseEntity<Object> activateUser(@RequestParam(value = "activeUserToken", required = true) String activeToken) {
        boolean isValidToken = jwtTokenUtil.validateToken(activeToken);
        if (isValidToken) {
            Optional<EmployeeDetail> userPresent = Optional.ofNullable(userDetailRepository.findByIsActiveToken(activeToken));
            if (userPresent.isPresent()) {
                EmployeeDetail user = userPresent.get();
                user.setIsActive(Boolean.TRUE);
                user.setIsActiveToken(null);
                user.setUpdatedOn(LocalDateTime.now());
                userDetailRepository.save(user);
                return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, "User is activated successfully. Please Login!"));
            } else {
                throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Error: Link may expired or already activated!");
            }
        }
        throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Error: Invalid Token or Expired!");
    }

    /**
     *
     * @param enrollToken
     * @return
     */
    @Operation(summary = "Enroll User by Token", description = "Enroll User by Email Token After Registratin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = RegisterController.RegisterResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PutMapping(path = "/confirm-enroll")
    @Transactional
    public ResponseEntity<Object> enrollEmployee(@RequestParam(value = "enrollToken", required = true) String enrollToken) {
        boolean isValidToken = jwtTokenUtil.validateToken(enrollToken);
        //String passwordSetupToken = jwtTokenUtil.generateEmailJwtToken();
        if (isValidToken) {
            Optional<EmployeeDetail> userPresent = Optional.ofNullable(userDetailRepository.findByEnrollToken(enrollToken));
            if (userPresent.isPresent()) {
                EmployeeDetail user = userPresent.get();
                user.setIsActive(null);
                user.setIsEnrolled(Boolean.TRUE);
                user.setUpdatedOn(LocalDateTime.now());
                userDetailRepository.save(user);
                return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, "User is enrolled successfully. Please set your password!"));
            } else {
                throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Error: Link may expired or already activated!");
            }
        }
        throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Error: Invalid Token or Expired!");
    }

    /**
     * this class created for open api documentation purpose
     */
    private static class RegisterResponse extends CustomSuccessApplicationResponse<String> {
        public String responseData;
    }

}
