/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.repository;

import com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate;
import com.forgerock.openbanking.analytics.model.kpi.EndpointUsageAggregateRepositoryCustom;
import com.forgerock.openbanking.analytics.model.openbanking.OBGroupName;
import com.forgerock.openbanking.analytics.model.openbanking.OBReference;
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

    void deleteByDateBetween(
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
