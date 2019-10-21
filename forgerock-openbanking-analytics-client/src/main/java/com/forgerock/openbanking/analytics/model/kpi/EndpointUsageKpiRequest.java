/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.kpi;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.forgerock.openbanking.serialiser.IsoDateTimeDeserializer;
import com.forgerock.openbanking.serialiser.IsoDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.util.LinkedList;

import static com.forgerock.openbanking.analytics.model.kpi.EndpointsUsageAggregation.BY_RESPONSE_TIME;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public
class EndpointUsageKpiRequest {
    public String endpoint;
    @JsonDeserialize(using = IsoDateTimeDeserializer.class)
    @JsonSerialize(using = IsoDateTimeSerializer.class)
    public DateTime from;
    @JsonDeserialize(using = IsoDateTimeDeserializer.class)
    @JsonSerialize(using = IsoDateTimeSerializer.class)
    public DateTime to;
    public EndpointsUsageKPI.DateGranularity dateGranularity;
    public LinkedList<EndpointsUsageAggregation> aggregations;
    public EndpointUsageFiltering filtering;
    public AggregationMethod aggregationMethod;

    public boolean isCacheable() {
        return to.compareTo(DateTime.now().minusDays(1)) < 0;
    }

    public AggregationMethod getAggregationMethod() {
        if (aggregationMethod == null) {
            if (getAggregations().contains(BY_RESPONSE_TIME)) {
                setAggregationMethod(AggregationMethod.BY_NB_RESPONSE_TIME);
            } else {
                setAggregationMethod(AggregationMethod.BY_NB_CALLS);
            }
        }
        return aggregationMethod;
    }
}
