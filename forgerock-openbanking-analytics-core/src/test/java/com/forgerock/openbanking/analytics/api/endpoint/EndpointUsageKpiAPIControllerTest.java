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
package com.forgerock.openbanking.analytics.api.endpoint;

import com.forgerock.openbanking.analytics.model.entries.EndpointUsageEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EndpointUsageKpiAPIControllerTest {

    @Mock
    private EndpointUsageAggregator endpointUsageAggregator;
    @Mock
    private EndpointUsageEntryEnricher enricher;
    @InjectMocks
    private EndpointUsageKpiAPIController endpointUsageKpiAPIController;

    @Test
    public void shouldEnrichEndpointUsageEntryOnAdd() {
        // Given
        EndpointUsageEntry entry = EndpointUsageEntry.builder().identityId("clientId").build();
        given(enricher.enrich(entry)).willReturn(entry);

        // When
        endpointUsageKpiAPIController.addEntry(entry);

        // Then
        verify(enricher).enrich(entry);
    }

    @Test
    public void shouldAggregateEndpointUsageEntryOnAdd() {
        // Given
        EndpointUsageEntry entry = EndpointUsageEntry.builder().identityId("clientId").build();
        given(enricher.enrich(entry)).willReturn(entry);

        // When
        endpointUsageKpiAPIController.addEntry(entry);

        // Then
        verify(endpointUsageAggregator).aggregate(Collections.singletonList(entry));
    }
}