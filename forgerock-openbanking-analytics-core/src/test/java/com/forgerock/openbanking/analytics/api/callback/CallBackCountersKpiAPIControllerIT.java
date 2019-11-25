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
package com.forgerock.openbanking.analytics.api.callback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forgerock.openbanking.analytics.charts.Donut;
import com.forgerock.openbanking.analytics.model.entries.callback.CallBackCounterEntry;
import com.forgerock.openbanking.analytics.model.entries.callback.CallBackResponseStatus;
import com.forgerock.openbanking.analytics.repository.CallBackCounterEntryRepository;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CallBackCountersKpiAPIControllerIT {

    @LocalServerPort
    private int port;

    @Value("${session.issuer-id}")
    private String issuerId;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CallBackCounterEntryRepository callBackCounterEntryRepository;

    @Before
    public void setUp() {
        Unirest.config().setObjectMapper(new JacksonObjectMapper(objectMapper)).verifySsl(false);
    }

    @After
    public void tearDown() {
        callBackCounterEntryRepository.deleteAll();
    }

    @Test
    public void getTokenUsageEntry() throws Exception {
        // Given

        //Success entries
        callBackCounterEntryRepository.save(CallBackCounterEntry.builder()
                .callBackResponseStatus(CallBackResponseStatus.SUCCESS)
                .redirectUri("https://google.com")
                .tppId("totoID")
                .date(DateTime.now())
                .build());
        callBackCounterEntryRepository.save(CallBackCounterEntry.builder()
                .callBackResponseStatus(CallBackResponseStatus.SUCCESS)
                .redirectUri("https://google.com")
                .tppId("totoID")
                .date(DateTime.now())
                .build());
        callBackCounterEntryRepository.save(CallBackCounterEntry.builder()
                .callBackResponseStatus(CallBackResponseStatus.SUCCESS)
                .redirectUri("https://google.com")
                .tppId("totoID")
                .date(DateTime.now())
                .build());

        //Failure entries
        callBackCounterEntryRepository.save(CallBackCounterEntry.builder()
                .callBackResponseStatus(CallBackResponseStatus.FAILURE)
                .redirectUri("https://google.com")
                .tppId("totoID")
                .date(DateTime.now())
                .build());
        callBackCounterEntryRepository.save(CallBackCounterEntry.builder()
                .callBackResponseStatus(CallBackResponseStatus.FAILURE)
                .redirectUri("https://google.com")
                .tppId("totoID")
                .date(DateTime.now())
                .build());

        // When
        HttpResponse<Donut> response = Unirest.get("http://metrics-services:" + port + "/api/kpi/callbacks/byResponseStatus")
                .queryString("fromDate", DateTime.now().minusDays(1))
                .queryString("toDate", DateTime.now().plusDays(1))
                .header("Content-Type", "application/json")
                .asObject(Donut.class);

        // Then
        assertThat(response.isSuccess()).isTrue();

        Optional<Donut.Section> successSection = response.getBody().getSection(CallBackResponseStatus.SUCCESS.name());
        assertThat(successSection.isPresent()).isTrue();
        assertThat(successSection.get().getValue()).isEqualTo(3l);

        Optional<Donut.Section> failureSection = response.getBody().getSection(CallBackResponseStatus.FAILURE.name());
        assertThat(failureSection.isPresent()).isTrue();
        assertThat(failureSection.get().getValue()).isEqualTo(2l);
    }
}