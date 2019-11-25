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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EndpointsUsageKPI {


    public EndpointsUsageKPI(String endpointName, DateGranularity dateGranularity, Collection<Line> values, Map<EndpointsUsageAggregation, List> definitions) {
        this.endpointName = endpointName;
        this.dateGranularity = dateGranularity;
        this.lines = new ArrayList<>(values);
        this.definition = definitions.entrySet().stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue));
    }

    public enum DateGranularity {
        BY_YEAR, BY_MONTH, BY_DAY, BY_HOUR
    }

    private String endpointName;
    private DateGranularity dateGranularity;
    private List<Line> lines;
    private Map<EndpointsUsageAggregation, Collection> definition;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class Line {
        private String name;
        private long[] dataset;
        private AggregationMethod.Unity yAxisID;
        @JsonIgnore
        private AggregationContext[] context;
        private AggregationMethod aggregationMethod;

        public AggregationMethod.Unity getyAxisID() {
            return aggregationMethod.getUnity();
        }
    }
}
