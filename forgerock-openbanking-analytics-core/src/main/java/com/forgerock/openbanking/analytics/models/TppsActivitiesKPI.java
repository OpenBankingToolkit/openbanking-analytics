/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.models;

import com.forgerock.openbanking.analytics.model.KPI;
import com.forgerock.openbanking.analytics.model.entries.TppEntry;

import java.util.HashMap;
import java.util.Map;

public class TppsActivitiesKPI implements KPI {

    private Map<String, Integer> dataset = new HashMap<>();

    public void increment(TppEntry tpp) {
        dataset.putIfAbsent(tpp.getDirectoryId(), 0);
        dataset.compute(tpp.getDirectoryId(), (k, v) -> ++v);
    }

    public Map<String, Integer> getDataset() {
        return dataset;
    }
}
