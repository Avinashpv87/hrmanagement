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

import com.saika.hrmanagement.common.entity.ClientDetail;
import com.saika.hrmanagement.common.payload.ClientRequest;
import com.saika.hrmanagement.employee.service.impl.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author mani
 */
public class ClientDetailUtil {

    public static ClientDetail createUpdateClientDetail(final ClientRequest clientRequest, final ClientDetail clientDetailExist) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClientDetail cd = new ClientDetail();
        cd.setName(clientRequest.getName());
        cd.setCode(clientRequest.getCode());
        cd.setAddressLine1(clientRequest.getAddressLine1());
        cd.setAddressLine2(clientRequest.getAddressLine2());
        cd.setCity(clientRequest.getCity());
        cd.setState(clientRequest.getState());
        cd.setPostalCode(clientRequest.getPostalCode());
        cd.setCountry(clientRequest.getCountry());
        cd.setDescription(clientRequest.getDescription());
        cd.setEmail(clientRequest.getEmail());
        cd.setRelationshipDate(clientRequest.getRelationshipDate());
        cd.setPrimaryPhoneNumber(clientRequest.getPrimaryPhoneNumber());
        cd.setSecondaryPhoneNumber(clientRequest.getSecondaryPhoneNumber());
        cd.setIsActive(clientRequest.getIsActive());
        if (Objects.isNull(clientRequest.getId()) || Objects.equals(clientRequest.getId(), "0")) {
            cd.setCreatedOn(LocalDateTime.now());
            cd.setCreatedBy(user.getId());
        } else {
            if (!Objects.isNull(clientDetailExist)) {
                cd.setCreatedOn(clientDetailExist.getCreatedOn());
                cd.setCreatedBy(clientDetailExist.getCreatedBy());
            }
            cd.setUpdatedOn(LocalDateTime.now());
            cd.setUpdatedBy(user.getId());
            cd.setId(clientDetailExist.getId());
        }

        return cd;
    }
}
