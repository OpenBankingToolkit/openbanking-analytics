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
