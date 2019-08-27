/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.kpi;

import com.forgerock.openbanking.analytics.model.KPI;
import com.nimbusds.jose.jwk.KeyType;

import java.util.HashMap;
import java.util.Map;

public class KeysAlgorithmKPI implements KPI {

    private Map<String, Long> dataset;

    public KeysAlgorithmKPI() {
        this.dataset = new HashMap<>();
        dataset.put(KeyType.EC.getValue(), 0l);
        dataset.put(KeyType.RSA.getValue(), 0l);
    }

    public void increment(KeyType type) {
        dataset.put(type.getValue(), dataset.get(type.getValue()) + 1);
    }

    public Map<String, Long> getDataset() {
        return dataset;
    }
}
