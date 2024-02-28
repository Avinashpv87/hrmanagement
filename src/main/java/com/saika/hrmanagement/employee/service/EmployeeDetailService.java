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
package com.saika.hrmanagement.employee.service;

import com.saika.hrmanagement.common.entity.EmployeeDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author mani
 */
public interface EmployeeDetailService {

    EmployeeDetail createEmployeeDetail(final EmployeeDetail EmployeeDetail);

    Page<EmployeeDetail> getAllEmployeeDetails(final String search, final Pageable pageable);

    EmployeeDetail getEmployeeDetailById(final String id);

    EmployeeDetail updateEmployeeDetail(final EmployeeDetail EmployeeDetail);

    EmployeeDetail deleteEmployeeDetail(final EmployeeDetail EmployeeDetail);

}
