/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
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