/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.kpi;

import com.forgerock.openbanking.analytics.model.KPI;
import com.forgerock.openbanking.analytics.model.entries.JwtsGenerationEntry;

import java.util.HashMap;
import java.util.Map;

public class JwtsGenerationKPI implements KPI {

    private Map<JwtsGenerationEntry.JwtType, Long> dataset;

    public JwtsGenerationKPI() {
        this.dataset = new HashMap<>();
        for (JwtsGenerationEntry.JwtType type : JwtsGenerationEntry.JwtType.values()) {
            dataset.put(type, 0l);
        }
    }

    public void increment(JwtsGenerationEntry.JwtType type, long value) {
        dataset.put(type, dataset.get(type) + value);
    }

    public Map<JwtsGenerationEntry.JwtType, Long> getDataset() {
        return dataset;
    }
}
