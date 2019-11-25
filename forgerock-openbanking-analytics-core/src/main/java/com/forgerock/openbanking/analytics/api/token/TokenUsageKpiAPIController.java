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
package com.forgerock.openbanking.analytics.api.token;

import com.forgerock.openbanking.analytics.charts.Counter;
import com.forgerock.openbanking.analytics.model.entries.TokenUsage;
import com.forgerock.openbanking.analytics.models.TokenUsageEntry;
import com.forgerock.openbanking.analytics.repository.TokenUsageEntryRepository;
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

@RequestMapping("/api/kpi/token-usage")
@RestController
public class TokenUsageKpiAPIController {

    private MongoTemplate mongoTemplate;
    private TokenUsageEntryRepository tokenUsageEntryRepository;

    public TokenUsageKpiAPIController(MongoTemplate mongoTemplate, TokenUsageEntryRepository tokenUsageEntryRepository) {
        this.mongoTemplate = mongoTemplate;
        this.tokenUsageEntryRepository = tokenUsageEntryRepository;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity incrementTokenUsage(@RequestBody Collection<TokenUsage> tokenUsages) {
        tokenUsages.forEach(t -> {
            log.debug("Incrementing token usage type={}", t);
            TokenUsageEntry example = TokenUsageEntry.today(t);
            mongoTemplate.upsert(new Query(byExample(example)),
                    new Update().inc("count", 1), TokenUsageEntry.class);
        });
        return ResponseEntity.accepted().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Counter> getTokenUsage(
            @RequestParam(value = "type") TokenUsage tokenUsage,
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime
    ) {
        log.debug("Getting token usage counter for type {} from {} to {}", tokenUsage, fromDateTime, toDateTime);
        Map<TokenUsage, Long> tokenUsageCounters = tokenUsageEntryRepository.findByDayBetween(fromDateTime.minusMinutes(1), toDateTime)
                .stream()
                .collect(Collectors.groupingBy(TokenUsageEntry::getTokenUsage,
                        Collectors.summingLong(TokenUsageEntry::getCount)));

        for (TokenUsage value: TokenUsage.values()) {
            if (!tokenUsageCounters.containsKey(value)) {
                tokenUsageCounters.put(value, 0l);
            }
        }

        return ResponseEntity.ok(
                Counter.builder()
                        .type(tokenUsage.name())
                        .counter(tokenUsageCounters.get(tokenUsage).longValue())
                        .build()
        );
    }
}
