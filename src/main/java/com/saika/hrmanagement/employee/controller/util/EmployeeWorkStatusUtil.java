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

import com.saika.hrmanagement.common.entity.EmployeeWorkStatus;
import com.saika.hrmanagement.common.payload.EmployeeWorkStatusRequest;
import com.saika.hrmanagement.employee.service.impl.UserDetailsImpl;
import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author mani
 */
public class EmployeeWorkStatusUtil {

    public static EmployeeWorkStatus createUpdateEmployeeWorkStatus(final EmployeeWorkStatusRequest employeeWorkStatusRequest, final EmployeeWorkStatus employeeWorkStatusExist) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmployeeWorkStatus employeeWorkStatus = new EmployeeWorkStatus();
        var employeeObjectId = new ObjectId(employeeWorkStatusRequest.getEmployeeId());
        employeeWorkStatus.setEmployeeDetailId(employeeObjectId);
        employeeWorkStatus.setVisaStatus(employeeWorkStatusRequest.getVisaStatus());
        employeeWorkStatus.setVisaType(employeeWorkStatusRequest.getVisaType());
        if (Objects.isNull(employeeWorkStatusRequest.getId()) || Objects.equals(employeeWorkStatusRequest.getId(), "0")) {
            employeeWorkStatus.setCreatedOn(LocalDateTime.now());
            employeeWorkStatus.setCreatedBy(user.getId());
        } else {
            if (!Objects.isNull(employeeWorkStatusExist)) {
                employeeWorkStatus.setCreatedOn(employeeWorkStatusExist.getCreatedOn());
                employeeWorkStatus.setCreatedBy(employeeWorkStatusExist.getCreatedBy());
            }
            employeeWorkStatus.setUpdatedOn(LocalDateTime.now());
            employeeWorkStatus.setUpdateBy(user.getId());
            employeeWorkStatus.setId(employeeWorkStatusExist.getId());
        }

        return employeeWorkStatus;
    }
}

