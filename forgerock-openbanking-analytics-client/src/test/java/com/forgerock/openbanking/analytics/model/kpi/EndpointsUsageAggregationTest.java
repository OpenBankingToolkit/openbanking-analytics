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

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EndpointsUsageAggregationTest {

    @Test
    public void getValuesFromEntryForMs_valid() {
        // Given
        List definitionsSet = ImmutableList.of(10.0, 10.0, 1000.0, 10000.0);

        // When
        List result = EndpointsUsageAggregation.BY_RESPONSE_TIME.getValuesFromEntryForMs(Collections.singletonList(10L), definitionsSet);

        // Then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(10.0);
    }

    @Test
    public void getValuesFromEntryForMs_empty() {
        // Given
        List definitionsSet = Collections.emptyList();

        // When
        List result = EndpointsUsageAggregation.BY_RESPONSE_TIME.getValuesFromEntryForMs(Collections.singletonList(10L), definitionsSet);

        // Then
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void getValuesFromEntryForMs_noLogScale() {
        // Given
        List definitionsSet = Collections.singletonList(1L);

        // When
        List result = EndpointsUsageAggregation.BY_RESPONSE_TIME.getValuesFromEntryForMs(Collections.singletonList(10L), definitionsSet);

        // Then
        assertThat(result.isEmpty()).isTrue();
    }
}