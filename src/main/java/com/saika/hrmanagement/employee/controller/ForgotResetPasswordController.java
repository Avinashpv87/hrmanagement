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
import com.saika.hrmanagement.common.payload.ForgotPasswordRequest;
import com.saika.hrmanagement.common.payload.ResetPasswordRequest;
import com.saika.hrmanagement.common.payload.SetupUserPasswordRequest;
import com.saika.hrmanagement.common.util.JwtTokenUtil;
import com.saika.hrmanagement.employee.controller.util.JwtAuthenticationControllerUtil;
import com.saika.hrmanagement.employee.repository.UserDetailRepository;
import com.saika.hrmanagement.employee.response.CustomApplicationResponse;
import com.saika.hrmanagement.employee.response.CustomSuccessApplicationResponse;
import com.saika.hrmanagement.employee.service.MailService;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
public class ForgotResetPasswordController extends JwtAuthenticationControllerUtil implements CustomApplicationResponse {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

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

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Operation(summary = "Forgot Password by Email", description = "Forgot Password by User Email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = ForgotResetPasswordResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PostMapping(path = "/forgot-password")
    @Transactional
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody final ForgotPasswordRequest forgotPasswordRequest) {
        String email = forgotPasswordRequest.getEmail();
        Optional<EmployeeDetail> userOptional = Optional.ofNullable(userDetailRepository.findByEmailAndIsActive(email, true));
        if (userOptional.isPresent()) {
            EmployeeDetail user = userOptional.get();
            try {
                String token = jwtTokenUtil.generateEmailJwtToken();
                ApplicationMailContract mailContract = new ApplicationMailContract();
                mailContract.setFromEmail(fromEmail);
                mailContract.setSendTo(email);
                mailContract.setSubject(passwordResetSubject);

                Map<String, Object> model = new HashMap<String, Object>();
                model.put("name", user.getFirstName() + " " + user.getLastName());
                model.put("username", user.getUserName());
                model.put("url", appHostname + "/#/reset-password?token=" + token);
                model.put("sign", sign);
                model.put("location", location);
                mailContract.setEmailContent(model);
                mailContract.setHtmlTemplate("password-reset");

                // update token
                user.setPasswordResetToken(token);
                user.setUpdatedOn(LocalDateTime.now());
                userDetailRepository.save(user);
                mailService.sendCustomMail(mailContract);

                return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, "Reset password email has sent successfully!"));
            } catch (Exception e) {
                e.printStackTrace();
                log.error("forgotPassword Failed {} ", e.getCause());
               throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "ForgotPassword Failed, Reason :" + e.getMessage());
            }
        }
       throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "User " + email + " Not Found ");
    }

    @Operation(summary = "Reset Password by Email", description = "Reset Password by User Email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = ForgotResetPasswordResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PutMapping(path = "/reset-password")
    @Transactional
    public ResponseEntity<?> validateResetLink(@Valid @RequestBody final ResetPasswordRequest resetPasswordRequest) {
        boolean isValidToken = jwtTokenUtil.validateToken(resetPasswordRequest.getToken());
        if (isValidToken) {
            EmployeeDetail user = null;
            if (Objects.isNull(resetPasswordRequest.getPassword())) {
                throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Please give a valid password!");
            }
            if (null != resetPasswordRequest.getPassword()) {
                user = userDetailRepository.findByPasswordResetToken(resetPasswordRequest.getToken());
                try {
                    if (user == null) {
                        throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "User Token Not Found or Expired or Invalid ");
                    }
                    user.setPassword(new BCryptPasswordEncoder().encode(resetPasswordRequest.getPassword()));
                    user.setPasswordResetToken(null);
                    userDetailRepository.save(user);
                } catch (Exception e) {
                    log.error("ValidateResetLink Failed ", e.getCause());
                    throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Password Reset Failed! " +  e.getMessage() );
                }
            }
            return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, "Found User and updated the password. Please login with new password"));
        }
        throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Link May be Expired or Required Field Missing");
    }


    @Operation(summary = "Setup Password by Email", description = "Setup Password by User Email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = ForgotResetPasswordResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PutMapping(path = "/setup-password")
    @Transactional
    public ResponseEntity<?> validateAndSetupUserAndPassword(@Valid @RequestBody final SetupUserPasswordRequest setupUserPasswordRequest) {
        boolean isValidToken = jwtTokenUtil.validateToken(setupUserPasswordRequest.getToken());
        if (isValidToken) {
            EmployeeDetail user = null;
            if (Objects.isNull(setupUserPasswordRequest.getPassword())) {
                throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Please give a valid password!");
            }
            if (null != setupUserPasswordRequest.getPassword()) {
                user = userDetailRepository.findByEnrollToken(setupUserPasswordRequest.getToken());
                if (user == null) {
                    throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "User Token Not Found or Expired or Invalid ");
                }
                final String userEmail = user.getEmail();
                try {
                    List<EmployeeDetail> checkIfUserNameOrEmailTaken =  userDetailRepository.findAllByUserNameOrEmail(setupUserPasswordRequest.getUserName(), user.getEmail());
                    if (!checkIfUserNameOrEmailTaken.isEmpty()) {
                        if (checkIfUserNameOrEmailTaken.stream().anyMatch(exist ->
                                ((Objects.equals(exist.getEmail(), userEmail) || Objects.equals(exist.getUserName(), setupUserPasswordRequest.getUserName()))
                                        && Objects.equals(exist.getIsActive(), true)
                                ))) {
                            return ResponseEntity
                                    .status(HttpStatus.BAD_REQUEST)
                                    .body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Error: Username/Email is already taken!"));
                        }
                    }
                    user.setUserName(setupUserPasswordRequest.getUserName());
                    user.setPassword(new BCryptPasswordEncoder().encode(setupUserPasswordRequest.getPassword()));
                    user.setIsActive(Boolean.TRUE);
                    user.setEnrollToken(null);
                    userDetailRepository.save(user);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("ValidateResetLink Failed ", e.getCause());
                    throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Password Reset Failed! " +  e.getMessage() );
                }
            }
            return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, "Enrolled Successful! Please login with User Name and Password!"));
        }
        throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Link May be Expired or Required Field Missing");
    }

    /**
     * this class created for open api documentation purpose
     */
    private static class ForgotResetPasswordResponse extends CustomSuccessApplicationResponse<String> {
        public String responseData;
    }
}
