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

import com.saika.hrmanagement.common.entity.AppMetadata;
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.common.payload.AppMetadataRequest;
import com.saika.hrmanagement.employee.controller.util.AppMetadataUtil;
import com.saika.hrmanagement.employee.repository.AppMetadataRepository;
import com.saika.hrmanagement.employee.response.CustomApplicationResponse;
import com.saika.hrmanagement.employee.response.CustomSuccessApplicationResponse;
import com.saika.hrmanagement.employee.response.CustomSuccessPagableResponse;
import com.saika.hrmanagement.employee.response.PageableResponse;
import com.saika.hrmanagement.employee.service.impl.AppMetadataServiceImpl;
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

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author mani
 */
@RestController
@CrossOrigin
@Tag(name = "Metadata", description = "The Metadata Management API")
@RequestMapping(path = "/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "Bearer Authentication")
public class AppMetadataController extends AppMetadataUtil implements CustomApplicationResponse {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private AppMetadataServiceImpl appMetadataService;

    @Autowired
    private AppMetadataRepository appMetadataRepository;

    /**
     * @param
     * @return
     */
    @Operation(summary = "Get Metadata Details by id ", description = "Get Metadata Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = MetadataResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN', 'MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> fetchMetadataById(@PathVariable("id") String id){

        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, appMetadataService.getAppMetadataById(id)));
    }

    @Operation(summary = "Get All Metadata Details", description = "Get All Metadata Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = MetadataPageableResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN','TIMESHEET_ADMIN', 'MANAGER')")
    @GetMapping("/all")
    public ResponseEntity<Object> fetchAllAppMetadata(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(defaultValue = "code",required = false) String sort,
                                                      @RequestParam(defaultValue = "ASC",required = false) String order,
                                                      @RequestParam(required = false)  String search) {

        /*Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sort));
        Page<AppMetadata> appMetadataPage = null;
        if (Objects.nonNull(search) && !Objects.equals(search.trim(), "")) {
            appMetadataPage = appMetadataRepository.findAllAppMetadataBySearch(search, pageable);
        } else {
            appMetadataPage = appMetadataRepository.findAll(pageable);
        }*/
        List<AppMetadata>  appMetadataList = appMetadataRepository.findAll();

        if (appMetadataList.isEmpty()) {
            throw new CustomApplicationException(HttpStatus.NO_CONTENT, "No Client Data!");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", appMetadataList);
        /*response.put("currentPage", appMetadataPage.getNumber());
        response.put("totalCount", appMetadataPage.getTotalElements());
        response.put("totalPages", appMetadataPage.getTotalPages());
        response.put("currentPageSize", appMetadataPage.getSize());*/

        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, response));
    }

    @Operation(summary = "Add Metadata Details", description = "Add Required Metadata Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = MetadataResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN', 'MANAGER')")
    @PostMapping
    public ResponseEntity<Object> addAppMetadata(@Valid @RequestBody AppMetadataRequest appMetadataRequest)  {
        AppMetadata appMetadata = createUpdateAppMetadata(appMetadataRequest, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(constructSuccessApplicationResponse(HttpStatus.CREATED, appMetadataService.createAppMetadata(appMetadata)));
    }

    @Operation(summary = "Update Client Details", description = "Update Client Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = MetadataResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN','TIMESHEET_ADMIN', 'MANAGER')")
    @PutMapping
    public ResponseEntity<Object> updateClient(@Valid @RequestBody AppMetadataRequest appMetadataRequest) {
        AppMetadata appMetadataExist = appMetadataService.getAppMetadataById(appMetadataRequest.getId());
        AppMetadata appMetadata = createUpdateAppMetadata(appMetadataRequest, appMetadataExist);
        return ResponseEntity.accepted().body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, appMetadataService.updateAppMetadata(appMetadata)));
    }

    @Operation(summary = "Soft Delete Metadata Details", description = "Soft Delete Metatdata Details.")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TIMESHEET_ADMIN', 'MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> softDeleteClient(@PathVariable("id") String id) {
        AppMetadata appMetadataExist = appMetadataService.getAppMetadataById(id);
        appMetadataService.deleteAppMetadata(appMetadataExist);
        return ResponseEntity.accepted().body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, "App Metadata has successfully deleted!"));
    }

    /**
     * this class created for open api documentation purpose
     */
    private static class MetadataResponse extends CustomSuccessApplicationResponse<AppMetadata> {
        public AppMetadata responseData;
    }

    private static class MetadataPageableResponse extends CustomSuccessPagableResponse<PageableResponse<List<AppMetadata>>> {
        public PageableResponse<List<AppMetadata>> responseData;
    }

}
