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

import com.saika.hrmanagement.common.entity.EmployeeDetail;
import com.saika.hrmanagement.common.entity.EmployeeTimesheet;
import com.saika.hrmanagement.common.payload.Status;
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
public interface EmployeeDetailRepository extends MongoRepository<EmployeeDetail, String> {

    @Query(value = "{$or: [ {'userName': {$regex : /.*?0.*/, $options: 'i'} }, {'email': {$regex : /.*?0.*/, $options: 'i'}}, {'firstName': {$regex : /.*?0.*/, $options: 'i'}} ,{'lastName': {$regex : /.*?0.*/, $options: 'i'}} ]}")
    Page<EmployeeDetail> getAllEmployeeDetailsBySearch(final String search, final Pageable pageable);

    @Query(value = "{'_id': {$in : ?0 }, 'isActive': true }", fields = "{ 'email' : 1 }")
    List<EmployeeDetail> fetchAllEmailByIdAndIsActive(final List<ObjectId> ids);

    @Query(value = "{'isActive': true }", fields = "{ 'email' : 1 }")
    List<EmployeeDetail> findAllByIsActive();

    @Query("{ '$or': [ {'dateOfJoin' :  {  $gte: { $date :?0} , $lte: { $date :?1} } }, " +
            "]," +
            " $and : [{ $or : [ { '_id': {$in : ?2 } }, { 'isActive':  ?3 }, { 'isEnrolled':  ?4 }, { 'isDelete':  ?5 } ] }] }")
    List<EmployeeDetail> findEmployeeByDateOrEmployeeIdOrStatus1(final LocalDate start, final LocalDate end, final List<ObjectId> userId, final Boolean isActive, final Boolean isEnrolled, final Boolean isDelete);

    @Query("{ '$and': [ {'dateOfJoin' :  {  $gte: { $date :?0} , $lte: { $date :?1} }, " +
            " '_id': {'$in' : ?2 } , 'isActive':  ?3 , 'isEnrolled':  ?4 ,  'isDelete':  ?5 } ] }")
    List<EmployeeDetail> findEmployeeByDateOrEmployeeIdAndStatus(final LocalDate start, final LocalDate end, final List<ObjectId> userId, final Boolean isActive, final Boolean isEnrolled, final Boolean isDelete);


    @Query("{ '$and': [ {'dateOfJoin' :  {  $gte: { $date :?0} , $lte: { $date :?1} }, " +
            " 'isActive':  ?2 ,  'isEnrolled':  ?3 ,  'isDelete':  ?4 } ] }")
    List<EmployeeDetail> findEmployeeByDateOrEmployeeAllAndStatus(final LocalDate start, final LocalDate end, final Boolean isActive, final Boolean isEnrolled, final Boolean isDelete);

    @Query("{ '$and': [ {'dateOfJoin' :  {  $gte: { $date :?0} , $lte: { $date :?1} } } ] }")
    List<EmployeeDetail> findEmployeeByDate(final LocalDate start, final LocalDate end);
}