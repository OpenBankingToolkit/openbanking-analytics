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
