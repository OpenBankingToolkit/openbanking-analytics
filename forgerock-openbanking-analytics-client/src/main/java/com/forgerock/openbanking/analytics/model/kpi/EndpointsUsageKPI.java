/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
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
