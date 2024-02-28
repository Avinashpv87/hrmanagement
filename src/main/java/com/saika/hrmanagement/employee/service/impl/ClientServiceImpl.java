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

import com.saika.hrmanagement.common.entity.ClientDetail;
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.employee.repository.ClientDetailRepository;
import com.saika.hrmanagement.employee.service.ClientService;
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
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientDetailRepository clientDetailRepository;

    @Override
    public ClientDetail createClientDetail(final ClientDetail clientDetail) throws CustomApplicationException {
        return clientDetailRepository.save(clientDetail);
    }

    @Override
    public Page<ClientDetail> getAllClientDetails(final Pageable pageable) {
        return clientDetailRepository.findAll(pageable);
    }

    @Override
    public ClientDetail getClientDetailById(final String clientId)  {
        return clientDetailRepository.findById(clientId).orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND, clientId +" Not Found"));
    }

    @Override
    public ClientDetail updateClientDetail(final ClientDetail updateClientDetail) {

        return clientDetailRepository.save(updateClientDetail);
    }

    public ClientDetail deleteClientDetail(ClientDetail clientDetailExist) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        clientDetailExist.setIsActive(Boolean.FALSE);
        clientDetailExist.setUpdatedBy(user.getId());
        clientDetailExist.setUpdatedOn(LocalDateTime.now());
        return clientDetailRepository.save(clientDetailExist);
    }
}
