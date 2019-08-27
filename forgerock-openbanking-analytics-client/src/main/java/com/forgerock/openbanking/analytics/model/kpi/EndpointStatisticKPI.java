/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.kpi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EndpointStatisticKPI {


    private String endpointName;

    private Map<AggregationMethod, Long> valuesByAggregationMethods = new HashMap<>();
    @JsonIgnore
    private Map<AggregationMethod, AggregationContext> contextByAggregationMethods = new HashMap<>();


    public void incrementCounters(EndpointUsageAggregate endpointUsageAggregate) {

        for(AggregationMethod aggregationMethod: AggregationMethod.values()) {
            AggregationContext context = aggregationMethod.updateContext(valuesByAggregationMethods.get(aggregationMethod), contextByAggregationMethods.get(aggregationMethod), endpointUsageAggregate);
            valuesByAggregationMethods.put(aggregationMethod, aggregationMethod.getValue(context));
            contextByAggregationMethods.put(aggregationMethod, context);
        }
    }
}
