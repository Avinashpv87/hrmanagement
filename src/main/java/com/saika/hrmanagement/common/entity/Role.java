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
package com.saika.hrmanagement.common.entity;

import com.saika.hrmanagement.common.constant.ERole;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author mani
 */
@Document(collection = "role")
@Data
@RequiredArgsConstructor
public class Role {

    @Id
    private String id;

    @Indexed(unique = true, direction = IndexDirection.DESCENDING)
    @NotNull
    private ERole name;

    private LocalDateTime createdOn;

    private Long createdBy;

    private LocalDateTime updatedOn;

    private Long updatedBy;

}