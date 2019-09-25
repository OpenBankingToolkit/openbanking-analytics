/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.api.consent;

import com.forgerock.openbanking.analytics.charts.Donut;
import com.forgerock.openbanking.analytics.model.IntentType;
import com.forgerock.openbanking.analytics.model.entries.ConsentStatusEntry;
import com.forgerock.openbanking.analytics.model.openbanking.OBGroupName;
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
@PreAuthorize("hasAnyAuthority('GROUP_FORGEROCK', 'GROUP_OB', 'ROLE_FORGEROCK_INTERNAL_APP')")
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
