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
import com.saika.hrmanagement.common.payload.EmployeeDetailRequest;
import com.saika.hrmanagement.common.util.JwtTokenUtil;
import com.saika.hrmanagement.employee.controller.helper.ExcelHelper;
import com.saika.hrmanagement.employee.repository.ClientDetailRepository;
import com.saika.hrmanagement.employee.repository.RoleRepository;
import com.saika.hrmanagement.employee.repository.UserDetailRepository;
import com.saika.hrmanagement.employee.response.CustomApplicationResponse;
import com.saika.hrmanagement.employee.response.CustomSuccessApplicationResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
public class ExcelController extends EmployeeDetailRequestResponseUtil implements CustomApplicationResponse {

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

    @Operation(summary = "Add Employee Details By Excel File", description = "Add Required Employee Details By file upload Excel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN')")
    @PostMapping("/bulk/file-upload")
    public ResponseEntity<Object> processEmployeeByExcelFileUpload(@RequestParam("file") MultipartFile file)  throws CustomApplicationException {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> successList = new ArrayList();
        List<String> errorEmailExistList = new ArrayList();
        List<String> errorSendingEmailList = new ArrayList();
        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                List<EmployeeDetailRequest> employeeDetailRequestList = ExcelHelper.excelToEmployeeRequestList(file.getInputStream());

                for (EmployeeDetailRequest employeeDetailRequest: employeeDetailRequestList) {
                    List<EmployeeDetail> checkIfRegister = userDetailRepository.findAllByEmail(employeeDetailRequest.getEmail()).stream().filter(isDel -> !Objects.equals(isDel.getIsDelete(), true)).collect(Collectors.toList());
                    if (!checkIfRegister.isEmpty()) {
                        errorEmailExistList.add(employeeDetailRequest.getEmail());
                        continue;
                    }
                    String enrollToken = jwtTokenUtil.generateActiveUserToken();
                    EmployeeDetail employeeDetail = createUpdateEmployeeDetail(employeeDetailRequest, null, roleRepository, null, enrollToken);

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
                            errorSendingEmailList.add(employeeDetail.getEmail());
                            log.error("Sending email to enroll email address failed while registering user {} ", e.getCause());
                            //throw new CustomApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "While Sending Email Failed! Please Contact Admin!");
                        }
                    }
                    successList.add(employeeDetailRequest.getEmail());
                }
            } catch (Exception e) {
                String message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(constructFailureApplicationResponse(HttpStatus.EXPECTATION_FAILED, message));

            }
        }
        if (errorEmailExistList.isEmpty() && errorSendingEmailList.isEmpty() && successList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(constructSuccessApplicationResponse(HttpStatus.CREATED, "Nothing processed! No Content"));
        }
        Map<String, List<String>> processedList = new HashMap<>();
        processedList.put("errorSendingEmail", errorSendingEmailList);
        processedList.put("errorEmailExist", errorEmailExistList);
        processedList.put("successList", successList);

        try {
            ApplicationMailContract mailContract = new ApplicationMailContract();
            mailContract.setFromEmail(fromEmail);
            mailContract.setSendTo(user.getEmail());
            mailContract.setSubject("Uploaded File has been processed "+file.getOriginalFilename());
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("name", user.getFirstName() + " " + user.getLastName());
            model.put("fileName", file.getOriginalFilename());
            model.put("sign", processedList);
            model.put("processed", successList);
            model.put("exist", errorEmailExistList);
            model.put("failure", errorSendingEmailList);
            model.put("location", location);
            mailContract.setEmailContent(model);
            mailContract.setHtmlTemplate("employee-fileupload-email");
            mailService.sendCustomMail(mailContract);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception occured while sending email to admin or who processed this files {} ", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(constructFailureApplicationResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(constructSuccessApplicationResponse(HttpStatus.CREATED, processedList));
    }

    private static class EmployeeResponse extends CustomSuccessApplicationResponse<EmployeeDetail> {
        public EmployeeDetail responseData;
    }

}
