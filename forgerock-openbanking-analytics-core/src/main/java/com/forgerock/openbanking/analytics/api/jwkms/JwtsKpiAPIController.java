/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.api.jwkms;

import com.forgerock.openbanking.analytics.charts.Donut;
import com.forgerock.openbanking.analytics.model.entries.JwtsGenerationEntry;
import com.forgerock.openbanking.analytics.model.entries.JwtsValidationEntry;
import com.forgerock.openbanking.analytics.model.kpi.JwtsGenerationKPI;
import com.forgerock.openbanking.analytics.model.kpi.JwtsValidationKPI;
import com.forgerock.openbanking.analytics.repository.JwtsGenerationEntriesRepository;
import com.forgerock.openbanking.analytics.repository.JwtsValidationEntriesRepository;
import com.google.common.collect.ImmutableSet;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.byExample;

@RestController
@RequestMapping("/api/kpi/jwts")

@Slf4j
public class JwtsKpiAPIController {

    @Autowired
    private JwtsGenerationEntriesRepository jwtsGenerationEntriesRepository;
    @Autowired
    private JwtsValidationEntriesRepository jwtsValidationEntriesRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping(value = "/jwts-generation/add-entry", method = RequestMethod.POST)
    public ResponseEntity jwtGenerationAddEntry(@RequestBody JwtsGenerationEntry entry) {
        return jwtGenerationAddEntries(ImmutableSet.of(entry));
    }

    @RequestMapping(value = "/jwts-generation/add-entries", method = RequestMethod.POST)
    public ResponseEntity jwtGenerationAddEntries(@RequestBody Set<JwtsGenerationEntry> entries) {
        Map<JwtsGenerationEntry, Long> groupedCount = entries.stream()
                .map(e -> {
                    e.setDate(e.getDate().withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).withZone(DateTimeZone.UTC));
                    return e;
                })
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        log.debug("Adding or creating JwtsGenerationEntry count={}", groupedCount.keySet().size());
        groupedCount.forEach((k, v) -> upsertJwtsGenerationEntryByExample(k, v));
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/jwts-validation/add-entry", method = RequestMethod.POST)
    public ResponseEntity jwtValidationAddEntry(@RequestBody JwtsValidationEntry entry) {
        return jwtValidationAddEntries(ImmutableSet.of(entry));
    }

    @RequestMapping(value = "/jwts-validation/add-entries", method = RequestMethod.POST)
    public ResponseEntity jwtValidationAddEntries(@RequestBody Set<JwtsValidationEntry> entries) {
        Map<JwtsValidationEntry, Long> groupedCount = entries.stream()
                .map(e -> {
                    e.setDate(e.getDate().withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).withZone(DateTimeZone.UTC));
                    return e;
                })
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        log.debug("Adding or creating JwtsValidationEntry count={}", groupedCount.keySet().size());
        groupedCount.forEach((k, v) -> upsertJwtsValidationEntryByExample(k, v));
        return ResponseEntity.ok().build();
    }

    private boolean upsertJwtsGenerationEntryByExample(JwtsGenerationEntry entry, Long count) {
        UpdateResult upsert = mongoTemplate.upsert(new Query(byExample(entry)),
                new Update().inc("count", count), JwtsGenerationEntry.class);
        return upsert.getModifiedCount() > 0;
    }

    private boolean upsertJwtsValidationEntryByExample(JwtsValidationEntry entry, Long count) {
        UpdateResult upsert = mongoTemplate.upsert(new Query(byExample(entry)),
                new Update().inc("count", count), JwtsValidationEntry.class);
        return upsert.getModifiedCount() > 0;
    }

    /*
     * KPI getters
     */

    @RequestMapping(value = "/jwts-generation/", method = RequestMethod.GET)
    public ResponseEntity<Donut> getJwtsGenerationKPI(
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime

    ) {
        log.debug("Get JWT generations KPIs from {} to {}", fromDateTime, toDateTime);
        JwtsGenerationKPI jwtsGenerationKPI = new JwtsGenerationKPI();
        jwtsGenerationEntriesRepository.findByDateBetween(fromDateTime, toDateTime).forEach(
                jwtsGenerationEntry ->  jwtsGenerationKPI.increment(jwtsGenerationEntry.getJwtType(), jwtsGenerationEntry.getCount()));
        return ResponseEntity.ok(Donut.convertCountersToDonus(jwtsGenerationKPI.getDataset()));
    }

    @RequestMapping(value = "/jwts-validation/", method = RequestMethod.GET)
    public ResponseEntity<Donut> getJwtsValidationKPI(
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime
    ) {
        log.debug("Get JWT validations KPIs from {} to {}", fromDateTime, toDateTime);
        JwtsValidationKPI jwtsValidationKPI = new JwtsValidationKPI();
        jwtsValidationEntriesRepository.findByDateBetween(fromDateTime, toDateTime).forEach(
                jwtsValidationEntry ->  jwtsValidationKPI.increment(jwtsValidationEntry.getWasValid(), jwtsValidationEntry.getCount()));
        return ResponseEntity.ok(Donut.convertCountersToDonus(jwtsValidationKPI.getDataset()));
    }
}
