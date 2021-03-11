/**
 * Copyright 2019 ForgeRock AS.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.forgerock.openbanking.analytics.repository;

import com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate;
import com.forgerock.openbanking.analytics.model.kpi.EndpointUsageAggregateRepositoryCustom;
import com.forgerock.openbanking.api.annotations.OBGroupName;
import com.forgerock.openbanking.api.annotations.OBReference;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;


public interface EndpointUsageAggregateRepository extends MongoRepository<EndpointUsageAggregate, Date>, EndpointUsageAggregateRepositoryCustom {

    void deleteByDateIsNull();

    void deleteByDateBetween(
            @Param("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @Param("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate);

    void deleteByDateBetweenAndTppEntryIsNotNull(
            @Param("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @Param("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate);

    Page<EndpointUsageAggregate> findByDateBetween(
            @Param("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @Param("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate,
            @Param("page") Pageable pageable
    );
    Stream<EndpointUsageAggregate> findByDateBetween(
            @Param("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @Param("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate);

    Stream<EndpointUsageAggregate> findByDateBetweenAndTppEntryIsNotNull(
            @Param("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @Param("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate);

    Stream<EndpointUsageAggregate> findByEndpointAndDateBetween(
            @Param("endpoint") String endpoint,
            @Param("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @Param("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate);

    Stream<EndpointUsageAggregate> findByEndpointAndIdentityIdInAndDateBetween(
            @Param("endpoint") String endpoint,
            @Param("identityIds") List<String> identityIds,
            @Param("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @Param("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate);

    Stream<EndpointUsageAggregate> findByEndpointAndResponseStatusInAndDateBetween(
            @Param("endpoint") String endpoint,
            @Param("responseStatus") List<String> responseStatus,
            @Param("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @Param("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate);

    Stream<EndpointUsageAggregate> findByEndpointAndIdentityIdInAndResponseStatusInAndDateBetween(
            @Param("endpoint") String endpoint,
            @Param("identityIds") List<String> identityIds,
            @Param("responseStatus") List<String> responseStatus,
            @Param("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @Param("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate);

    Stream<EndpointUsageAggregate> findByObReferenceInAndResponseStatusInAndDateBetween(
            @Param("obReferences") List<OBReference> obReferences,
            @Param("responseStatus") List<String> responseStatus,
            @Param("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @Param("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate);

    Stream<EndpointUsageAggregate> findByObReferenceInAndDateBetween(
            @Param("obReferences") List<OBReference> obReferences,
            @Param("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @Param("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate);

    Stream<EndpointUsageAggregate> findByEndpointTypeInAndDateBetween(
            @Param("endpointTypes") List<OBGroupName> endpointTypes,
            @Param("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @Param("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate);

    Stream<EndpointUsageAggregate> findByEndpointTypeInAndResponseStatusInAndDateBetween(
            @Param("endpointTypes") List<OBGroupName> endpointTypes,
            @Param("responseStatus") List<String> responseStatus,
            @Param("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @Param("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate);

    Stream<EndpointUsageAggregate> findByEndpointTypeInAndIdentityIdInAndResponseStatusInAndDateBetween(
            @Param("endpointTypes") List<OBGroupName> endpointTypes,
            @Param("identityIds") List<String> identityIds,
            @Param("responseStatus") List<String> responseStatus,
            @Param("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @Param("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate);

    Stream<EndpointUsageAggregate> findByEndpointTypeInAndIdentityIdInAndDateBetween(
            @Param("endpointTypes") List<OBGroupName> endpointTypes,
            @Param("identityIds") List<String> identityIds,
            @Param("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @Param("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate);
}
