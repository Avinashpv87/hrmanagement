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

import com.saika.hrmanagement.common.entity.AppMetadata;
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.employee.repository.AppMetadataRepository;
import com.saika.hrmanagement.employee.service.AppMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/***
 * @author mani
 */
@Service
public class AppMetadataServiceImpl implements AppMetadataService {

    @Autowired
    private AppMetadataRepository appMetadataRepository;

    @Override
    public AppMetadata createAppMetadata(final AppMetadata appMetadata) throws CustomApplicationException {
        return appMetadataRepository.save(appMetadata);
    }

    @Override
    public Page<AppMetadata> getAllAppMetadata(final Pageable pageable) {
        return appMetadataRepository.findAll(pageable);
    }

    @Override
    public AppMetadata getAppMetadataById(final String id)  {
        return appMetadataRepository.findById(id).orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND, id +" Not Found"));
    }

    @Override
    public AppMetadata updateAppMetadata(final AppMetadata updateAppMetadata) {

        return appMetadataRepository.save(updateAppMetadata);
    }

    public AppMetadata deleteAppMetadata(AppMetadata AppMetadataExist) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AppMetadataExist.setIsDelete(false);
        AppMetadataExist.setUpdatedBy(user.getId());
        AppMetadataExist.setUpdatedOn(LocalDateTime.now());
        return appMetadataRepository.save(AppMetadataExist);
    }
}
