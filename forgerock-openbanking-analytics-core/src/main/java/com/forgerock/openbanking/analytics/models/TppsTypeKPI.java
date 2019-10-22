/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.models;

import com.forgerock.openbanking.analytics.model.KPI;
import com.forgerock.openbanking.model.SoftwareStatementRole;

import java.util.HashMap;
import java.util.Map;

public class TppsTypeKPI implements KPI {

    private Map<SoftwareStatementRole, Integer> dataset;

    public TppsTypeKPI() {
        this.dataset = new HashMap<>();

        for (SoftwareStatementRole type : SoftwareStatementRole.values()) {
            if (type == SoftwareStatementRole.ASPSP)
                continue;
            dataset.put(type, 0);

        }
    }

    public void increment(SoftwareStatementRole type) {
        if (dataset.keySet().contains(type)) {
            dataset.compute(type, (k, v) -> ++v);
        }
    }

    public Map<SoftwareStatementRole, Integer> getDataset() {
        return dataset;
    }
}
