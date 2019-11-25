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
