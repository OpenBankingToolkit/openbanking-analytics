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
@PreAuthorize("hasAnyAuthority('GROUP_ANALYTICS')")
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
