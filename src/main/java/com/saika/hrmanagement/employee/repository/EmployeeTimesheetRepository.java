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
package com.saika.hrmanagement.employee.repository;

import com.saika.hrmanagement.common.entity.EmployeeTimesheet;
import com.saika.hrmanagement.common.entity.TimesheetChartAggregate;
import com.saika.hrmanagement.common.payload.Status;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * @author mani
 */
@Repository
public interface EmployeeTimesheetRepository extends MongoRepository<EmployeeTimesheet, String> {
      @Query(value = "{'createdBy': ?0}")
      Page<EmployeeTimesheet> findAllByEmployeeDetailId(final String createdBy, final Pageable pageable);

      @Query(value = "{'employeeId': ?0}")
      Page<EmployeeTimesheet> findEmployeeTimesheetsByEmpId(final ObjectId employeeId, final Pageable pageable);

      @Query(value = "{'employeeId': ?0}")
      List<EmployeeTimesheet> findAllTimesheetByEmployeeId(final ObjectId employeeId);

      @Query(value = "{'employeeId': ?0, 'start': ?1, 'end': ?2}")
      List<EmployeeTimesheet> findAllTimesheetByEmployeeIdAndFormAndToDate(final ObjectId employeeId, final LocalDate from, final LocalDate to);

      @Query("{ '$or': [ {'start' :  {  $gte: { $date :?0} }, 'end': { $lte: { $date :?1} } }, " +
                       " {'start' :  {  $lte: { $date :?1} }, 'end': { $gte: { $date :?0} } }," +
                       "], $and : [ { 'employeeId': ?2 } ] }")
      List<EmployeeTimesheet> findByDateBetween(final LocalDate start, final LocalDate end, final ObjectId userId);

      @Query(value = "{ 'status' : ?0, 'isTimesheetDeleted': null }", count = true)
      long countStatus(String status);

      @Query(value = "{'status': ?0, 'employeeId': ?1, 'isTimesheetDeleted': null }", count = true)
      long countStatusByEmployeeId(final String status, final ObjectId employeeId);

      @Query(value = "{$or: [ {'start': {$regex : /.*?1.*/, $options: 'i'} }, {'totalHrs': {$regex : /.*?1.*/, $options: 'i'}}, {'end': {$regex : /.*?1.*/, $options: 'i'}} ,{'comments': {$regex : /.*?1.*/, $options: 'i'}} ], $and:[{'createdBy': ?0}]}")
      Page<EmployeeTimesheet> getAllEmployeeTimesheetBySearch(final String createdBy, final String search, final Pageable pageable);


      @Query(value = "{$or: [ {'employeeName': {$regex : /.*?0.*/, $options: 'i'} }, {'totalHrs': {$regex : /.*?0.*/, $options: 'i'}}, {'end': {$regex : /.*?0.*/, $options: 'i'}} ,{'comments': {$regex : /.*?0.*/, $options: 'i'}} ]}")
      Page<EmployeeTimesheet> getAllTimesheetBySearch(final String search, final Pageable pageable);


      @Query("{ '$or': [ {'start' :  {  $gte: { $date :?0} }, 'end': { $lte: { $date :?1} } }, " +
              " {'start' :  {  $lte: { $date :?1} }, 'end': { $gte: { $date :?0} } }," +
              "]," +
              " $and : [{ 'employeeId': {$in : ?2 } }, { 'status': { $in : ?3} }] }")
      List<EmployeeTimesheet> findTimesheetByDateOrEmployeeIdAndStatus(final LocalDate start, final LocalDate end, final List<ObjectId> userId, final List<Status> status);

      @Query("{ '$or': [ {'start' :  {  $gte: { $date :?0} }, 'end': { $lte: { $date :?1} } }, " +
              " {'start' :  {  $lte: { $date :?1} }, 'end': { $gte: { $date :?0} } }," +
              "]," +
              " $and : [{ 'status': { $in : ?2} }] }")
      List<EmployeeTimesheet> findTimesheetByDateOrEmployeeAllAndStatus(final LocalDate start, final LocalDate end, final List<Status> status);


      @Query("{ '$or': [ {'start' :  {  $gte: { $date :?0} }, 'end': { $lte: { $date :?1} } }, " +
              " {'start' :  {  $lte: { $date :?1} }, 'end': { $gte: { $date :?0} } }," +
              "] }")
      List<EmployeeTimesheet> findTimesheetByDate(final LocalDate start, final LocalDate end);


      @Aggregation(
              "{\n" +
                      "      $group: {\n" +
                      "            _id: {\n" +
                      "                  month: {\n" +
                      "                        $month: \"$start\",\n" +
                      "                  },\n" +
                      "                  year: {\n" +
                      "                        $year: \"$start\",\n" +
                      "                  },\n" +
                      "                  status: \"$status\",\n" +
                      "            },\n" +
                      "            totalHours: {\n" +
                      "                  $sum: {\n" +
                      "                        $toInt: \"$totalHrs\",\n" +
                      "                  },\n" +
                      "            },\n" +
                      "            count: {\n" +
                      "                  $sum: 1,\n" +
                      "            },\n" +
                      "      },\n" +
                      "}"
      )
      List<TimesheetChartAggregate> findTotalHrsAndGroupByMonthYearAndStatus();

      @Aggregation("{\n" +
              "    $group: {\n" +
              "      _id: {\n" +
              "        month: {\n" +
              "          $month: \"$start\",\n" +
              "        },\n" +
              "        year: {\n" +
              "          $year: \"$start\",\n" +
              "        },\n" +
              "        status: \"$status\",\n" +
              "      },\n" +
              "      totalHours: {\n" +
              "        $sum: {\n" +
              "          $toInt: \"$totalHrs\",\n" +
              "        },\n" +
              "      },\n" +
              "      count: {\n" +
              "        $sum: 1,\n" +
              "      },\n" +
              "    },\n" +
              "  },\n" +
              "  {\n" +
              "    $sort:\n" +
              "      {\n" +
              "        month: 1,\n" +
              "      },\n" +
              "  }")
      List<TimesheetChartAggregate> findTotalHoursByMonthAndYearAndApproveStatus();

      @Aggregation(pipeline = {
              "{ $match: { 'employeeId' : ?0 } }",
              "{ $group : { _id: { month:  { $toString: { $month: '$start' } }, year: { $toString: { $year : '$start'} }, status: '$status' }, totalHours: { $sum : { $toInt: '$totalHrs' } }, count: { $sum : 1} } } ",
              "{ $project: { _id: 1, totalHours: 1, count: 1}  }"
      })
      List<TimesheetChartAggregate> findTotalHoursByMonthAndYearAndApproveStatusForEmployee(final ObjectId userId);

}