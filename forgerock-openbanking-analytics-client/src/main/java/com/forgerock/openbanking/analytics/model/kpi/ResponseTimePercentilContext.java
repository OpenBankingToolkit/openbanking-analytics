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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTimePercentilContext implements AggregationContext {
    private List<Long> previousResponseTimes = new ArrayList<>();


    public long getPercentile(int percentile) {
        synchronized (this) {

            Collections.sort(getPreviousResponseTimes());

            int index = (int) Math.round(getPreviousResponseTimes().size() * (percentile / 100.0)) - 1;
            Long value = 0l;
            if (0 <= index && index < getPreviousResponseTimes().size()) {
                value = getPreviousResponseTimes().get(index);
            }
            return value;
        }
    }

    public void addAll(List<Long> ms) {
        synchronized (this) {
            previousResponseTimes.addAll(ms);
        }
    }
}