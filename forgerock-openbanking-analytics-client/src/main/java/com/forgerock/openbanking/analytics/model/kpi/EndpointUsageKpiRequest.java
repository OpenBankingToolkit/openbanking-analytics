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
