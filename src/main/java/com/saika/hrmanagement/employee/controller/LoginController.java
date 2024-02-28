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
import com.saika.hrmanagement.common.payload.LoginRequest;
import com.saika.hrmanagement.common.util.JwtTokenUtil;
import com.saika.hrmanagement.employee.controller.util.JwtAuthenticationControllerUtil;
import com.saika.hrmanagement.employee.repository.UserDetailRepository;
import com.saika.hrmanagement.employee.response.CustomApplicationResponse;
import com.saika.hrmanagement.employee.response.CustomSuccessApplicationResponse;
import com.saika.hrmanagement.employee.response.JwtResponse;
import com.saika.hrmanagement.employee.service.impl.JwtUserDetailsServiceImpl;
import com.saika.hrmanagement.employee.service.impl.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Mani
 */
@RestController
@CrossOrigin
@Tag(name = "Auth-User", description = "The User Management API")
public class LoginController extends JwtAuthenticationControllerUtil implements CustomApplicationResponse {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailRepository userDetailRepository;

    /**
     *
     * @param loginRequest
     * @return
     * @throws Exception
     */
    @Operation(summary = "Login", description = " login into timesheet app")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "4XX, 5XX", description = "Any Error, Response format will be same!",
                    content = @Content(schema = @Schema(implementation = CustomApplicationException.class)))
    })
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<Object> createAuthenticationToken(@Valid @RequestBody LoginRequest loginRequest)
    {
        authenticate(loginRequest.getUserName(), loginRequest.getPassword());
        final UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(loginRequest.getUserName());
        if (!userDetails.getIsUserActive()) {
            String message = "User is not active. Please activate the user by clicking on the link which was sent to you in email.";
            throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, message);
        }
        EmployeeDetail user = userDetailRepository.findByUserNameAndIsActive(loginRequest.getUserName(), true);
        user.setLastLoginDateTime(LocalDateTime.now());
        user.setVisits(Objects.nonNull(user.getVisits()) ? user.getVisits() + 1 : 1);
        userDetailRepository.save(user);
        final String token = jwtTokenUtil.generateToken(userDetails);
        JwtResponse jwtResponse = modelUserDetailsToJwtResponse(userDetails, token);
        return ResponseEntity.ok().body(constructSuccessApplicationResponse(HttpStatus.OK, jwtResponse));
    }

    /**
     *
     * @param username
     * @param password
     * @throws Exception
     */
    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            e.printStackTrace();
            log.error(" DisabledException ", e.getMessage());
            throw new CustomApplicationException(HttpStatus.UNAUTHORIZED , "User Disabled", e);
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            log.error(" BadCredentialsException ", e.getMessage());
            throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, "Invalid Credential", e);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(" Exception ", e.getMessage());
            throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, e.getMessage(), e);
        }
    }


    /**
     * this class created for open api documentation purpose
     */
    private static class LoginResponse extends CustomSuccessApplicationResponse<JwtResponse> {
        public JwtResponse responseData;
    }

}
