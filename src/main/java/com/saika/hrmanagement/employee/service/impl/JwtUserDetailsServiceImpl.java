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
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.employee.repository.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author mani
 */
@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UserDetailRepository userDetailRepository;
	
	@Autowired
	private PasswordEncoder bcryptEncoder;

	/**
	 *
	 * @param username the username identifying the user whose data is required.
	 * @return
	 * @throws UsernameNotFoundException
	 */
	public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
		EmployeeDetail user = userDetailRepository.findByUserNameAndIsActive(username, true);
		if (user == null) {
			throw new CustomApplicationException(HttpStatus.NO_CONTENT, "User Not Found : "  + username );
		}
		return UserDetailsImpl.build(user);
	}

}
