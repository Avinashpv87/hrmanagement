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

import com.saika.hrmanagement.common.entity.EmployeeClientMapping;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * @author mani
 */
@Repository
public interface EmployeeClientMappingRepository extends MongoRepository<EmployeeClientMapping, String> {

    @Query("{ '$or': [ {'startDate' :  {  $gte: { $date :?0} }, 'endDate': { $lte: { $date :?1} } }, " +
                      "{'startDate' :  {  $lte: { $date :?1} }, 'endDate': { $gte: { $date :?0} } }" +
                    "], $and : [ { 'employeeDetailId': ?2 }, { 'clientDetailId': ?3 } ] }")
    List<EmployeeClientMapping> findByDateBetweenForEmployeeClient(final LocalDate start, final LocalDate end, final ObjectId employeePkId, final ObjectId clientPkId);
    @Query(value = "{$or: [ {'clientDetail': { $elemMatch: {'name': {$regex : /.*?0.*/, $options: 'i'}}} },{'clientDetail': { $elemMatch: {'code': {$regex : /.*?0.*/, $options: 'i'}}} } ]}")
    Page<EmployeeClientMapping> getAllClientsBySearch(final String search, final Pageable pageable);

    @Query(value = "{'employeeDetailId': ?0 }")
    List<EmployeeClientMapping> findByEmployeeDetailId(ObjectId objectId);
}