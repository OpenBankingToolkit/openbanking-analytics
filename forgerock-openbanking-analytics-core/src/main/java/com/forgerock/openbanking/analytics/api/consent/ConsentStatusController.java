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
package com.forgerock.openbanking.analytics.api.consent;

import com.forgerock.openbanking.analytics.charts.Donut;
import com.forgerock.openbanking.analytics.model.IntentType;
import com.forgerock.openbanking.analytics.model.entries.ConsentStatusEntry;
import com.forgerock.openbanking.api.annotations.OBGroupName;
import com.forgerock.openbanking.analytics.models.ConsentActivities;
import com.forgerock.openbanking.analytics.models.ConsentTypeCounter;
import com.forgerock.openbanking.analytics.repository.ConsentStatusEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j

@RequestMapping("/api/kpi/consent")
@RestController
public class ConsentStatusController {

    private ConsentStatusEntryRepository consentStatusEntryRepository;

    public ConsentStatusController(ConsentStatusEntryRepository consentStatusEntryRepository) {
        this.consentStatusEntryRepository = consentStatusEntryRepository;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity addConsentStatusEntry(@RequestBody ConsentStatusEntry consentStatusEntry) {
        log.debug("Add call back entries in database: {}", consentStatusEntry);
        consentStatusEntryRepository.save(consentStatusEntry);
        return ResponseEntity.accepted().build();
    }

    @RequestMapping(value = "/activities", method = RequestMethod.GET)
    public ResponseEntity<Donut> getConsentsActivities(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime,
            @RequestParam(value = "consentType") IntentType consentType
    ) {
        log.debug("Getting consent activities");

        List<ConsentActivities> counters = consentStatusEntryRepository.countActiveConsentStatusBetweenDate(fromDateTime, toDateTime, consentType);
        return ResponseEntity.ok(Donut.convertCountersToDonus(counters.stream().collect(Collectors.toMap(ConsentActivities::getConsentStatus,
                ConsentActivities::getTotal))));
    }

    @RequestMapping(value = "/type", method = RequestMethod.GET)
    public ResponseEntity<Donut> getConsentType(
            @RequestParam(value = "obGroupName", required = false) OBGroupName obGroupName,
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime
    ) {
        log.debug("Getting consent type");

        List<ConsentTypeCounter> counters = consentStatusEntryRepository.countConsentsCreatedBetweenDate(obGroupName, fromDateTime, toDateTime);
        return ResponseEntity.ok(Donut.convertCountersToDonus(counters.stream().collect(Collectors.toMap(ConsentTypeCounter::getConsentType,
                ConsentTypeCounter::getTotal))));
    }

}
