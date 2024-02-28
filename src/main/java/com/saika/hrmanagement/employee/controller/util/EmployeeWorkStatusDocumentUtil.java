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

import com.saika.hrmanagement.common.entity.EmployeeWorkStatusDocument;
import com.saika.hrmanagement.common.payload.EmployeeWorkStatusDocumentRequest;
import com.saika.hrmanagement.employee.service.impl.UserDetailsImpl;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author mani
 */
public class EmployeeWorkStatusDocumentUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeWorkStatusDocumentUtil.class);
    public static List<EmployeeWorkStatusDocument> createUpdateEmployeeWorkStatusDocuments(final EmployeeWorkStatusDocumentRequest employeeWorkStatusDocumentRequest, final EmployeeWorkStatusDocument employeeWorkStatusDocumentExist, final MultipartFile[] files) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<EmployeeWorkStatusDocument> employeeWorkStatusDocuments = new ArrayList<>();
        try {
            if (Objects.nonNull(files)) {
                for (MultipartFile file : files) {
                    EmployeeWorkStatusDocument employeeWorkStatusDocument = new EmployeeWorkStatusDocument();
                    employeeWorkStatusDocument.setFileName(file.getOriginalFilename());
                    employeeWorkStatusDocument.setFileContent(file.getBytes());
                    employeeWorkStatusDocument.setFileContentType(file.getContentType());
                    employeeWorkStatusDocument.setFileSize(file.getSize());
                    employeeWorkStatusDocument.setCreatedOn(LocalDateTime.now());
                    employeeWorkStatusDocument.setCreatedBy(user.getId());

                    //todo
                    employeeWorkStatusDocument.setDocType(employeeWorkStatusDocumentRequest.getDocType());
                    employeeWorkStatusDocument.setWorkAuthType(employeeWorkStatusDocumentRequest.getWorkAuthType());
                    employeeWorkStatusDocument.setStatus(employeeWorkStatusDocumentRequest.getStatus());
                    employeeWorkStatusDocument.setStartDate(employeeWorkStatusDocumentRequest.getStartDate());
                    employeeWorkStatusDocument.setEndDate(employeeWorkStatusDocumentRequest.getEndDate());

                    var empObjectId = new ObjectId(user.getId());
                    //employeeWorkStatusDocument.setEmployeeWorkStatusId(stringObjectId);
                    employeeWorkStatusDocument.setEmployeePkId(empObjectId);

                    //add files one by one
                    employeeWorkStatusDocuments.add(employeeWorkStatusDocument);
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
            LOGGER.error("Error Occured at createUpdateEmployeeTimesheetDocument {} ", ioException.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error Occured at createUpdateEmployeeTimesheetDocument {} ", e.getLocalizedMessage());
        }

        return employeeWorkStatusDocuments;
    }

    public static EmployeeWorkStatusDocument createUpdateEmployeeWorkStatusDocument(final EmployeeWorkStatusDocumentRequest employeeWorkStatusDocumentRequest, final EmployeeWorkStatusDocument employeeWorkStatusDocumentExist, final MultipartFile files, final Boolean employerUpload) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmployeeWorkStatusDocument employeeWorkStatusDocument = new EmployeeWorkStatusDocument();
        try {
                if (Objects.nonNull(files)) {
                    employeeWorkStatusDocument.setFileName(files.getOriginalFilename());
                    employeeWorkStatusDocument.setFileContent(files.getBytes());
                    employeeWorkStatusDocument.setFileContentType(files.getContentType());
                    employeeWorkStatusDocument.setFileSize(files.getSize());
                }
                //todo
                employeeWorkStatusDocument.setDocType(employeeWorkStatusDocumentRequest.getDocType());
                employeeWorkStatusDocument.setWorkAuthType(employeeWorkStatusDocumentRequest.getWorkAuthType());
                employeeWorkStatusDocument.setStatus(employeeWorkStatusDocumentRequest.getStatus());
                employeeWorkStatusDocument.setStartDate(employeeWorkStatusDocumentRequest.getStartDate());
                employeeWorkStatusDocument.setEndDate(employeeWorkStatusDocumentRequest.getEndDate());
                employeeWorkStatusDocument.setComments(employeeWorkStatusDocumentRequest.getComments());
                employeeWorkStatusDocument.setVisibleToEmployee(employeeWorkStatusDocumentRequest.isVisibleToEmployee());
                employeeWorkStatusDocument.setIsDeleted(Boolean.FALSE);
                var empObjectId = new ObjectId(employerUpload ? employeeWorkStatusDocumentRequest.getEmployeeId() : user.getId());
                employeeWorkStatusDocument.setEmployeePkId(empObjectId);

                if (Objects.nonNull(employeeWorkStatusDocumentExist)){
                    if (!Objects.isNull(employeeWorkStatusDocumentExist)) {
                        employeeWorkStatusDocument.setCreatedOn(employeeWorkStatusDocumentExist.getCreatedOn());
                        employeeWorkStatusDocument.setCreatedBy(employeeWorkStatusDocumentExist.getCreatedBy());
                    }
                    employeeWorkStatusDocument.setUpdatedOn(LocalDateTime.now());
                    employeeWorkStatusDocument.setUpdatedBy(user.getId());
                    employeeWorkStatusDocument.setId(employeeWorkStatusDocumentExist.getId());
                } else {
                    employeeWorkStatusDocument.setCreatedOn(LocalDateTime.now());
                    employeeWorkStatusDocument.setCreatedBy(user.getId());
                }

        } catch (IOException ioException) {
            ioException.printStackTrace();
            LOGGER.error("Error Occured at createUpdateEmployeeTimesheetDocument {} ", ioException.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error Occured at createUpdateEmployeeTimesheetDocument {} ", e.getLocalizedMessage());
        }

        return employeeWorkStatusDocument;
    }

}

