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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class EndpointUsageEntryEnricher {

    private TppEntryRepository tppEntryRepository;

    public EndpointUsageEntryEnricher(TppEntryRepository tppEntryRepository) throws IOException {
        this.tppEntryRepository = tppEntryRepository;
    }

    EndpointUsageEntry enrich(EndpointUsageEntry entry) {

        if (!UserContext.UserType.OIDC_CLIENT.equals(entry.getUserType())) {
            return entry;
        }
        log.debug("Enriching EndpointUsageEntry {}", entry);
        Optional<TppEntry> tpp = tppEntryRepository.findByOidcClientId(entry.getIdentityId());
        tpp.ifPresent(t -> {
            entry.setTppEntry(tpp.get());
        });

        return entry;
    }
}
