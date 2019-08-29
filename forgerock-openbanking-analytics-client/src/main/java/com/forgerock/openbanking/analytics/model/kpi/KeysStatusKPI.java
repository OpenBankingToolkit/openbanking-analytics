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

public class KeysStatusKPI implements KPI {

    public enum Status {
        EXPIRED, ACTIVE, REVOKED
    }

    private Map<Status, Long> dataset;

    public KeysStatusKPI() {
        this.dataset = new HashMap<>();
        for (Status type : Status.values()) {
            dataset.put(type, 0l);
        }
    }

    public void increment(Status status) {
        dataset.put(status, dataset.get(status) + 1);
    }

    public Map<Status, Long> getDataset() {
        return dataset;
    }
}
