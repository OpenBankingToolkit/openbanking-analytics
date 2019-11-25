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
package com.forgerock.openbanking.analytics.api.token;

import com.forgerock.openbanking.analytics.charts.Counter;
import com.forgerock.openbanking.analytics.model.entries.TokenUsage;
import com.forgerock.openbanking.analytics.models.TokenUsageEntry;
import com.forgerock.openbanking.analytics.repository.TokenUsageEntryRepository;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class TokenUsageKpiAPIControllerTest {

    @Mock
    private TokenUsageEntryRepository tokenUsageEntryRepository;
    @InjectMocks
    private TokenUsageKpiAPIController tokenUsageKpiAPIController;

    @Test
    public void countGroupingByTokenType() {
        // Given
        given(tokenUsageEntryRepository.findByDayBetween(any(), any())).willReturn(Arrays.asList(
                new TokenUsageEntry(TokenUsage.ACCESS_TOKEN, DateTime.now().minusDays(1), 3L),
                new TokenUsageEntry(TokenUsage.ACCESS_TOKEN, DateTime.now(), 4L),
                new TokenUsageEntry(TokenUsage.ACCESS_TOKEN, DateTime.now().plusDays(1), 3L)));

        // When
        ResponseEntity<Counter> tokenUsage = tokenUsageKpiAPIController.getTokenUsage(TokenUsage.ACCESS_TOKEN, DateTime.now(), DateTime.now());

        // Then
        assertThat(tokenUsage.getBody()).isEqualTo(
                Counter.builder()
                        .type(TokenUsage.ACCESS_TOKEN.name())
                        .counter(10L)
                        .build());
    }
}