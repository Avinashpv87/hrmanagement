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
package com.saika.hrmanagement.employee.service.impl.util;

import com.saika.hrmanagement.common.constant.ERole;
import com.saika.hrmanagement.common.entity.EmployeeDetail;
import com.saika.hrmanagement.common.entity.Role;
import com.saika.hrmanagement.common.payload.EmployeeDetailRequest;
import com.saika.hrmanagement.common.payload.EmployeeProfileRequest;
import com.saika.hrmanagement.common.payload.SignupRequest;
import com.saika.hrmanagement.employee.repository.RoleRepository;
import com.saika.hrmanagement.employee.response.EmployeeDetailResponse;
import com.saika.hrmanagement.employee.service.impl.UserDetailsImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author mani
 */
public class EmployeeDetailRequestResponseUtil {

	protected EmployeeDetail constructEmployeeDetail(SignupRequest userRequest, String activeUserToken, RoleRepository roleRepository) {
		EmployeeDetail userDetailInformation = modelRequestToEntity(userRequest);
		Set<Role> roles = new HashSet<>();
		fetchAndAssignRole(userRequest, roleRepository, roles);
		userDetailInformation.setIsActive(Boolean.FALSE);
		userDetailInformation.setRoles(roles);
		userDetailInformation.setIsActiveToken(activeUserToken);
		return userDetailInformation;
	}

	private static void fetchAndAssignRole(SignupRequest userRequest, RoleRepository roleRepository, Set<Role> roles) {
		if (userRequest.getRoles() != null) {
			userRequest.getRoles().forEach(role -> {
				switch (role) {
					case "admin":
						Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(adminRole);
						break;
					case "superadmin":
						Role moderatorRole = roleRepository.findByName(ERole.ROLE_SUPER_ADMIN)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(moderatorRole);
						break;
					case "user":
						Role userRole = roleRepository.findByName(ERole.ROLE_USER)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(userRole);
						break;
					default:
						Role defaultRole = roleRepository.findByName(ERole.ROLE_USER)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(defaultRole);
						break;
				}
			});
		} else {
			Optional<Role> defaultRole = roleRepository.findByName(ERole.ROLE_USER);
			if (!defaultRole.isPresent()){
				Role _role = new Role();
				_role.setName(ERole.ROLE_USER);
				_role.setCreatedOn(LocalDateTime.now());
				roles.add(_role);
				roleRepository.save(_role);
			} else {
				roles.add(defaultRole.get());
			}
		}
	}

	protected EmployeeDetailResponse modelEntityToResponse(EmployeeDetail entityResponse) {
		EmployeeDetailResponse employeeDetailResponse = new EmployeeDetailResponse();
		//no need all this time
		employeeDetailResponse.setUserName(entityResponse.getUserName());
		return employeeDetailResponse;
	}

	// this method is duplicate .. todo in future
	protected EmployeeDetail modelRequestToEntity(SignupRequest signupRequest) {
		EmployeeDetail employeeDetail = new EmployeeDetail();
		employeeDetail.setCreatedOn(LocalDateTime.now());
		employeeDetail.setEmail(signupRequest.getEmail());
		employeeDetail.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
		employeeDetail.setUserName(signupRequest.getUserName());
		employeeDetail.setFirstName(signupRequest.getFirstName());
		employeeDetail.setLastName(signupRequest.getLastName());
		employeeDetail.setPrimaryPhoneNumber(signupRequest.getPrimaryPhoneNumber());
		employeeDetail.setSecondaryPhoneNumber(signupRequest.getSecondaryPhoneNumber());
		employeeDetail.setAddressLine1(signupRequest.getAddressLine1());
		employeeDetail.setAddressLine2(signupRequest.getAddressLine2());
		employeeDetail.setCity(signupRequest.getCity());
		employeeDetail.setState(signupRequest.getState());
		employeeDetail.setCountry(signupRequest.getCountry().isEmpty() ? "USA": signupRequest.getCountry());
		employeeDetail.setPostalCode(signupRequest.getPostalCode());
		employeeDetail.setCreatedOn(LocalDateTime.now());
		return employeeDetail;
	}

	//same method used for enroll and sign up
	public static EmployeeDetail createUpdateEmployeeDetail(final EmployeeDetailRequest employeeDetailRequest, final EmployeeDetail employeeDetailExists, final RoleRepository roleRepository, final String activeUserToken, final String enrollToken) {
		UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		EmployeeDetail employeeDetail = new EmployeeDetail();
		employeeDetail.setEmail(employeeDetailRequest.getEmail());
		employeeDetail.setFirstName(employeeDetailRequest.getFirstName());
		employeeDetail.setLastName(employeeDetailRequest.getLastName());
		employeeDetail.setEmployeeIdentityNumber(employeeDetailRequest.getEmployeeIdentityNumber());
		employeeDetail.setPrimaryPhoneNumber(employeeDetailRequest.getPrimaryPhoneNumber());
		employeeDetail.setSecondaryPhoneNumber(employeeDetailRequest.getSecondaryPhoneNumber());
		employeeDetail.setAddressLine1(employeeDetailRequest.getAddressLine1());
		employeeDetail.setAddressLine2(employeeDetailRequest.getAddressLine2());
		employeeDetail.setCity(employeeDetailRequest.getCity());
		employeeDetail.setState(employeeDetailRequest.getState());
		employeeDetail.setCountry(Objects.nonNull(employeeDetailRequest.getCountry()) ?  employeeDetailRequest.getCountry() : "USA");
		employeeDetail.setPostalCode(employeeDetailRequest.getPostalCode());
		employeeDetail.setDesignation(employeeDetailRequest.getDesignation());
		employeeDetail.setDateOfJoin(employeeDetailRequest.getDateOfJoin());

		employeeDetail.setProjectAllocation(employeeDetailRequest.getProjectAllocation());

		employeeDetail.setWorkAuthType(employeeDetailRequest.getWorkAuthType());
		employeeDetail.setWorkStatus(employeeDetailRequest.getWorkStatus());
		employeeDetail.setEmployementType(employeeDetailRequest.getEmployementType());

		employeeDetail.setWageType(employeeDetailRequest.getWageType());
		if (Objects.nonNull(employeeDetailRequest.getSkills()) && !employeeDetailRequest.getSkills().isEmpty()) {
			employeeDetail.setSkills(employeeDetailRequest.getSkills());
		}
		Set<Role> roles = new HashSet<>();
		//kind of todo
		SignupRequest signupRequest = new SignupRequest();

		if (Objects.isNull(employeeDetailRequest.getId()) || StringUtils.isEmpty(employeeDetailRequest.getId()) || Objects.equals(employeeDetailRequest.getId(), "0")) {
			employeeDetail.setCreatedOn(LocalDateTime.now());
			employeeDetail.setCreatedBy(user.getId());
			employeeDetail.setIsActive(Boolean.FALSE);
			signupRequest.setRoles(employeeDetailRequest.getRoles());
			fetchAndAssignRole(signupRequest , roleRepository, roles);
			employeeDetail.setRoles(roles);
		} else {
			if (!Objects.isNull(employeeDetailExists)) {
				employeeDetail.setUserName(employeeDetailExists.getUserName());
				employeeDetail.setCreatedOn(employeeDetailExists.getCreatedOn());
				employeeDetail.setCreatedBy(employeeDetailExists.getCreatedBy());
				employeeDetail.setId(employeeDetailExists.getId());
				employeeDetail.setUpdatedOn(LocalDateTime.now());
				employeeDetail.setUpdatedBy(user.getId());
				employeeDetail.setPassword(employeeDetailExists.getPassword());
				employeeDetail.setEnrollToken(employeeDetailExists.getEnrollToken());
				employeeDetail.setIsActive(employeeDetailExists.getIsActive());
				employeeDetail.setRoles(employeeDetailExists.getRoles());
			}
		}
		if (Objects.nonNull(employeeDetailRequest.getComments())) {
			employeeDetail.setComments(employeeDetailRequest.getComments());
		}
		employeeDetail.setEnrollToken(enrollToken);

		return employeeDetail;
	}

	public static EmployeeDetail createUpdateEmployeeProfile(final EmployeeProfileRequest employeeProfileRequest, final EmployeeDetail employeeDetailExists) {
		UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		employeeDetailExists.setFirstName(employeeProfileRequest.getFirstName());
		employeeDetailExists.setLastName(employeeProfileRequest.getLastName());
		employeeDetailExists.setPrimaryPhoneNumber(employeeProfileRequest.getPrimaryPhoneNumber());
		employeeDetailExists.setSecondaryPhoneNumber(employeeProfileRequest.getSecondaryPhoneNumber());
		employeeDetailExists.setAddressLine1(employeeProfileRequest.getAddressLine1());
		employeeDetailExists.setAddressLine2(employeeProfileRequest.getAddressLine2());
		employeeDetailExists.setCity(employeeProfileRequest.getCity());
		employeeDetailExists.setState(employeeProfileRequest.getState());
		employeeDetailExists.setCountry(employeeProfileRequest.getCountry().isEmpty() ? "USA": employeeProfileRequest.getCountry());
		employeeDetailExists.setPostalCode(employeeProfileRequest.getPostalCode());
		employeeDetailExists.setUpdatedOn(LocalDateTime.now());
		employeeDetailExists.setUpdatedBy(user.getId());

		if(Objects.nonNull(employeeProfileRequest.getSkills()) && !employeeProfileRequest.getSkills().isEmpty()) {
			employeeDetailExists.setSkills(employeeProfileRequest.getSkills());
		}

		return employeeDetailExists;
	}

	public EmployeeDetailRequest constructEmployeeFromExistingRecord(final EmployeeDetail employeeDetailFromOldRecord) {
		EmployeeDetailRequest employeeDetailRequest = new EmployeeDetailRequest();

		employeeDetailRequest.setEmail(employeeDetailFromOldRecord.getEmail());
		employeeDetailRequest.setFirstName(employeeDetailFromOldRecord.getFirstName());
		employeeDetailRequest.setLastName(employeeDetailFromOldRecord.getLastName());

		employeeDetailRequest.setPrimaryPhoneNumber(employeeDetailFromOldRecord.getPrimaryPhoneNumber());
		employeeDetailRequest.setSecondaryPhoneNumber(employeeDetailFromOldRecord.getSecondaryPhoneNumber());
		employeeDetailRequest.setAddressLine1(employeeDetailFromOldRecord.getAddressLine1());
		employeeDetailRequest.setAddressLine2(employeeDetailFromOldRecord.getAddressLine2());
		employeeDetailRequest.setCity(employeeDetailFromOldRecord.getCity());
		employeeDetailRequest.setState(employeeDetailFromOldRecord.getState());
		employeeDetailRequest.setCountry(Objects.nonNull(employeeDetailFromOldRecord.getCountry()) ?  employeeDetailFromOldRecord.getCountry() : "USA");
		employeeDetailRequest.setPostalCode(employeeDetailFromOldRecord.getPostalCode());
		employeeDetailRequest.setDesignation(employeeDetailFromOldRecord.getDesignation());

		employeeDetailRequest.setProjectAllocation(employeeDetailFromOldRecord.getProjectAllocation());

		employeeDetailRequest.setWorkAuthType(employeeDetailFromOldRecord.getWorkAuthType());
		employeeDetailRequest.setWorkStatus(employeeDetailFromOldRecord.getWorkStatus());
		employeeDetailRequest.setEmployementType(employeeDetailFromOldRecord.getEmployementType());

		employeeDetailRequest.setWageType(employeeDetailFromOldRecord.getWageType());

		if(Objects.nonNull(employeeDetailFromOldRecord.getSkills()) && !employeeDetailFromOldRecord.getSkills().isEmpty()) {
			employeeDetailRequest.setSkills(employeeDetailFromOldRecord.getSkills());
		}


		//employeeDetailRequest.setEmployeeIdentityNumber(employeeDetailFromOldRecord.getEmployeeIdentityNumber());
		//employeeDetailRequest.setDateOfJoin(employeeDetailFromOldRecord.getDateOfJoin());


		return employeeDetailRequest;
	}

}
