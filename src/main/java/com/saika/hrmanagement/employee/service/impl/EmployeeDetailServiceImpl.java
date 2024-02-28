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
package com.saika.hrmanagement.employee.service.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.saika.hrmanagement.common.entity.EmployeeDetail;
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.common.payload.EmployeeReEnrollOrTerminateRequest;
import com.saika.hrmanagement.employee.repository.EmployeeDetailRepository;
import com.saika.hrmanagement.employee.service.EmployeeDetailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author mani
 */
@Service
public class EmployeeDetailServiceImpl implements EmployeeDetailService {

    @Autowired
    private EmployeeDetailRepository employeeDetailRepository;

    @Override
    public EmployeeDetail createEmployeeDetail(final EmployeeDetail employeeDetail) {
        return employeeDetailRepository.save(employeeDetail);
    }

    @Override
    @JsonIgnoreProperties({"password","isActiveToken", "enrollToken"})
    public Page<EmployeeDetail> getAllEmployeeDetails(final String search, final Pageable pageable) {
        if (Objects.nonNull(search) && !Objects.equals(search.trim(), "")) {
            return employeeDetailRepository.getAllEmployeeDetailsBySearch(search, pageable);
        }
        return employeeDetailRepository.findAll(pageable);
    }

    @Override
    @JsonIgnoreProperties({"password","isActiveToken", "enrollToken"})
    public EmployeeDetail getEmployeeDetailById(final String id)  {
        return employeeDetailRepository.findById(id).orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND,  id + "Not Found"));
    }

    @Override
    public EmployeeDetail updateEmployeeDetail(final EmployeeDetail updateEmployeeDetail) {

        return employeeDetailRepository.save(updateEmployeeDetail);
    }

    public EmployeeDetail deleteEmployeeDetail(EmployeeDetail employeeDetailExist) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        employeeDetailExist.setIsActive(Boolean.FALSE);
        employeeDetailExist.setUpdatedBy(user.getId());
        employeeDetailExist.setUpdatedOn(LocalDateTime.now());
        employeeDetailExist.setIsDelete(Boolean.TRUE);
        employeeDetailExist.setIsEnrolled(null);
        return employeeDetailRepository.save(employeeDetailExist);
    }

    public EmployeeDetail reEnrollOrTerminateEmployee(EmployeeDetail employeeDetailExist, EmployeeReEnrollOrTerminateRequest employeeReEnrollOrTerminateRequest) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        employeeDetailExist.setIsActive(Boolean.FALSE);
        employeeDetailExist.setUpdatedBy(user.getId());
        employeeDetailExist.setUpdatedOn(LocalDateTime.now());
        employeeDetailExist.setId(employeeReEnrollOrTerminateRequest.getEmployeeId());
        employeeDetailExist.setIsEnrolled(null);
        if (Objects.nonNull(employeeReEnrollOrTerminateRequest.getDateOfEmployment())) {
            employeeDetailExist.setDateOfJoin(employeeReEnrollOrTerminateRequest.getDateOfEmployment());
        }
        if (Objects.nonNull(employeeReEnrollOrTerminateRequest.getComments()) && StringUtils.isNotEmpty(employeeReEnrollOrTerminateRequest.getComments())) {
            employeeDetailExist.setComments(employeeReEnrollOrTerminateRequest.getComments());
        }
        if (Objects.nonNull(employeeReEnrollOrTerminateRequest.getLastDateOfEmployment())) {
            employeeDetailExist.setIsDelete(Boolean.TRUE);
            employeeDetailExist.setLastDateOfEmployment(employeeReEnrollOrTerminateRequest.getLastDateOfEmployment());
        }
        if (Objects.nonNull(employeeReEnrollOrTerminateRequest.getTerminationReason()) && StringUtils.isNotEmpty(employeeReEnrollOrTerminateRequest.getTerminationReason())) {
            employeeDetailExist.setTerminationReason(employeeReEnrollOrTerminateRequest.getTerminationReason());
        }
        return employeeDetailRepository.save(employeeDetailExist);
    }
}
