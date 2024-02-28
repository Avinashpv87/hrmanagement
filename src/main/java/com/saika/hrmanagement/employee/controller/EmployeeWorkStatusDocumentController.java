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
import com.saika.hrmanagement.common.entity.EmployeeWorkStatusDocument;
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.common.payload.EmployeeWorkStatusDocumentRequest;
import com.saika.hrmanagement.employee.controller.util.EmployeeWorkStatusDocumentUtil;
import com.saika.hrmanagement.employee.repository.EmployeeWorkStatusDocumentRepository;
import com.saika.hrmanagement.employee.response.CustomApplicationResponse;
import com.saika.hrmanagement.employee.response.CustomSuccessApplicationResponse;
import com.saika.hrmanagement.employee.response.CustomSuccessPagableResponse;
import com.saika.hrmanagement.employee.response.PageableResponse;
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

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author mani
 */
@RestController
@CrossOrigin
@Tag(name = "EmployeeWorkStatusDocument", description = "The Employee Work Status Document Management API")
@RequestMapping(path = "/work-status-document", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "Bearer Authentication")
public class EmployeeWorkStatusDocumentController extends EmployeeWorkStatusDocumentUtil implements CustomApplicationResponse {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private EmployeeWorkStatusDocumentRepository employeeWorkStatusDocumentRepository;


    @Operation(summary = "Get Employee Work Status Document Details by id", description = "Get Employee Work Status Document Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeWorkStatusDocumentResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @GetMapping("/employee/all")
    public ResponseEntity<Object> fetchEmployeeWorkStatusDocumentEmp(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size,
                                                                     @RequestParam(defaultValue = "createdOn") String sort,
                                                                     @RequestParam(defaultValue = "DESC")String order,
                                                                     @RequestParam(required = false)  String search){
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sort));
        Page<EmployeeWorkStatusDocument> employeeWorkStatusDocumentPageable = employeeWorkStatusDocumentRepository.findByEmployeePklId(new ObjectId(user.getId()), pageable);
        List<EmployeeWorkStatusDocument>  employeeWorkStatusDocumentList = employeeWorkStatusDocumentPageable.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("data", employeeWorkStatusDocumentList);
        response.put("currentPage", employeeWorkStatusDocumentPageable.getNumber());
        response.put("totalCount", employeeWorkStatusDocumentPageable.getTotalElements());
        response.put("totalPages", employeeWorkStatusDocumentPageable.getTotalPages());
        response.put("currentPageSize", employeeWorkStatusDocumentPageable.getSize());

        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, response));

    }

    @Operation(summary = "Get Employee Work Status Document Details by id", description = "Get Employee Work Status Document Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeWorkStatusDocumentResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @GetMapping("/employee/all/valid")
    public ResponseEntity<Object> fetchEmployeeWorkStatusDocumentValid(){
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<EmployeeWorkStatusDocument> employeeWorkStatusDocumentList = employeeWorkStatusDocumentRepository.findAllEmployeePklId(new ObjectId(user.getId()));
        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, employeeWorkStatusDocumentList));

    }

    @Operation(summary = "Get Employee Work Status Document Details by id", description = "Get Employee Work Status Document Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeWorkStatusDocumentResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> fetchEmployeeWorkStatusDocumentById(@PathVariable("id") String id){
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmployeeWorkStatusDocument employeeWorkStatusDocument = employeeWorkStatusDocumentRepository.findById(id).orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND,   id +"Not Records Found"));
        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, employeeWorkStatusDocument));
    }

    @Operation(summary = "Get Employee Work Status Document Details by id", description = "Get Employee Work Status Document Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeWorkStatusDocumentResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/employee/{id}")
    public ResponseEntity<Object> fetchEmployeeWorkStatusDocumentByEmpId(@PathVariable("id") String empId){
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<EmployeeWorkStatusDocument> employeeWorkStatusDocument = employeeWorkStatusDocumentRepository.findAllEmployeePklId(new ObjectId(empId));
        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, employeeWorkStatusDocument));
    }

    @Operation(summary = "Get All Employee Work Status Document Details", description = "Get All Employee Work Status Document Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeWorkStatusDocumentPageableResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Object> fetchAllEmployeeWorkStatusDocument(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size,
                                                                     @RequestParam(defaultValue = "firstName") String sort,
                                                                     @RequestParam(defaultValue = "DESC")String order,
                                                                     @RequestParam(required = false)  String search){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sort));
        Page<EmployeeWorkStatusDocument> employeeWorkStatusDocumentPageable = employeeWorkStatusDocumentRepository.findAll(pageable);
        List<EmployeeWorkStatusDocument>  employeeWorkStatusDocumentList = employeeWorkStatusDocumentPageable.getContent();

        if (employeeWorkStatusDocumentList.isEmpty()) {
            throw new CustomApplicationException(HttpStatus.NO_CONTENT, "No Employee Work Status Document Data!");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", employeeWorkStatusDocumentList);
        response.put("currentPage", employeeWorkStatusDocumentPageable.getNumber());
        response.put("totalCount", employeeWorkStatusDocumentPageable.getTotalElements());
        response.put("totalPages", employeeWorkStatusDocumentPageable.getTotalPages());
        response.put("currentPageSize", employeeWorkStatusDocumentPageable.getSize());

        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, response));
    }


    @Operation(summary = "Add Employee Work Status Document Details", description = "Add Required Employee Work Status Document Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeWorkStatusDocumentResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @Transactional
    @PostMapping(value = "/file-upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> addEmployeeWorkStatusDocUpload(@Valid @RequestPart("workStatusDocumentRequest") String workStatusDocumentRequest,
                                                                 @RequestParam(name="files", required=false) MultipartFile files)  throws CustomApplicationException {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmployeeWorkStatusDocumentRequest employeeWorkStatusDocumentRequest = null;
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            employeeWorkStatusDocumentRequest = mapper.readValue(workStatusDocumentRequest, EmployeeWorkStatusDocumentRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructSuccessApplicationResponse(HttpStatus.BAD_REQUEST, "Invalid Request, Unable Parse Request!"));
        }
        if (Objects.nonNull(employeeWorkStatusDocumentRequest.getId()) && !Objects.equals(employeeWorkStatusDocumentRequest.getId(), "") && !Objects.equals(employeeWorkStatusDocumentRequest.getId(), "0") ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Trying to do invalid operation!"));
        }
        //start date should be before end date or same day
        if (employeeWorkStatusDocumentRequest.getEndDate().isBefore(employeeWorkStatusDocumentRequest.getStartDate())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Please check start and end date range!"));
        }

        //todo
        //validate date range
        List<EmployeeWorkStatusDocument> employeeWSDRange = employeeWorkStatusDocumentRepository.findByDateBetween(employeeWorkStatusDocumentRequest.getStartDate(), employeeWorkStatusDocumentRequest.getEndDate(), employeeWorkStatusDocumentRequest.getWorkAuthType(), employeeWorkStatusDocumentRequest.getDocType(), new ObjectId(user.getId())).stream().filter(ews ->
                (!Objects.equals(ews.getIsDeleted(), Boolean.TRUE))).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(employeeWSDRange)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Already you have uploaded documents for these dates & doc type, please check again!"));
        }
        //default employee doc should be visible
        employeeWorkStatusDocumentRequest.setVisibleToEmployee(Boolean.TRUE);
        EmployeeWorkStatusDocument employeeWorkStatusDocument = createUpdateEmployeeWorkStatusDocument(employeeWorkStatusDocumentRequest, null, files, false);
        EmployeeWorkStatusDocument res = employeeWorkStatusDocumentRepository.save(employeeWorkStatusDocument);
        return ResponseEntity.status(HttpStatus.CREATED).body(constructSuccessApplicationResponse(HttpStatus.CREATED, res));
    }

    @Operation(summary = "Add Employee Work Status Document Details", description = "Add Required Employee Work Status Document Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeWorkStatusDocumentResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    @PostMapping(value = "/employer/file-upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> uploadEmployeeWorkStatusByEmployer(@Valid @RequestPart("workStatusDocumentRequest") String workStatusDocumentRequest,
                                                                 @RequestParam(name="files", required=false) MultipartFile files)  throws CustomApplicationException {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmployeeWorkStatusDocumentRequest employeeWorkStatusDocumentRequest = null;
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            employeeWorkStatusDocumentRequest = mapper.readValue(workStatusDocumentRequest, EmployeeWorkStatusDocumentRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructSuccessApplicationResponse(HttpStatus.BAD_REQUEST, "Invalid Request, Unable Parse Request!"));
        }
        if (Objects.isNull(employeeWorkStatusDocumentRequest.getEmployeeId()) || Objects.equals(employeeWorkStatusDocumentRequest.getEmployeeId(), "") || Objects.equals(employeeWorkStatusDocumentRequest.getId(), "0") ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Trying to do invalid operation! Employee Id Empty"));
        }
        //start date should be before end date or same day
        if (employeeWorkStatusDocumentRequest.getEndDate().isBefore(employeeWorkStatusDocumentRequest.getStartDate())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Please check start and end date range!"));
        }

        //todo
        //validate date range
        List<EmployeeWorkStatusDocument> employeeWSDRange = employeeWorkStatusDocumentRepository.findByDateBetween(employeeWorkStatusDocumentRequest.getStartDate(), employeeWorkStatusDocumentRequest.getEndDate(), employeeWorkStatusDocumentRequest.getWorkAuthType(), employeeWorkStatusDocumentRequest.getDocType(), new ObjectId(employeeWorkStatusDocumentRequest.getEmployeeId())).stream().filter(ews ->
                (!Objects.equals(ews.getIsDeleted(), Boolean.TRUE))).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(employeeWSDRange)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Already you have uploaded documents for these dates & doc type, please check again!"));
        }
        EmployeeWorkStatusDocument employeeWorkStatusDocument = createUpdateEmployeeWorkStatusDocument(employeeWorkStatusDocumentRequest, null, files, true);
        EmployeeWorkStatusDocument res = employeeWorkStatusDocumentRepository.save(employeeWorkStatusDocument);
        return ResponseEntity.status(HttpStatus.CREATED).body(constructSuccessApplicationResponse(HttpStatus.CREATED, res));
    }

    @Operation(summary = "Update Employee Work Status Document Details", description = "Update Required Employee Work Status Document Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeWorkStatusDocumentResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @Transactional
    @PutMapping(value = "/file-upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> updateEmployeeWorkStatusDocUpload(@Valid @RequestPart("workStatusDocumentRequest") String workStatusDocumentRequest,
                                                                    @RequestParam(name="files", required=false) MultipartFile files)  throws CustomApplicationException {
        final EmployeeWorkStatusDocumentRequest employeeWorkStatusDocumentRequest;
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            employeeWorkStatusDocumentRequest = mapper.readValue(workStatusDocumentRequest, EmployeeWorkStatusDocumentRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructSuccessApplicationResponse(HttpStatus.BAD_REQUEST, "Invalid Request, Unable Parse Request!"));
        }
        if (Objects.isNull(employeeWorkStatusDocumentRequest.getId()) || Objects.equals(employeeWorkStatusDocumentRequest.getId(), "") ||  Objects.equals(employeeWorkStatusDocumentRequest.getId(), "0")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Trying to do invalid update operation!"));
        }
        //start date should be before end date or same day
        if (employeeWorkStatusDocumentRequest.getEndDate().isBefore(employeeWorkStatusDocumentRequest.getStartDate())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Please check start and end date range!"));
        }
        EmployeeWorkStatusDocument employeeWorkStatusDocumentExist = employeeWorkStatusDocumentRepository.findById(employeeWorkStatusDocumentRequest.getId()).orElseThrow(() -> new CustomApplicationException(HttpStatus.NO_CONTENT,  employeeWorkStatusDocumentRequest.getId() + " Not Found"));
        // do soft delete or delete and create...
        //employeeWorkStatusDocumentRepository.deleteById(employeeWorkStatusDocumentExist.getId());
        EmployeeWorkStatusDocument employeeWorkStatusDocument = createUpdateEmployeeWorkStatusDocument(employeeWorkStatusDocumentRequest, employeeWorkStatusDocumentExist, files, false);
        EmployeeWorkStatusDocument resUpdate = employeeWorkStatusDocumentRepository.save(employeeWorkStatusDocument);
        return ResponseEntity.status(HttpStatus.CREATED).body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, resUpdate));
    }

    @Operation(summary = "Soft Delete Employee Work Status Document Details", description = "Soft Delete Employee Work Status Document Details.")
    //@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> softDeteEmployeeWorkStatusDocument(@PathVariable("id") String id) {
        EmployeeWorkStatusDocument employeeWorkStatusDocument = employeeWorkStatusDocumentRepository.findById(id).orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND,  id + "Not Found"));
        employeeWorkStatusDocument.setIsDeleted(true);
        EmployeeWorkStatusDocument employeeWorkStatusDocumentUpdated = employeeWorkStatusDocumentRepository.save(employeeWorkStatusDocument);
        return ResponseEntity.accepted().body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, employeeWorkStatusDocumentUpdated));
    }

    /**
     * this class created for open api documentation purpose
     */
    private static class EmployeeWorkStatusDocumentResponse extends CustomSuccessApplicationResponse<EmployeeWorkStatusDocument> {
        public EmployeeWorkStatusDocument responseData;
    }

    private static class EmployeeWorkStatusDocumentPageableResponse extends CustomSuccessPagableResponse<PageableResponse<List<EmployeeWorkStatusDocument>>> {
        public PageableResponse<List<EmployeeWorkStatusDocument>> responseData;
    }

}
