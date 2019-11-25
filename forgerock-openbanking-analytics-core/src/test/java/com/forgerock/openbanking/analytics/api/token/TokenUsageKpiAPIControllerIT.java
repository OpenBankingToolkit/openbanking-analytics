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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forgerock.openbanking.analytics.charts.Counter;
import com.forgerock.openbanking.analytics.model.entries.TokenUsage;
import com.forgerock.openbanking.analytics.models.TokenUsageEntry;
import com.forgerock.openbanking.analytics.repository.TokenUsageEntryRepository;
import kong.unirest.HttpResponse;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.Unirest;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TokenUsageKpiAPIControllerIT {

    @LocalServerPort
    private int port;

    @Value("${session.issuer-id}")
    private String issuerId;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TokenUsageEntryRepository tokenUsageEntryRepository;


    @Before
    public void setUp() {
        Unirest.config()
                .setObjectMapper(new JacksonObjectMapper(objectMapper)).verifySsl(false);
    }

    @After
    public void tearDown() {
        tokenUsageEntryRepository.deleteAll();
    }

    @Test
    public void createsTokenUsageEntry() throws Exception {
        // Given

        // When
        DateTime today = DateTime.now()
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        HttpResponse response = Unirest.post("http://metrics-services:" + port + "/api/kpi/token-usage?type=ACCESS_TOKEN" )
                .header("Content-Type", "application/json")
                .body(Collections.singletonList(TokenUsage.ACCESS_TOKEN))
                .asEmpty();

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(tokenUsageEntryRepository.findAll()).containsOnly(new TokenUsageEntry(TokenUsage.ACCESS_TOKEN, today, 1L));
    }

    @Test
    public void createsMultipleTokenUsageEntry() throws Exception {
        // Given

        // When
        DateTime today = DateTime.now()
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        HttpResponse response = Unirest.post("http://metrics-services:" + port + "/api/kpi/token-usage")
                .header("Content-Type", "application/json")
                .body(Arrays.asList(TokenUsage.ACCESS_TOKEN, TokenUsage.ID_TOKEN))
                .asEmpty();

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(tokenUsageEntryRepository.findAll()).contains(
                new TokenUsageEntry(TokenUsage.ACCESS_TOKEN, today, 1L),
                new TokenUsageEntry(TokenUsage.ID_TOKEN, today, 1L)
        );
    }

    @Test
    public void incrementTokenUsageEntry() throws Exception {
        // Given
        DateTime today = DateTime.now()
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        tokenUsageEntryRepository.save(new TokenUsageEntry(TokenUsage.ACCESS_TOKEN, today, 1L));

        // When


        HttpResponse response = Unirest.post("http://metrics-services:" + port + "/api/kpi/token-usage")
                .header("Content-Type", "application/json")
                .body(Collections.singletonList(TokenUsage.ACCESS_TOKEN))
                .asEmpty();

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(tokenUsageEntryRepository.findAll()).containsOnly(new TokenUsageEntry(TokenUsage.ACCESS_TOKEN, today, 2L));
    }

    @Test
    public void getTokenUsageEntry() throws Exception {
        // Given
        tokenUsageEntryRepository.save(new TokenUsageEntry(TokenUsage.ACCESS_TOKEN, DateTime.now(), 1L));

        // When
        HttpResponse<Counter> response = Unirest.get("http://metrics-services:" + port + "/api/kpi/token-usage")
                .queryString("fromDate", DateTime.now().minusDays(1))
                .queryString("toDate", DateTime.now().plusDays(1))
                .queryString("type", "ACCESS_TOKEN")
                .header("Content-Type", "application/json")
                .asObject(Counter.class);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getBody()).isEqualTo(Counter.builder()
                .type("ACCESS_TOKEN")
                .counter(1L)
                .build());
    }
}