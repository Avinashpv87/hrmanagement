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
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.common.payload.ClientRequest;
import com.saika.hrmanagement.employee.controller.util.ClientDetailUtil;
import com.saika.hrmanagement.employee.repository.ClientDetailRepository;
import com.saika.hrmanagement.employee.response.CustomApplicationResponse;
import com.saika.hrmanagement.employee.response.CustomSuccessApplicationResponse;
import com.saika.hrmanagement.employee.response.CustomSuccessPagableResponse;
import com.saika.hrmanagement.employee.response.PageableResponse;
import com.saika.hrmanagement.employee.service.impl.ClientServiceImpl;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * @author mani
 */
@RestController
@CrossOrigin
@Tag(name = "Client", description = "The Client Management API")
@RequestMapping(path = "/hrm", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "Bearer Authentication")
public class ClientController extends ClientDetailUtil implements CustomApplicationResponse {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ClientServiceImpl clientService;

    @Autowired
    private ClientDetailRepository clientDetailRepository;

    /**
     * @param
     * @return
     */
    @Operation(summary = "Get Client Details by clientId", description = "Get Client Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/client/{id}")
    public ResponseEntity<Object> fetchClientByClientId(@PathVariable("id") String clientId){

        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, clientService.getClientDetailById(clientId)));
    }

    @Operation(summary = "Get All Client Details", description = "Get All Client Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = ClientPageableResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/clients")
    public ResponseEntity<Object> fetchAllClient(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(defaultValue = "name",required = false) String sort,
                                                 @RequestParam(defaultValue = "ASC", required = false) String order,
                                                 @RequestParam(required = false)  String search) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sort));

        Page<ClientDetail> clientDetailPage = null;
        if (Objects.nonNull(search) && !Objects.equals(search.trim(), "")) {
            clientDetailPage = clientDetailRepository.findAllClientDetailBySearch(search, pageable);
        } else {
            clientDetailPage = clientDetailRepository.findAll(pageable);
        }

        List<ClientDetail>  clientDetailList = clientDetailPage.getContent();

        if (clientDetailList.isEmpty()) {
            throw new CustomApplicationException(HttpStatus.NO_CONTENT, "No Client Data!");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", clientDetailList);
        response.put("currentPage", clientDetailPage.getNumber());
        response.put("totalCount", clientDetailPage.getTotalElements());
        response.put("totalPages", clientDetailPage.getTotalPages());
        response.put("currentPageSize", clientDetailPage.getSize());

        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, response));
    }

    @Operation(summary = "Add Client Details", description = "Add Required Client Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/client")
    public ResponseEntity<Object> addClient(@Valid @RequestBody ClientRequest clientRequest)  {
        List<ClientDetail> clientDetailExist = clientDetailRepository.getClientDetailByNameOrCodeOrEmail(clientRequest.getName(), clientRequest.getCode(), clientRequest.getEmail());
        if (Objects.nonNull(clientDetailExist) && !CollectionUtils.isEmpty(clientDetailExist)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Already Client Name or Code or Email Exist! Please Try with different values!"));
        }
        ClientDetail clientDetail = createUpdateClientDetail(clientRequest, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(constructSuccessApplicationResponse(HttpStatus.CREATED, clientService.createClientDetail(clientDetail)));
    }

    @Operation(summary = "Update Client Details", description = "Update Client Details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/client")
    public ResponseEntity<Object> updateClient(@Valid @RequestBody ClientRequest updateClientRequest) {
        ClientDetail clientDetailExist = clientService.getClientDetailById(updateClientRequest.getId());
        if (Objects.isNull(clientDetailExist)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constructFailureApplicationResponse(HttpStatus.BAD_REQUEST, "Client Not Exist! Please Try valid one!"));
        }
        ClientDetail clientDetail = createUpdateClientDetail(updateClientRequest, clientDetailExist);
        return ResponseEntity.accepted().body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, clientService.updateClientDetail(clientDetail)));
    }

    @Operation(summary = "Soft Delete Client Details", description = "Soft Delete Client Details.")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/client/{id}")
    public ResponseEntity<Object> softDeteClient(@PathVariable("id") String clientId) {
        ClientDetail clientDetailExist = clientService.getClientDetailById(clientId);
        clientService.deleteClientDetail(clientDetailExist);
        return ResponseEntity.accepted().body(constructSuccessApplicationResponse(HttpStatus.ACCEPTED, "Client has successfully deleted!"));
    }

    /**
     * this class created for open api documentation purpose
     */
    private static class ClientResponse extends CustomSuccessApplicationResponse<ClientDetail> {
        public ClientDetail responseData;
    }

    private static class ClientPageableResponse extends CustomSuccessPagableResponse<PageableResponse<List<ClientDetail>>> {
        public PageableResponse<List<ClientDetail>> responseData;
    }

}
