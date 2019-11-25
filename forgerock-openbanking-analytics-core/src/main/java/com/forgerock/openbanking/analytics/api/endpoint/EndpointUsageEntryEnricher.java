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
