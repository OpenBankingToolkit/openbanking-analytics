/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.kpi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTimePercentilContext implements AggregationContext {
    private List<Long> previousResponseTimes = new ArrayList<>();


    public long getPercentile(int percentile) {
        synchronized (this) {

            Collections.sort(getPreviousResponseTimes());

            int index = (int) Math.round(getPreviousResponseTimes().size() * (percentile / 100.0)) - 1;
            Long value = 0l;
            if (0 <= index && index < getPreviousResponseTimes().size()) {
                value = getPreviousResponseTimes().get(index);
            }
            return value;
        }
    }

    public void addAll(List<Long> ms) {
        synchronized (this) {
            previousResponseTimes.addAll(ms);
        }
    }
}