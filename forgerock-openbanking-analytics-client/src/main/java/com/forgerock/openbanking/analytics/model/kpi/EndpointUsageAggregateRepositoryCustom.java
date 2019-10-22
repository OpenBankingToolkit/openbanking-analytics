/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.kpi;

import com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface EndpointUsageAggregateRepositoryCustom {

    List<String> getSetDefinition(EndpointUsageKpiRequest request, DateTime from, DateTime to, String field);

    String getMax(EndpointUsageKpiRequest request, DateTime from, DateTime to, String field);


    Page<EndpointUsageAggregate> getEndpointUsageAggregateGroupBy(EndpointTableRequest request, PageRequest pageRequest);
}
