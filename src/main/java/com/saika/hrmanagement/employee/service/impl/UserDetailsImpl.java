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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saika.hrmanagement.common.entity.EmployeeDetail;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mani
 */
@Data
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private String id;

    private String employeeIdentityNumber;

    private String username;
    @JsonIgnore
    private String password;

    private String firstName;

    private String lastName;

    private String email;

    private LocalDateTime createdOn;

    private LocalDateTime LastModified;

    private LocalDateTime lastLoginDate;

    private String phoneNumber;

    private Boolean isUserActive;

    private Collection<? extends GrantedAuthority> authorities;

    // this should be update based on business need
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isUserActive;
    }

    public UserDetailsImpl(String id, String employeeIdentityNumber,String username, String password, String firstName,
                           String lastName, String email, LocalDateTime createdOn, LocalDateTime lastModified, LocalDateTime lastLoginDate, String phoneNumber,
                           Boolean isUserActive,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.employeeIdentityNumber = employeeIdentityNumber;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdOn = createdOn;
        this.LastModified = lastModified;
        this.lastLoginDate = lastLoginDate;
        this.phoneNumber = phoneNumber;
        this.isUserActive = isUserActive;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(EmployeeDetail user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

        return new UserDetailsImpl(user.getId(), user.getEmployeeIdentityNumber(), user.getUserName(), user.getPassword(),
                user.getFirstName(), user.getLastName(), user.getEmail(), user.getCreatedOn(), user.getUpdatedOn(),
                user.getLastLoginDateTime(), user.getPrimaryPhoneNumber(), user.getIsActive(), authorities);
    }
}
