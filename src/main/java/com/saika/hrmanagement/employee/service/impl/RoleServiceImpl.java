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

import com.saika.hrmanagement.common.constant.ERole;
import com.saika.hrmanagement.common.entity.Role;
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.employee.repository.RoleRepository;
import com.saika.hrmanagement.employee.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Mani
 *
 */
@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleRepository roleRepo;

	@Override
	@Transactional
	public String addRoles(List<String> roles) {
			List<String> errorList = new ArrayList<>();
			roles.forEach(role -> {
				Role roleObject = new Role();
				switch (role) {
					case "admin":
						Optional<Role> adminRolePresent = roleRepo.findByName(ERole.ROLE_ADMIN);
						if (adminRolePresent.isPresent()) {
							errorList.add(role + " : role is already present.");
							break;
						}
						roleObject.setName(ERole.ROLE_ADMIN);
						roleObject.setCreatedOn(LocalDateTime.now());
						roleRepo.save(roleObject);
						break;
					case "superadmin":
						Optional<Role> superadminRolePresent = roleRepo.findByName(ERole.ROLE_SUPER_ADMIN);
						if (superadminRolePresent.isPresent()) {
							errorList.add(role + " :  role is already present.");
							break;
						}
						roleObject.setName(ERole.ROLE_SUPER_ADMIN);
						roleObject.setCreatedOn(LocalDateTime.now());
						roleRepo.save(roleObject);
						break;
					case "manager":
						Optional<Role> managerRolePresent = roleRepo.findByName(ERole.ROLE_MANAGER);
						if (managerRolePresent.isPresent()) {
							errorList.add(role + " :  role is already present.");
							break;
						}
						roleObject.setName(ERole.ROLE_MANAGER);
						roleObject.setCreatedOn(LocalDateTime.now());
						roleRepo.save(roleObject);
						break;
					case "timesheetadmin":
						Optional<Role> timesheetAdminRolePresent = roleRepo.findByName(ERole.ROLE_TIMESHEET_ADMIN);
						if (timesheetAdminRolePresent.isPresent()) {
							errorList.add(role + " :  role is already present.");
							break;
						}
						roleObject.setName(ERole.ROLE_TIMESHEET_ADMIN);
						roleObject.setCreatedOn(LocalDateTime.now());
						roleRepo.save(roleObject);
						break;
					case "user":
						Optional<Role> userRolePresent = roleRepo.findByName(ERole.ROLE_USER);
						if (userRolePresent.isPresent()) {
							errorList.add(role + " : role is already present.");
							break;
						}
						roleObject.setName(ERole.ROLE_USER);
						roleObject.setCreatedOn(LocalDateTime.now());
						roleRepo.save(roleObject);
						break;
					default:
						throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "You trying to add some role which is not in scope! "+role);
				}
			});

		 if (!CollectionUtils.isEmpty(errorList)) {
			 throw new CustomApplicationException(HttpStatus.IM_USED, errorList.stream().collect(Collectors.joining(", ", "{", "}")));
		 }
		 return  "Role Successfully Added";
	}

}
