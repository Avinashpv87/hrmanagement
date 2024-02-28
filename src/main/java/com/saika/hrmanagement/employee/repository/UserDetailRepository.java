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

import com.saika.hrmanagement.common.constant.EWorkAuthType;
import com.saika.hrmanagement.common.entity.EmployeeDetail;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author mani
 */
@Repository
public interface UserDetailRepository extends MongoRepository<EmployeeDetail, Long> {

        @Query(value = "$or: [{'userName': {$regex : /^?0$/, $options: 'i'}, {'email': {$regex : /^?1$/, $options: 'i'}}]")
        List<EmployeeDetail> findAllByUserNameOrEmail(final String userName, final String email);

        @Query(value = "{'email': {$regex : /^?0$/, $options: 'i'}}")
        List<EmployeeDetail> findAllByEmail(final String email);
        EmployeeDetail findByIsActiveToken(final String isActiveToken);

        EmployeeDetail findByEnrollToken(final String enrollToken);

        @Query(value = "{'userName': {$regex : /^?0$/, $options: 'i'}, 'isActive': ?1}")
        EmployeeDetail findByUserNameAndIsActive(final String userName, final Boolean isActive);

        @Query(value = "{'passwordResetToken': ?0 }")
        EmployeeDetail findByPasswordResetToken(final String passwordResetTokens);
        @Query(value = "{'email': {$regex : /^?0$/, $options: 'i'}, 'isActive': ?1}")
        EmployeeDetail findByEmailAndIsActive(final String email,Boolean isActive);

        @Query(value = "{'id': ?0 }")
        EmployeeDetail findByEmployeeId(final String id);

        @Query(value = "{isEnrolled: ?0}", count = true)
        long countIsEnrolled(final Boolean isEnrolled);

        @Query(value = "{isActive: ?0}", count = true)
        long countIsActive(final Boolean isActive);

        @Query(value = "{isDelete: ?0}", count = true)
        long countIsDelete(final Boolean isDelete);

        @Query(value = "{workAuthType: ?0, isActive: true}", count = true)
        long countWorkAuthType(final EWorkAuthType workAuthType);

        @Aggregation("{ '$group' : { '_id' : null, 'sumTotalVisits' : { $sum: '$visits' } } }")
        Long sumTotalVisits();

        @Query(value = "{designation: ?0}", count = true)
        long countDesignation(final String designation);


}

