/**
 * Copyright 2019 ForgeRock AS.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
