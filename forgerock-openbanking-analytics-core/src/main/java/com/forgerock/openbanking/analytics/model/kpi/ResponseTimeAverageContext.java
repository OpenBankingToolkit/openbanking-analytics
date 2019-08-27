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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTimeAverageContext implements AggregationContext {
    private Long nbCalls;
    private Double average;

    public void updateAverage(long entryResponseTime, long entryCount) {
        synchronized (this) {
            average = (average * nbCalls + entryResponseTime) / Double.valueOf(nbCalls + entryCount);
            nbCalls += entryCount;
        }
    }

}