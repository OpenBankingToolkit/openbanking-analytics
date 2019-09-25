/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
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