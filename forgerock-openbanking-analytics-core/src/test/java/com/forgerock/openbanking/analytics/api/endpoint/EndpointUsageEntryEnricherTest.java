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
import com.forgerock.openbanking.analytics.model.entries.TppEntry;
import com.forgerock.openbanking.analytics.repository.TppEntryRepository;
import com.forgerock.openbanking.model.UserContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EndpointUsageEntryEnricherTest {

    @Mock
    private TppEntryRepository tppEntryRepository;
    @InjectMocks
    private EndpointUsageEntryEnricher enricher;

    @Test
    public void shouldEnrichIODCEndpoints() {
        // Given
        EndpointUsageEntry entry = EndpointUsageEntry.builder().userType(UserContext.UserType.OIDC_CLIENT).identityId("clientId").build();
        TppEntry tppEntry = TppEntry.builder()
                .name("tpp")
                .logoUri("logoUir")
                .build();
        given(tppEntryRepository.findByOidcClientId(entry.getIdentityId())).willReturn(Optional.of(tppEntry));

        // When
        EndpointUsageEntry enriched = enricher.enrich(entry);

        // Then
        assertThat(enriched.getTppEntry()).isEqualTo(tppEntry);
    }

    @Test
    public void shouldNotEnrichNonIODCEndpoints() {
        // Given
        EndpointUsageEntry entry = EndpointUsageEntry.builder().userType(UserContext.UserType.ANONYMOUS).identityId("clientId").build();

        // When
        enricher.enrich(entry);

        // Then
        verify(tppEntryRepository, never()).findById(entry.getIdentityId());
    }
}