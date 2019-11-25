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
