/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.api.psu;

import com.forgerock.openbanking.analytics.charts.Counter;
import com.forgerock.openbanking.analytics.model.entries.PsuCounterEntry;
import com.forgerock.openbanking.analytics.repository.PsuCounterEntryRepository;
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

import static org.springframework.data.mongodb.core.query.Criteria.byExample;


@RestController
@PreAuthorize("hasAnyAuthority('GROUP_FORGEROCK', 'GROUP_OB', 'ROLE_FORGEROCK_INTERNAL_APP')")
@RequestMapping("/api/kpi/psu")
@Slf4j
public class PSUKpiAPIController {

    @Autowired
    private PsuCounterEntryRepository psuCounterEntryRepository;
    @Autowired
    private MongoTemplate mongoTemplate;


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity addPsuCounterEntry(@RequestBody PsuCounterEntry psuCounterEntry) {
        psuCounterEntry.setDay(psuCounterEntry.getDay()
                .withZone(DateTimeZone.UTC)
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0)
        );
        PsuCounterEntry psuCounterEntryExample = PsuCounterEntry.today(psuCounterEntry);
        mongoTemplate.upsert(new Query(byExample(psuCounterEntryExample)),
                new Update().inc("count", psuCounterEntry.getCount()), PsuCounterEntry.class);
        return ResponseEntity.accepted().build();
    }

    @RequestMapping(value = "/counter", method = RequestMethod.GET)
    public ResponseEntity<Counter> count(
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime
    ) {
        log.debug("Get PSU counter from {} to {}", fromDateTime, toDateTime);

       Long count = psuCounterEntryRepository.findByDayBetween(fromDateTime.minusMinutes(1), toDateTime)
                .stream()
                .mapToLong(e -> e.getCount()).sum();

        return ResponseEntity.ok(Counter.builder()
                .counter(count)
                .type("All")
                .build());
    }
}
