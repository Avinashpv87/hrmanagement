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

import com.saika.hrmanagement.common.entity.EmployeeTimesheet;
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.employee.repository.EmployeeTimesheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author mani
 */
@Service
public class EmployeeTimesheetServiceImpl implements EmployeeTimesheetService {

    @Autowired
    private EmployeeTimesheetRepository employeeTimesheetRepository;

    @Override
    public EmployeeTimesheet createEmployeeTimesheet(final EmployeeTimesheet EmployeeTimesheet) {
        return employeeTimesheetRepository.save(EmployeeTimesheet);
    }

    @Override
    public Page<EmployeeTimesheet> getAllEmployeeTimesheets(final Pageable pageable) {
        return employeeTimesheetRepository.findAll(pageable);
    }

    @Override
    public Page<EmployeeTimesheet> getAllEmployeeTimesheetByEmployeeDetail(final Pageable pageable) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return employeeTimesheetRepository.findAllByEmployeeDetailId(user.getId(), pageable);
    }

    @Override
    public EmployeeTimesheet getEmployeeTimesheetById(final String id)  {
        return employeeTimesheetRepository.findById(id).orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND,  id + "Not Found"));
    }

    @Override
    public EmployeeTimesheet updateEmployeeTimesheet(final EmployeeTimesheet updateEmployeeTimesheet) {

        return employeeTimesheetRepository.save(updateEmployeeTimesheet);
    }

    public EmployeeTimesheet deleteEmployeeTimesheet(EmployeeTimesheet employeeTimesheetExist) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        employeeTimesheetExist.setUpdatedBy(user.getId());
        employeeTimesheetExist.setUpdatedOn(LocalDateTime.now());
        employeeTimesheetExist.setIsTimesheetDeleted(Boolean.TRUE);
        return employeeTimesheetRepository.save(employeeTimesheetExist);
    }

}
