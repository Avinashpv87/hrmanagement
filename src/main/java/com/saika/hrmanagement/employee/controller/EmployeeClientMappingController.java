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

import com.saika.hrmanagement.common.entity.EmployeeClientMapping;
import com.saika.hrmanagement.common.entity.EmployeeTimesheet;
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.common.payload.CommonStatusRequest;
import com.saika.hrmanagement.common.payload.EmployeeClientMappingRequest;
import com.saika.hrmanagement.common.payload.Status;
import com.saika.hrmanagement.employee.controller.util.EmployeeClientMappingUtil;
import com.saika.hrmanagement.employee.repository.EmployeeClientMappingRepository;
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
import org.apache.commons.lang3.StringUtils;
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
@Tag(name = "EmployeeClientMapping", description = "The Employee Client Mapping Management API")
@RequestMapping(path = "/hrm", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "Bearer Authentication")
public class EmployeeClientMappingController extends EmployeeClientMappingUtil implements CustomApplicationResponse {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private EmployeeClientMappingRepository employeeClientMappingRepository;

    /**
     * @param
     * @return
     */
    @Operation(summary = "Get Employee Client Mapping Details by id", description = "Get Employee Client Mapping Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeClientMappingResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN')")
    @GetMapping("/client-mapping/{id}")
    public ResponseEntity<Object> fetchEmployeeClientMappingByClientId(@PathVariable("id") String id){
        EmployeeClientMapping clientMappingOptional = employeeClientMappingRepository.findById(id).orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND,  id + "Not Found"));
        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, clientMappingOptional));
    }

    @Operation(summary = "Get All Employee Client Mapping Details", description = "Get All Employee Client Mapping Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeClientMappingPageableResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN')")
    @GetMapping("/client-mappings")
    public ResponseEntity<Object> fetchAllEmployeeClientMapping(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(defaultValue = "createdOn", required = false) String sort,
                                                                @RequestParam(defaultValue = "DESC", required = false)String order,
                                                                @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sort));

        Page<EmployeeClientMapping> employeeClientMappingPageable = null;

        if (Objects.nonNull(search) && !Objects.equals(search.trim(), "")) {
            employeeClientMappingPageable =  employeeClientMappingRepository.getAllClientsBySearch(search, pageable);
        } else {
            employeeClientMappingPageable =  employeeClientMappingRepository.findAll(pageable);
        }

        List<EmployeeClientMapping>  employeeClientMappingList = employeeClientMappingPageable.getContent();

        if (employeeClientMappingList.isEmpty()) {
            throw new CustomApplicationException(HttpStatus.NO_CONTENT, "No Employee Client Mapping Data!");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", employeeClientMappingList);
        response.put("currentPage", employeeClientMappingPageable.getNumber());
        response.put("totalCount", employeeClientMappingPageable.getTotalElements());
        response.put("totalPages", employeeClientMappingPageable.getTotalPages());
        response.put("currentPageSize", employeeClientMappingPageable.getSize());

        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, response));
    }

    @Operation(summary = "Add Employee Client Mapping Details", description = "Add Required Employee Client Mapping Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeClientMappingResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN')")
    @Transactional
    @PostMapping("/client-mapping")
    public ResponseEntity<Object> addEmployeeClientMapping(@Valid @RequestBody EmployeeClientMappingRequest employeeClientMappingRequest)  {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (Objects.nonNull(employeeClientMappingRequest) && Objects.nonNull(employeeClientMappingRequest.getId()) && !StringUtils.isEmpty(employeeClientMappingRequest.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Request Invalid! You trying to invalid operation!"));
        }
         //start date should be before end date or same day
        if (employeeClientMappingRequest.getEndDate().isBefore(employeeClientMappingRequest.getStartDate())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Please check start and end date range!"));
        }

        List<EmployeeClientMapping> employeeClientMappingInRange  = employeeClientMappingRepository.findByDateBetweenForEmployeeClient(employeeClientMappingRequest.getStartDate(),
                employeeClientMappingRequest.getEndDate(), new ObjectId(employeeClientMappingRequest.getEmployeePkId()),
                new ObjectId(employeeClientMappingRequest.getClientPkId()));

        //validate date range
        List<EmployeeClientMapping> employeeClientMappingFilter =
                employeeClientMappingInRange
                .stream().filter(isDeleted ->
                (!Objects.equals(isDeleted.getIsDeleted(), true))).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(employeeClientMappingFilter)) {
            List<EmployeeClientMapping> sameClientList = employeeClientMappingFilter.stream().filter(clientId -> Objects.equals(clientId.getClientDetail().getId(), employeeClientMappingRequest.getClientPkId())).collect(Collectors.toList());
            int sumTotalHrs = employeeClientMappingFilter.stream().mapToInt(projAlloc -> projAlloc.getProjectAllocation()).sum();
            if (sumTotalHrs >= 100 || (sumTotalHrs + employeeClientMappingRequest.getProjectAllocation()) > 100 || !CollectionUtils.isEmpty(sameClientList)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Already Employee Mapped with same period or allocation reached 100%, please check and map again!"));
            }
        }
        /* //todo later if needed!
        else {
            List<EmployeeClientMapping> employeeClientMappingProjAllocation  = employeeClientMappingRepository.findByEmployeeDetailId(new ObjectId(employeeClientMappingRequest.getEmployeePkId()));
            //project allocation!
            List<EmployeeClientMapping> employeeProjectAllocationFilter =
                    employeeClientMappingProjAllocation
                            .stream().filter(isDeleted ->
                                    (!Objects.equals(isDeleted.getIsDeleted(), true))).collect(Collectors.toList());
        }*/
        EmployeeClientMapping employeeClientMappingCreate = createUpdateEmployeeClientMappingDetail(employeeClientMappingRequest, null);
        EmployeeClientMapping employeeClientMapping = employeeClientMappingRepository.save(employeeClientMappingCreate);
        EmployeeClientMapping employeeClientMappingCreated = null;
        if (Objects.nonNull(employeeClientMapping)) {
            employeeClientMappingCreated = employeeClientMappingRepository.findById(employeeClientMapping.getId()).orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND, employeeClientMapping.getId() + "Not Found"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(constructSuccessApplicationResponse(HttpStatus.CREATED, employeeClientMappingCreated));
    }

    @Operation(summary = "Update Employee Client Mapping Details", description = "Update Employee Client Mapping Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeClientMappingResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN')")
    @PutMapping("/client-mapping")
    public ResponseEntity<Object> updateEmployeeClientMapping(@Valid @RequestBody EmployeeClientMappingRequest employeeClientMappingRequest) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmployeeClientMapping clientMappingExist = employeeClientMappingRepository.findById(employeeClientMappingRequest.getId()).orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND,  employeeClientMappingRequest.getId() + "Not Found"));
        if (Objects.nonNull(clientMappingExist.getIsContractEnded())
            || Objects.equals(clientMappingExist.getIsContractEnded(), true)
            || Objects.equals(clientMappingExist.getIsDeleted(), true) ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.ACCEPTED, "Trying to update invalid client mapping! Please try valid one or contact admin!"));
        }
        //start date should be before end date or same day
        if (employeeClientMappingRequest.getEndDate().isBefore(employeeClientMappingRequest.getStartDate())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Please check start and end date range!"));
        }

        EmployeeClientMapping employeeClientMappingCreate = createUpdateEmployeeClientMappingDetail(employeeClientMappingRequest, clientMappingExist);
        EmployeeClientMapping employeeClientMapping = employeeClientMappingRepository.save(employeeClientMappingCreate);
        EmployeeClientMapping employeeClientMappingUpdated = null;
        if (Objects.nonNull(employeeClientMapping)) {
            employeeClientMappingUpdated = employeeClientMappingRepository.findById(employeeClientMapping.getId()).orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND, employeeClientMapping.getId() + "Not Found"));
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, employeeClientMapping));
    }

    @Operation(summary = "Update Employee Client Mapping Status Details", description = "Update Employee Client Mapping Status Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeClientMappingResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN')")
    @PutMapping("/client-mapping/contract-status")
    public ResponseEntity<Object> updateEmployeeClientMappingStatus(@Valid @RequestBody CommonStatusRequest commonStatusRequest) {
        EmployeeClientMapping clientMappingExist = employeeClientMappingRepository.findById(commonStatusRequest.getId()).orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND,  commonStatusRequest.getId() + "Not Found"));
        if (Objects.nonNull(clientMappingExist.getIsContractEnded())
                || Objects.equals(clientMappingExist.getIsContractEnded(), true)
                || Objects.equals(clientMappingExist.getIsDeleted(), true) ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.ACCEPTED, "Trying to update invalid client mapping! Please try valid one or contact admin!"));
        }
        clientMappingExist.setIsContractEnded(commonStatusRequest.getStatus());
        EmployeeClientMapping employeeClientMapping = employeeClientMappingRepository.save(clientMappingExist);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, employeeClientMapping));
    }

    @Operation(summary = "Soft Delete Employee Client Mapping Details", description = "Soft Delete Employee Client Mapping Details.")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN')")
    @PutMapping("/client-mapping/{id}")
    public ResponseEntity<Object> softDeteEmployeeClientMapping(@PathVariable("id") String id) {
        EmployeeClientMapping employeeClientMapping = employeeClientMappingRepository.findById(id).orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND,  id + "Not Found"));
        employeeClientMapping.setIsDeleted(true);
        EmployeeClientMapping employeeClientMappingUpdated = employeeClientMappingRepository.save(employeeClientMapping);
        return ResponseEntity.accepted().body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, employeeClientMappingUpdated));
    }

    /**
     * this class created for open api documentation purpose
     */
    private static class EmployeeClientMappingResponse extends CustomSuccessApplicationResponse<EmployeeClientMapping> {
        public EmployeeClientMapping responseData;
    }

    private static class EmployeeClientMappingPageableResponse extends CustomSuccessPagableResponse<PageableResponse<List<EmployeeClientMapping>>> {
        public PageableResponse<List<EmployeeClientMapping>> responseData;
    }

}
