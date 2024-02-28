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
package com.saika.hrmanagement.employee.controller.util;

import com.saika.hrmanagement.common.entity.EmployeeClientMapping;
import com.saika.hrmanagement.common.payload.EmployeeClientMappingRequest;
import com.saika.hrmanagement.employee.service.impl.UserDetailsImpl;
import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author mani
 */
public class EmployeeClientMappingUtil {

    public static EmployeeClientMapping createUpdateEmployeeClientMappingDetail(final EmployeeClientMappingRequest employeeClientMappingRequest, final EmployeeClientMapping employeeClientMappingExist) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmployeeClientMapping employeeClientMapping = new EmployeeClientMapping();
        var clientObjectId = new ObjectId(employeeClientMappingRequest.getClientPkId());
        var employeeObjectId = new ObjectId(employeeClientMappingRequest.getEmployeePkId());
        if (Objects.nonNull(employeeClientMappingRequest.getEndClientPkId()) && !employeeClientMappingRequest.getEndClientPkId().isEmpty()) {
            var endClientObjectId = new ObjectId(employeeClientMappingRequest.getEndClientPkId());
            employeeClientMapping.setEndClientDetailId(endClientObjectId);
        }
        employeeClientMapping.setClientDetailId(clientObjectId);
        employeeClientMapping.setEmployeeDetailId(employeeObjectId);
        employeeClientMapping.setDesignation(employeeClientMappingRequest.getDesignation());
        employeeClientMapping.setStartDate(employeeClientMappingRequest.getStartDate());
        employeeClientMapping.setEndDate(employeeClientMappingRequest.getEndDate());
        employeeClientMapping.setProjectAllocation(employeeClientMappingRequest.getProjectAllocation());
        if (Objects.isNull(employeeClientMappingRequest.getId()) || Objects.equals(employeeClientMappingRequest.getId(), "0") || Objects.equals(employeeClientMappingRequest.getId(), "")) {
            employeeClientMapping.setCreatedOn(LocalDateTime.now());
            employeeClientMapping.setCreatedBy(user.getId());
        } else {
            if (!Objects.isNull(employeeClientMappingExist)) {
                employeeClientMapping.setCreatedOn(employeeClientMappingExist.getCreatedOn());
                employeeClientMapping.setCreatedBy(employeeClientMappingExist.getCreatedBy());
            }
            employeeClientMapping.setUpdatedOn(LocalDateTime.now());
            employeeClientMapping.setUpdatedBy(user.getId());
            employeeClientMapping.setId(employeeClientMappingExist.getId());
        }

        return employeeClientMapping;
    }
}

