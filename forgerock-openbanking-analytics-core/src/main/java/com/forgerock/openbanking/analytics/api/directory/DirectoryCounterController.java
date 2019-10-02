/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.api.directory;

import com.forgerock.openbanking.analytics.charts.Counter;
import com.forgerock.openbanking.analytics.model.entries.DirectoryCounterEntry;
import com.forgerock.openbanking.analytics.model.entries.DirectoryCounterType;
import com.forgerock.openbanking.analytics.repository.DirectoryCounterEntryRepository;
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

@RequestMapping("/api/kpi/directory")
@RestController
public class DirectoryCounterController {

    private MongoTemplate mongoTemplate;
    private DirectoryCounterEntryRepository directoryCounterEntryRepository;

    public DirectoryCounterController(MongoTemplate mongoTemplate, DirectoryCounterEntryRepository directoryCounterEntryRepository) {
        this.mongoTemplate = mongoTemplate;
        this.directoryCounterEntryRepository = directoryCounterEntryRepository;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity incrementCounter(@RequestBody Collection<DirectoryCounterType> directoryCounterTypes) {
        directoryCounterTypes.forEach(t -> {
            log.debug("Incrementing directory counter : {}", t);
            DirectoryCounterEntry directoryCounterEntry = DirectoryCounterEntry.today(t);
            mongoTemplate.upsert(new Query(byExample(directoryCounterEntry)),
                    new Update().inc("count", 1), DirectoryCounterEntry.class);
        });
        return ResponseEntity.accepted().build();
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public ResponseEntity<Counter> getDirectoryCounter(
            @RequestParam(value = "type") DirectoryCounterType type,

            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime
    ) {
        log.debug("Getting directory counter type from {} to {}", fromDateTime, toDateTime);

        Map<DirectoryCounterType, Long> counters = directoryCounterEntryRepository.findByDayBetween(fromDateTime.minusMinutes(1), toDateTime)
                .stream()
                .collect(Collectors.groupingBy(DirectoryCounterEntry::getDirectoryCounterType,
                        Collectors.summingLong(DirectoryCounterEntry::getCount)));

        return ResponseEntity.ok(Counter.builder()
                .type(type.name())
                .counter(counters.containsKey(type) ? counters.get(type): 0l)
                .build()
               );
    }


}
