/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
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