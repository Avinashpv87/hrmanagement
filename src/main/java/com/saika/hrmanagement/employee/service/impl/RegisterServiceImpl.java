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

import com.saika.hrmanagement.common.entity.EmployeeDetail;
import com.saika.hrmanagement.common.payload.SignupRequest;
import com.saika.hrmanagement.employee.repository.RoleRepository;
import com.saika.hrmanagement.employee.repository.UserDetailRepository;
import com.saika.hrmanagement.employee.response.EmployeeDetailResponse;
import com.saika.hrmanagement.employee.service.RegisterService;
import com.saika.hrmanagement.employee.service.impl.util.EmployeeDetailRequestResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author Mani
 */
@Service
@Transactional
public class RegisterServiceImpl extends EmployeeDetailRequestResponseUtil implements RegisterService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Override
    public EmployeeDetailResponse registerUser(SignupRequest userRequest, String activeUserToken) {
        EmployeeDetail userDetailInformation = constructEmployeeDetail(userRequest, activeUserToken, roleRepository);
        EmployeeDetail response = userDetailRepository.save(userDetailInformation);
        return response != null ? modelEntityToResponse(response) : null;
    }

}
