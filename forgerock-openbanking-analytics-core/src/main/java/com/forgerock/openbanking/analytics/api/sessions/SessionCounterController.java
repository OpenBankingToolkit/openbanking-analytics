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
package com.forgerock.openbanking.analytics.api.sessions;

import com.forgerock.openbanking.analytics.charts.Donut;
import com.forgerock.openbanking.analytics.model.entries.SessionCounterEntry;
import com.forgerock.openbanking.analytics.model.entries.SessionCounterType;
import com.forgerock.openbanking.analytics.repository.SessionCounterEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.byExample;

@Slf4j
@PreAuthorize("hasAnyAuthority('GROUP_ANALYTICS')")
@RequestMapping("/api/kpi/session")
@RestController
public class SessionCounterController {

    private MongoTemplate mongoTemplate;
    private SessionCounterEntryRepository sessionCounterEntryRepository;

    public SessionCounterController(MongoTemplate mongoTemplate, SessionCounterEntryRepository sessionCounterEntryRepository) {
        this.mongoTemplate = mongoTemplate;
        this.sessionCounterEntryRepository = sessionCounterEntryRepository;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity incrementCounter(@RequestBody Collection<SessionCounterType> tokenUsages) {
        tokenUsages.forEach(t -> {
            log.debug("Incrementing token usage type={}", t);
            SessionCounterEntry example = SessionCounterEntry.today(t);
            mongoTemplate.upsert(new Query(byExample(example)),
                    new Update().inc("count", 1), SessionCounterEntry.class);
        });
        return ResponseEntity.accepted().build();
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public ResponseEntity<Donut> getSessionCounter(
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime
    ) {
        log.debug("Getting session counter from {} to {}", fromDateTime, toDateTime);

        Map<SessionCounterType, Long> counters = sessionCounterEntryRepository.findByDayBetween(fromDateTime.minusMinutes(1), toDateTime)
                .stream()
                .collect(Collectors.groupingBy(SessionCounterEntry::getSessionCounterType,
                        Collectors.summingLong(SessionCounterEntry::getCount)));

        return ResponseEntity.ok(Donut.convertCountersToDonus(counters));
    }
}
