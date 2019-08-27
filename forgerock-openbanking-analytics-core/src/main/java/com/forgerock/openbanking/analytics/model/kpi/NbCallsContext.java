/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.kpi;

import lombok.Data;

@Data
public class NbCallsContext implements AggregationContext {

    private Long nbCalls = 0l;

    public void incrementValue(long value) {
        synchronized (this) {
            nbCalls += value;
        }
    }
}