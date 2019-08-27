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

public class KeysTypeKPI implements KPI {

    public enum Type {
        TRANSPORT, SIGNING, ENCRYPTION
    }

    private Map<Type, Long> dataset;

    public KeysTypeKPI() {
        this.dataset = new HashMap<>();

        for (Type type : Type.values()) {
            dataset.put(type, 0l);
        }
    }

    public void increment(Type type) {
        increment(type, 1);
    }
    public void increment(Type type, int number) {
        dataset.put(type, dataset.get(type) + number);
    }

    public Map<Type, Long> getDataset() {
        return dataset;
    }
}
