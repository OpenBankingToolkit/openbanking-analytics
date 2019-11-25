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
package com.forgerock.openbanking.analytics.api.endpoint;

import com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageEntry;
import com.forgerock.openbanking.analytics.repository.EndpointUsageAggregateRepository;
import com.mongodb.client.result.UpdateResult;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate.newAggregate;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.data.mongodb.core.query.Criteria.byExample;

@RunWith(MockitoJUnitRunner.class)
public class EndpointUsageAggregatorTest {

    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private EndpointUsageAggregateRepository endpointUsageAggregateRepository;
    @InjectMocks
    private EndpointUsageAggregator endpointUsageAggregator;

    @Test
    public void shouldTryToIncrementNewAggregates() {
        // Given
        EndpointUsageEntry entry = mockUpsertNoUpdate(EndpointUsageEntry.builder()
                .date(DateTime.now())
                .responseTime(100l)
                .responseTimeByKb(100l)
                .build());

        // When
        endpointUsageAggregator.aggregate(Collections.singletonList(entry));

        // Then
        verify(mongoTemplate).upsert(new Query(byExample(newAggregate(entry).example())),
                new Update()
                        .inc("count", 1L)
                        .inc("responseTimesSum", 100L)
                        .push("responseTimesHistory").each(entry.getResponseTime())
                        .inc("responseTimesByKbSum", 100L)
                        .push("responseTimesByKbHistory").each(entry.getResponseTime())

                , EndpointUsageAggregate.class);
    }

    @Test
    public void shouldIncrementExistingAggregates() {
        // Given
        EndpointUsageEntry entry = mockUpsert(EndpointUsageEntry.builder()
                .date(DateTime.now())
                .responseTime(100l)
                .responseTimeByKb(100l)
                .build(), 1L, 100, Arrays.asList(100l));

        // When
        endpointUsageAggregator.aggregate(Collections.singletonList(entry));

        // Then
        verify(mongoTemplate).upsert(new Query(byExample(newAggregate(entry).example())),
                new Update()
                        .inc("count", 1L)
                        .inc("responseTimesSum", 100L)
                        .push("responseTimesHistory").each(entry.getResponseTime())
                        .inc("responseTimesByKbSum", 100L)
                        .push("responseTimesByKbHistory").each(entry.getResponseTime())

                , EndpointUsageAggregate.class);
    }

    @Test
    public void shouldGroupAndIncrementExistingAggregates() {
        // Given
        EndpointUsageEntry entry = EndpointUsageEntry.builder()
                .date(DateTime.now())
                .endpoint("accounts")
                .responseTime(100l)
                .responseTimeByKb(100l)
                .build();
        EndpointUsageEntry entry2 = EndpointUsageEntry.builder()
                .date(DateTime.now())
                .endpoint("payments")
                .responseTime(100l)
                .responseTimeByKb(100l)
                .build();
        mockUpsert(entry, 2L, 200, Arrays.asList(100l, 100l));
        mockUpsert(entry2, 1L, 100, Arrays.asList(100l));

        // When
        endpointUsageAggregator.aggregate(Arrays.asList(entry, entry, entry2));

        // Then
        verify(mongoTemplate).upsert(new Query(byExample(newAggregate(entry).example())),
                new Update()
                        .inc("count", 2L)
                        .inc("responseTimesSum", 200L)
                        .push("responseTimesHistory").each(Arrays.asList(100l, 100l))
                        .inc("responseTimesByKbSum", 200L)
                        .push("responseTimesByKbHistory").each(Arrays.asList(100l, 100l))
                , EndpointUsageAggregate.class);
        verify(mongoTemplate).upsert(new Query(byExample(newAggregate(entry2).example())),
                new Update()
                        .inc("count", 1L)
                        .inc("responseTimesSum", 100L)
                        .push("responseTimesHistory").each(Arrays.asList(100l))
                        .inc("responseTimesByKbSum", 100L)
                        .push("responseTimesByKbHistory").each(Arrays.asList(100l))
                , EndpointUsageAggregate.class);
    }

    private EndpointUsageEntry mockUpsertNoUpdate(EndpointUsageEntry entry) {
        UpdateResult notModifiedUpdateResult = mock(UpdateResult.class);
        given(notModifiedUpdateResult.getModifiedCount()).willReturn(0L);
        given(mongoTemplate.upsert(new Query(byExample(newAggregate(entry).example())),
                new Update()
                        .inc("count", 1L)
                        .inc("responseTimesSum", entry.getResponseTime())
                        .push("responseTimesHistory").each(entry.getResponseTime())
                        .inc("responseTimesByKbSum", entry.getResponseTime())
                        .push("responseTimesByKbHistory").each(entry.getResponseTime()),
                EndpointUsageAggregate.class)).willReturn(notModifiedUpdateResult);
        return entry;
    }

    private EndpointUsageEntry mockUpsert(EndpointUsageEntry entry, long increment, long responseTimeSum, List<Long> responseTimes) {
        UpdateResult notModifiedUpdateResult = mock(UpdateResult.class);
        given(notModifiedUpdateResult.getModifiedCount()).willReturn(1L);
        given(mongoTemplate.upsert(new Query(byExample(newAggregate(entry).example())),
                new Update()
                        .inc("count", increment)
                        .inc("responseTimesSum", responseTimeSum)
                        .push("responseTimesHistory").each(responseTimes)
                        .inc("responseTimesByKbSum", responseTimeSum)
                        .push("responseTimesByKbHistory").each(responseTimes)
                , EndpointUsageAggregate.class)).willReturn(notModifiedUpdateResult);
        return entry;
    }
}