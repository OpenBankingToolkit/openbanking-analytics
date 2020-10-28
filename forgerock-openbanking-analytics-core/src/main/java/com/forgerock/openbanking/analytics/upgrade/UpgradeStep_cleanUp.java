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
package com.forgerock.openbanking.analytics.upgrade;

import com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate;
import com.forgerock.openbanking.analytics.model.entries.TppEntry;
import com.forgerock.openbanking.analytics.repository.*;
import com.forgerock.openbanking.repositories.TppRepository;
import com.forgerock.openbanking.upgrade.exceptions.UpgradeException;
import com.forgerock.openbanking.upgrade.model.UpgradeStep;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
//@UpgradeMeta(version = "1.1.23")
@Slf4j
public class UpgradeStep_cleanUp implements UpgradeStep {
    
    @Autowired
    private EndpointUsageAggregateRepository endpointUsageAggregateRepository;
    @Autowired
    private EndpointUsageEntryRepository endpointUsageEntryRepository;
    @Autowired
    private CallBackCounterEntryRepository callBackCounterEntryRepository;
    @Autowired
    private ConsentStatusEntryRepository consentStatusEntryRepository;
    @Autowired
    private DirectoryCounterEntryRepository directoryCounterEntryRepository;
    @Autowired
    private JwtsGenerationEntriesRepository jwtsGenerationEntriesRepository;
    @Autowired
    private JwtsValidationEntriesRepository jwtsValidationEntriesRepository;
    @Autowired
    private PsuCounterEntryRepository psuCounterEntryRepository;
    @Autowired
    private SessionCounterEntryRepository sessionCounterEntryRepository;
    @Autowired
    private TokenUsageEntryRepository tokenUsageEntryRepository;
    @Autowired
    private TppEntryRepository tppEntryRepository;
    @Autowired
    private TppRepository tppRepository;

    private static final String COMPANIES_CSV = "companies.csv";
    private static final String NAMES_CSV = "names.csv";

    @Override
    public boolean upgrade() throws UpgradeException {
        log.debug("Start cleaning up data");
        try {
            //deleteOldMetrics();

            anonymiseData();
        } catch (Exception e) {
            throw new UpgradeException("Could not clean up data", e);
        } finally {
            SecurityContextHolder.clearContext();
        }
        return true;
    }

    private void deleteOldMetrics() {
        DateTime from = DateTime.now().minusYears(3);
        DateTime to = DateTime.now().minusMonths(3);

        endpointUsageAggregateRepository.deleteByDateIsNull();

        endpointUsageAggregateRepository.deleteByDateBetween(from, to);
        callBackCounterEntryRepository.deleteByDateBetween(from, to);
        consentStatusEntryRepository.deleteByDateBetween(from, to);
        directoryCounterEntryRepository.deleteByDayBetween(from, to);
        jwtsGenerationEntriesRepository.deleteByDateBetween(from, to);
        jwtsValidationEntriesRepository.deleteByDateBetween(from, to);
        psuCounterEntryRepository.deleteByDayBetween(from, to);
        sessionCounterEntryRepository.deleteByDayBetween(from, to);
        tokenUsageEntryRepository.deleteByDayBetween(from, to);

        endpointUsageEntryRepository.deleteAll();
        tppRepository.deleteAll();
    }

    private List<String> companies;
    private List<String> names;

    private void anonymiseData() throws IOException {
        companies = loadCSV(new ClassPathResource(COMPANIES_CSV));
        names = loadCSV(new ClassPathResource(NAMES_CSV));

        Map<String, String> tppNames = new HashMap<>();
        Map<String, String> tppOrgs = new HashMap<>();

        List<TppEntry> tpps = tppEntryRepository.findAll().stream().map(tpp -> {

            int k = ThreadLocalRandom.current().nextInt(names.size());
            String name;

            if (tppNames.containsKey(tpp.getName())) {
                name = tppNames.get(tpp.getName());
            } else {
                name = k + " - " + names.get(ThreadLocalRandom.current().nextInt(names.size()));
                tppNames.put(tpp.getName(), name);
            }

            String company;
            if (tppOrgs.containsKey(tpp.getOrganisationName())) {
                company = tppOrgs.get(tpp.getOrganisationName());
            } else {
                company = k + " - " + companies.get(ThreadLocalRandom.current().nextInt(companies.size()));
                tppOrgs.put(tpp.getOrganisationName(), name);
            }

            int logoId = Math.abs(name.hashCode() + company.hashCode()) % 10;
            tpp.setName(name);
            tpp.setOrganisationName(company);
            tpp.setLogoUri("https://randomuser.me/api/portraits/lego/" + logoId + ".jpg");
            return tpp;
        }).collect(Collectors.toList());
        tppEntryRepository.saveAll(tpps);

        Map<String, TppEntry> tppByOIDCClientId = tpps.stream().collect(Collectors.toMap(tpp -> tpp.getOidcClientId(),
                tpp -> tpp));

        DateTime current = DateTime.now();
        DateTime stop = DateTime.now().minusMonths(3);

        while (current.isAfter(stop)) {
            log.debug("From {} to {}", current.minusDays(1), current);

            List<EndpointUsageAggregate> entries = endpointUsageAggregateRepository.findByDateBetween(current.minusDays(1), current).collect(Collectors.toList());
            endpointUsageAggregateRepository.deleteByDateBetween(current.minusDays(1), current);

            entries.stream().map(entry -> {
                if (entry.getTppEntry() != null && entry.getTppEntry().getOidcClientId() != null) {
                    TppEntry tppEntry = tppByOIDCClientId.get(entry.getTppEntry().getOidcClientId());
                    if (tppEntry != null) {
                        entry.setTppEntry(tppEntry);
                    } else {
                        log.warn("OIDC client {} not found!", entry.getTppEntry().getOidcClientId());
                        int k = ThreadLocalRandom.current().nextInt(names.size());
                        String company = k + " - " + companies.get(ThreadLocalRandom.current().nextInt(companies.size()));
                        String name = k + " - " + names.get(ThreadLocalRandom.current().nextInt(names.size()));

                        int logoId = ThreadLocalRandom.current().nextInt(names.size()) % 10;
                        entry.getTppEntry().setName(name);
                        entry.getTppEntry().setOrganisationName(company);
                        entry.getTppEntry().setLogoUri("https://randomuser.me/api/portraits/lego/" + logoId + ".jpg");
                    }
                }
                entry.setGeoIP(null);
                if (entry.getResponseTimesByKbHistory().size() > 200) {
                    entry.setResponseTimesByKbHistory(entry.getResponseTimesByKbHistory().subList(0, 200));
                }
                if (entry.getResponseTimesHistory().size() > 200) {
                    entry.setResponseTimesHistory(entry.getResponseTimesHistory().subList(0, 200));
                }
                return entry;
            }).collect(Collectors.toList());
            endpointUsageAggregateRepository.saveAll(entries);
            current = current.minusDays(1);
        }

        log.debug("Upgrade done!");
    }

    private List<String> loadCSV(Resource resource) throws IOException {

        log.debug("Load resource {}", resource);
        List<String> content = new ArrayList<>();

        String line;
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                while ((line = br.readLine()) != null) {
                    content.add(line);
                }
            } catch (IOException e) {
                log.error("Can't load resource '{}'", resource, e);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return content;
    }
}
