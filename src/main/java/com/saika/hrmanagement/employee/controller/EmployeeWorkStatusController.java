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
 *//*

package com.saika.hrmanagement.employee.controller;

import com.saika.hrmanagement.common.entity.EmployeeWorkStatus;
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.common.payload.CommonStatusRequest;
import com.saika.hrmanagement.common.payload.EmployeeWorkStatusRequest;
import com.saika.hrmanagement.employee.controller.util.EmployeeWorkStatusUtil;
import com.saika.hrmanagement.employee.repository.EmployeeWorkStatusRepository;
import com.saika.hrmanagement.employee.response.CustomApplicationResponse;
import com.saika.hrmanagement.employee.response.CustomSuccessApplicationResponse;
import com.saika.hrmanagement.employee.response.CustomSuccessPagableResponse;
import com.saika.hrmanagement.employee.response.PageableResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

*/
/**
 * @author mani
 *//*

@RestController
@CrossOrigin
@Tag(name = "EmployeeWorkStatus", description = "The Employee Work Status Management API")
@RequestMapping(path = "/work-status", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "Bearer Authentication")
public class EmployeeWorkStatusController extends EmployeeWorkStatusUtil implements CustomApplicationResponse {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private EmployeeWorkStatusRepository employeeWorkStatusRepository;

    */
/**
     * @param
     * @return
     *//*

    @Operation(summary = "Get Employee Work Status Details by id", description = "Get Employee Work Status Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeWorkStatusResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/work-status/{id}")
    public ResponseEntity<Object> fetchEmployeeWorkStatusByClientId(@PathVariable("id") String id){
        EmployeeWorkStatus employeeWorkStatus = employeeWorkStatusRepository.findById(id).orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND,  id + "Not Found"));
        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, employeeWorkStatus));
    }

    @Operation(summary = "Get All Employee Work Status Details", description = "Get All Employee Work Status Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeWorkStatusPageableResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/work-status/all")
    public ResponseEntity<Object> fetchAllEmployeeWorkStatus(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "firstName") String sort,
                                                             @RequestParam(defaultValue = "DESC")String order,
                                                             @RequestParam(required = false)  String search) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sort));
        Page<EmployeeWorkStatus> employeeWorkStatusPageable = employeeWorkStatusRepository.findAll(pageable);
        List<EmployeeWorkStatus>  employeeWorkStatusList = employeeWorkStatusPageable.getContent();

        if (employeeWorkStatusList.isEmpty()) {
            throw new CustomApplicationException(HttpStatus.NO_CONTENT, "No Employee Work Status Data!");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", employeeWorkStatusList);
        response.put("currentPage", employeeWorkStatusPageable.getNumber());
        response.put("totalCount", employeeWorkStatusPageable.getTotalElements());
        response.put("totalPages", employeeWorkStatusPageable.getTotalPages());
        response.put("currentPageSize", employeeWorkStatusPageable.getSize());

        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, response));
    }

    @Operation(summary = "Add Employee Work Status Details", description = "Add Required Employee Work Status Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeWorkStatusResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    @PostMapping("/work-status")
    public ResponseEntity<Object> addEmployeeWorkStatus(@Valid @RequestBody EmployeeWorkStatusRequest employeeWorkStatusRequest)  {
        EmployeeWorkStatus employeeWorkStatusCreate = createUpdateEmployeeWorkStatus(employeeWorkStatusRequest, null);
        EmployeeWorkStatus employeeWorkStatus = employeeWorkStatusRepository.save(employeeWorkStatusCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(constructSuccessApplicationResponse (HttpStatus.CREATED, employeeWorkStatus));
    }

    @Operation(summary = "Update Employee Work Status Details", description = "Update Employee Work Status Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = EmployeeWorkStatusResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/work-status")
    public ResponseEntity<Object> updateEmployeeWorkStatus(@Valid @RequestBody EmployeeWorkStatusRequest EmployeeWorkStatusRequest) {
        EmployeeWorkStatus employeeWorkStatusExit = employeeWorkStatusRepository.findById(EmployeeWorkStatusRequest.getId()).orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND,  EmployeeWorkStatusRequest.getId() + "Not Found"));
        if (Objects.nonNull(employeeWorkStatusExit.getIsDeleted())
            || Objects.equals(employeeWorkStatusExit.getIsDeleted(), true)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, "Trying to update invalid record! Please try valid one or contact admin!"));
        }
        EmployeeWorkStatus employeeWorkStatusCreate = createUpdateEmployeeWorkStatus(EmployeeWorkStatusRequest, employeeWorkStatusExit);
        EmployeeWorkStatus employeeWorkStatus = employeeWorkStatusRepository.save(employeeWorkStatusCreate);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, employeeWorkStatus));
    }

    @Operation(summary = "Soft Delete Employee Work Status Details", description = "Soft Delete Employee Work Status Details.")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/work-status/{id}")
    public ResponseEntity<Object> softDeteEmployeeWorkStatus(@PathVariable("id") String id) {
        EmployeeWorkStatus employeeWorkStatus = employeeWorkStatusRepository.findById(id).orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND,  id + "Not Found"));
        employeeWorkStatus.setIsDeleted(true);
        employeeWorkStatusRepository.save(employeeWorkStatus);
        return ResponseEntity.accepted().body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, " Employee Work Status has successfully deleted!"));
    }

    */
/**
     * this class created for open api documentation purpose
     *//*

    private static class EmployeeWorkStatusResponse extends CustomSuccessApplicationResponse<EmployeeWorkStatus> {
        public EmployeeWorkStatus responseData;
    }

    private static class EmployeeWorkStatusPageableResponse extends CustomSuccessPagableResponse<PageableResponse<List<EmployeeWorkStatus>>> {
        public PageableResponse<List<EmployeeWorkStatus>> responseData;
    }

}
*/
