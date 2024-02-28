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

import com.saika.hrmanagement.common.entity.AppMetadata;
import com.saika.hrmanagement.common.payload.AppMetadataRequest;
import com.saika.hrmanagement.employee.service.impl.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author mani
 */
public class AppMetadataUtil {

    public static AppMetadata createUpdateAppMetadata(final AppMetadataRequest appMetadataRequest, final AppMetadata appMetadataExist) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AppMetadata appMetadata = new AppMetadata();
        appMetadata.setGroup(appMetadataRequest.getGroup());
        appMetadata.setLabelKey(appMetadataRequest.getLabelKey());
        appMetadata.setValue(appMetadataRequest.getValue());
        appMetadata.setDescription(appMetadataRequest.getDescription());

        if (Objects.isNull(appMetadataRequest.getId()) || Objects.equals(appMetadataRequest.getId(), "0")) {
            appMetadata.setCreatedOn(LocalDateTime.now());
            appMetadata.setCreatedBy(user.getId());
        } else {
            if (!Objects.isNull(appMetadataExist)) {
                appMetadata.setCreatedOn(appMetadataExist.getCreatedOn());
                appMetadata.setCreatedBy(appMetadataExist.getCreatedBy());
            }
            appMetadata.setUpdatedOn(LocalDateTime.now());
            appMetadata.setUpdatedBy(user.getId());
            appMetadata.setId(appMetadataExist.getId());
        }

        return appMetadata;
    }
}
