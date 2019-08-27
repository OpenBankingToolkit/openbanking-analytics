/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.kpi;

import com.forgerock.openbanking.analytics.model.KPI;

import java.util.HashMap;
import java.util.Map;

public class JwtsValidationKPI implements KPI {

    private Map<Boolean, Long> dataset;

    public JwtsValidationKPI() {
        this.dataset = new HashMap<>();
        dataset.put(true, 0l);
        dataset.put(false, 0l);

    }

    public void increment(Boolean isValid, long count) {
        dataset.put(isValid, dataset.get(isValid) + count);
    }

    public Map<Boolean, Long> getDataset() {
        return dataset;
    }
}
