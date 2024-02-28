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

import com.saika.hrmanagement.common.entity.ClientDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author mani
 */
@Repository
public interface ClientDetailRepository extends MongoRepository<ClientDetail, String> {

    Page<ClientDetail> findByCodeContainingIgnoreCase(final String code, final Pageable pageable);

    @Query(value = "{$or: [ {'name': {$regex : /.*?0.*/, $options: 'i'} }, {'email': {$regex : /.*?0.*/, $options: 'i'}}, {'code': {$regex : /.*?0.*/, $options: 'i'}} ,{'description': {$regex : /.*?0.*/, $options: 'i'}} ]}")
    Page<ClientDetail> findAllClientDetailBySearch(final String search, final Pageable pageable);



    @Query(value = "{$or: [ {'name': {$regex : ?0, $options: 'i'} }, {'code': {$regex : ?1, $options: 'i'}}, {'email': {$regex : ?2, $options: 'i'}}]}")
    List<ClientDetail> getClientDetailByNameOrCodeOrEmail(final String name, final String code, final String email);
}