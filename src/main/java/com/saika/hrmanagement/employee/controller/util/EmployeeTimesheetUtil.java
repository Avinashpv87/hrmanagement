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

import com.saika.hrmanagement.common.entity.DateTaskComment;
import com.saika.hrmanagement.common.entity.EmployeeTimesheet;
import com.saika.hrmanagement.common.entity.EmployeeTimesheetDocument;
import com.saika.hrmanagement.common.exception.CustomApplicationException;
import com.saika.hrmanagement.common.payload.EmployeeTimesheetRequest;
import com.saika.hrmanagement.employee.service.impl.UserDetailsImpl;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author mani
 */
public class EmployeeTimesheetUtil {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public static EmployeeTimesheet createUpdateEmployeeTimesheet(final EmployeeTimesheetRequest employeeTimesheetRequest, final EmployeeTimesheet employeeTimesheetExist) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmployeeTimesheet et = new EmployeeTimesheet();

        AtomicReference<Double> hrsPerDayTotal = new AtomicReference<>(0.0);
        List<DateTaskComment> dateTaskCommentList = employeeTimesheetRequest.getDateTaskComment().stream().map(request -> {
            DateTaskComment dtc = new DateTaskComment();
            dtc.setDate(request.getDate());
            dtc.setHrs(request.getHrs());
            hrsPerDayTotal.set(hrsPerDayTotal.get() + Double.parseDouble(request.getHrs()));
            dtc.setComments(request.getComments());
            return dtc;
        }).collect(Collectors.toList());
        et.setDateTaskComment(dateTaskCommentList);
        et.setComments(employeeTimesheetRequest.getComments());
        et.setStart(employeeTimesheetRequest.getStart());
        et.setEnd(employeeTimesheetRequest.getEnd());

        //this logic will compare per day hrs vs total
        if (!Objects.equals(hrsPerDayTotal.get().doubleValue(), Double.valueOf(employeeTimesheetRequest.getTotalHrs()))) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Total Hrs not matching with per day hours!");
        }
        et.setTotalHrs(employeeTimesheetRequest.getTotalHrs());
        et.setEmployeeName(user.getLastName()+", "+user.getFirstName());
        if (Objects.isNull(employeeTimesheetRequest.getId()) || Objects.equals(employeeTimesheetRequest.getId(), "0")) {
            et.setCreatedOn(LocalDateTime.now());
            et.setCreatedBy(user.getId());
            et.setEmployeeId(new ObjectId(user.getId()));

        } else {
            if (!Objects.isNull(employeeTimesheetExist)) {
                et.setCreatedOn(employeeTimesheetExist.getCreatedOn());
                et.setCreatedBy(employeeTimesheetExist.getCreatedBy());
            }
            et.setUpdatedOn(LocalDateTime.now());
            et.setUpdatedBy(user.getId());
            et.setId(employeeTimesheetExist.getId());
            et.setEmployeeId(new ObjectId(employeeTimesheetExist.getCreatedBy()));
        }

        et.setStatus(employeeTimesheetRequest.getStatus());

        return et;
    }

    public  List<EmployeeTimesheetDocument> createUpdateEmployeeTimesheetDocument(EmployeeTimesheet employeeTimesheet, MultipartFile[] files) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<EmployeeTimesheetDocument> employeeTimesheetDocuments = new ArrayList<>();
        try {
            if (Objects.nonNull(files)) {
                for (MultipartFile file : files) {
                    EmployeeTimesheetDocument employeeTimesheetDocument = new EmployeeTimesheetDocument();
                    employeeTimesheetDocument.setFileName(file.getOriginalFilename());
                    employeeTimesheetDocument.setFileContent(file.getBytes());
                    employeeTimesheetDocument.setFileContentType(file.getContentType());
                    employeeTimesheetDocument.setFileSize(file.getSize());
                    employeeTimesheetDocument.setCreatedOn(LocalDateTime.now());
                    employeeTimesheetDocument.setCreatedBy(user.getId());
                    //employeeTimesheetDocument.setEmployeeTimesheet(employeeTimesheet);
                    var stringObjectId = new ObjectId(employeeTimesheet.getId());
                    employeeTimesheetDocument.setEmployeeTimesheetId(stringObjectId);
                    employeeTimesheetDocuments.add(employeeTimesheetDocument);
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
            LOGGER.error("Error Occured at createUpdateEmployeeTimesheetDocument {} ", ioException.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error Occured at createUpdateEmployeeTimesheetDocument {} ", e.getLocalizedMessage());
        }

        return employeeTimesheetDocuments;
    }
}
