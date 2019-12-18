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

import com.forgerock.openbanking.analytics.model.entries.TppEntry;
import com.forgerock.openbanking.analytics.repository.TppEntryRepository;
import com.forgerock.openbanking.upgrade.exceptions.UpgradeException;
import com.forgerock.openbanking.upgrade.model.UpgradeMeta;
import com.forgerock.openbanking.upgrade.model.UpgradeStep;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@UpgradeMeta(version = "1.1.24")
@Slf4j
public class UpgradeStep_stat implements UpgradeStep {

    @Autowired
    private TppEntryRepository tppEntryRepository;

    @Override
    public boolean upgrade() throws UpgradeException {
        if (log.isDebugEnabled()) {
            log.debug("Start cleaning up data");
            try {
                analyseData();
            } catch (Exception e) {
                throw new UpgradeException("Could not clean up data", e);
            } finally {
                SecurityContextHolder.clearContext();
            }
        }
        return true;
    }

    private void analyseData() {
        Set<String> uniqOrgs = new HashSet<>();
        Set<String> uniqTpps = new HashSet<>();
        List<TppEntry> tpps = tppEntryRepository.findAll();
        for(TppEntry tppEntry: tpps) {
            uniqTpps.add(tppEntry.getName() + tppEntry.getOrganisationName());
            uniqOrgs.add(tppEntry.getOrganisationName());
        }
        log.debug("We got {} Tpps, {} uniques and {} orgs", tpps.size(), uniqTpps.size(), uniqOrgs.size());

        DateTime from = DateTime.now().minusMonths(3).minusDays(2);
        DateTime to = DateTime.now();

        Set<String> uniqOrgs3months = new HashSet<>();
        Set<String> uniqTpps3months = new HashSet<>();
        List<TppEntry> tpps3months = tppEntryRepository.findByCreatedBetween(from, to);
        for(TppEntry tppEntry: tpps3months) {
            uniqTpps3months.add(tppEntry.getName() + tppEntry.getOrganisationName());
            uniqOrgs3months.add(tppEntry.getOrganisationName());
        }
        log.debug("On the last 3 months tWe got {} Tpps, {} uniques and {} orgs", tpps3months.size(), uniqTpps3months.size(),
                uniqOrgs3months.size());
    }
}
