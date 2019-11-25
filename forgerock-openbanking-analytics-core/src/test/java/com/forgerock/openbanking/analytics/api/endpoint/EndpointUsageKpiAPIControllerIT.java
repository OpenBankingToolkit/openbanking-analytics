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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forgerock.openbanking.analytics.charts.Table;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageEntry;
import com.forgerock.openbanking.analytics.model.entries.GeoIP;
import com.forgerock.openbanking.analytics.model.entries.TppEntry;
import com.forgerock.openbanking.analytics.model.kpi.*;
import com.forgerock.openbanking.analytics.model.openbanking.OBGroupName;
import com.forgerock.openbanking.analytics.models.ApplicationType;
import com.forgerock.openbanking.analytics.repository.EndpointUsageAggregateRepository;
import com.forgerock.openbanking.analytics.repository.TppEntryRepository;
import com.forgerock.openbanking.constants.OpenBankingConstants;
import com.forgerock.openbanking.model.UserContext;
import com.github.jsonzou.jmockdata.JMockData;
import com.github.jsonzou.jmockdata.MockConfig;
import com.google.common.collect.ImmutableMap;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.forgerock.openbanking.analytics.api.jwkms.JWT.jws;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class EndpointUsageKpiAPIControllerIT {

    @LocalServerPort
    private int port;

    @Value("${session.issuer-id}")
    private String issuerId;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EndpointUsageAggregateRepository endpointUsageAggregateRepository;
    @MockBean
    private TppEntryRepository tppEntryRepository;
    private MockConfig mockConfig = new MockConfig() {{
        registerMocker(m -> claimSet(), JWTClaimsSet.class);
    }};

    @Before
    public void setUp() {
        Unirest.config()
                .setObjectMapper(new JacksonObjectMapper(objectMapper)).verifySsl(false);
    }

    @After
    public void tearDown() {
        endpointUsageAggregateRepository.deleteAll();
    }

    @Test
    public void getEndpointUsageAggregateSortedByCountDesc() throws Exception {
        // Given
        endpointUsageAggregateRepository.save(aggregate("/accounts"));
        endpointUsageAggregateRepository.save(aggregate("/account-access-consent"));
        endpointUsageAggregateRepository.save(aggregate("/account-access-consent"));

        // When
        Table<EndpointUsageAggregate> table = callEndpointUsageAggregated(EndpointTableRequest.builder()
                .from(DateTime.now().minusDays(1))
                .to(DateTime.now().plusDays(1))
                .sort(Arrays.asList(new SortOrder("count", Sort.Direction.DESC)))
                .fields(Arrays.asList("endpoint")).filters(Arrays.asList(EndpointTableFilter.builder()
                        .field("endpointType")
                        .regex("AISP")
                        .build()))
                .build());

        // Then
        assertThat(table.getCurrentPage()).isEqualTo(0);
        assertThat(table.getTotalPages()).isEqualTo(1);
        assertThat(table.getTotalResults()).isEqualTo(2);

        EndpointUsageAggregate endpointUsageAggregate = table.getData().get(0);
        assertThat(endpointUsageAggregate.getEndpoint()).isEqualTo("/account-access-consent");
        assertThat(endpointUsageAggregate.getEndpointStatisticKPI().getValuesByAggregationMethods().get(AggregationMethod.BY_NB_CALLS)).isEqualTo(6l);
        assertThat(endpointUsageAggregate.getEndpointStatisticKPI().getValuesByAggregationMethods().get(AggregationMethod.BY_RESPONSE_TIME_AVERAGE)).isEqualTo(200l);

    }


    @Test
    public void getEndpointUsageAggregateSecondPage() throws Exception {
        // Given
        List<EndpointUsageAggregate> aggregates = IntStream.range(0, 11).mapToObj(i -> aggregate("/test-" + i)).collect(Collectors.toList());
        endpointUsageAggregateRepository.saveAll(aggregates);

        // When
        Table<EndpointUsageAggregate> table = callEndpointUsageAggregated(EndpointTableRequest.builder()
                .from(DateTime.now().minusDays(1))
                .to(DateTime.now().plusDays(1))
                .sort(Arrays.asList(new SortOrder("count", Sort.Direction.DESC)))
                .fields(Arrays.asList("endpoint"))
                .page(1)
                .build());

        // Then
        assertThat(table.getCurrentPage()).isEqualTo(1l);
    }

    @Test
    public void addEntryCreatesAggregatedEndpointUsage() throws Exception {
        // Given
        EndpointUsageEntry entry = JMockData.mock(EndpointUsageEntry.class, mockConfig);
        entry.setDate(entry.getDate().withMillisOfSecond(0));
        given(tppEntryRepository.findById(entry.getIdentityId())).willReturn(Optional.of(
                TppEntry.builder()
                        .oidcClientId(entry.getIdentityId())
                        .name("toto")
                        .build()));

        // When
        HttpResponse response = Unirest.post("http://metrics-services:" + port + "/api/kpi/endpoint-usage/add-entry")
                .header("Content-Type", "application/json")
                .body(entry)
                .asJson();

        // Then
        assertThat(response.isSuccess()).isTrue();
        EndpointUsageAggregate aggregate = EndpointUsageAggregate.newAggregate(entry);
        aggregate.setCount(1L);
        assertThat(endpointUsageAggregateRepository.findAll())
                .containsOnly(aggregate);
    }

    @Test
    public void addDuplicateEntriesCreatesAggregatedEndpointUsage() throws Exception {
        // Given
        EndpointUsageEntry entry = JMockData.mock(EndpointUsageEntry.class, mockConfig);
        entry.setDate(entry.getDate().withMillisOfSecond(0));
        given(tppEntryRepository.findById(entry.getIdentityId())).willReturn(Optional.of(
                TppEntry.builder()
                        .oidcClientId(entry.getIdentityId())
                        .name("toto")
                        .build()));
        // When
        HttpResponse response = Unirest.post("http://metrics-services:" + port + "/api/kpi/endpoint-usage/add-entries")
                .header("Content-Type", "application/json")
                .body(Arrays.asList(entry, entry))
                .asJson();

        // Then
        assertThat(response.isSuccess()).isTrue();
        EndpointUsageAggregate aggregate = EndpointUsageAggregate.newAggregate(entry);
        aggregate.setCount(2L);
        assertThat(endpointUsageAggregateRepository.findAll())
                .containsOnly(aggregate);
    }

    @Test
    public void addDuplicateEntryIncrementsAggregatedEndpointUsage() throws Exception {
        // Given
        EndpointUsageEntry entry = JMockData.mock(EndpointUsageEntry.class, mockConfig);
        entry.setDate(entry.getDate().withMillisOfSecond(0));
        given(tppEntryRepository.findById(entry.getIdentityId())).willReturn(Optional.of(
                TppEntry.builder()
                        .oidcClientId(entry.getIdentityId())
                        .name("toto")
                        .build()));

        EndpointUsageAggregate aggregate = EndpointUsageAggregate.newAggregate(entry);
        aggregate.setCount(1L);
        endpointUsageAggregateRepository.save(aggregate);

        // When
        HttpResponse response = Unirest.post("http://metrics-services:" + port + "/api/kpi/endpoint-usage/add-entries")
                .header("Content-Type", "application/json")
                .body(Arrays.asList(entry, entry))
                .asJson();

        // Then
        assertThat(response.isSuccess()).isTrue();
        aggregate.setCount(3L);
        assertThat(endpointUsageAggregateRepository.findAll())
                .containsOnly(aggregate);
    }

    @Test
    public void getEndpointUsageAggregateFilterUserType() throws Exception {
        // Given
        EndpointUsageAggregate aggregate1 = aggregate("/account-access-consent");
        aggregate1.setUserType(UserContext.UserType.OIDC_CLIENT);
        EndpointUsageAggregate aggregate2 = aggregate("/account-access-consent");
        aggregate2.setUserType(UserContext.UserType.ANONYMOUS);
        endpointUsageAggregateRepository.saveAll(Arrays.asList(aggregate1, aggregate2));

        // When
        Table<EndpointUsageAggregate> table = callEndpointUsageAggregated(EndpointTableRequest.builder()
                .from(DateTime.now().minusDays(1))
                .to(DateTime.now().plusDays(1))
                .sort(Arrays.asList(new SortOrder("count", Sort.Direction.DESC)))
                .fields(Arrays.asList("endpoint", "userType")).filters(Arrays.asList(EndpointTableFilter.builder()
                        .field("userType")
                        .regex(UserContext.UserType.ANONYMOUS.name())
                        .build()))
                .build());


        // Then
        assertThat(table.getData().get(0).getUserType()).isEqualTo(UserContext.UserType.ANONYMOUS);
    }

    @Test
    public void getEndpointUsageAggregateFilterEndpointType() throws Exception {
        // Given
        String jws = jws("accounts", "GROUP_FORGEROCK");
        EndpointUsageAggregate aggregate1 = aggregate("/account-access-consent");
        aggregate1.setUserType(UserContext.UserType.OIDC_CLIENT);
        aggregate1.setEndpointType(OBGroupName.AISP);
        EndpointUsageAggregate aggregate2 = aggregate("/account-access-consent");
        aggregate2.setUserType(UserContext.UserType.ANONYMOUS);
        aggregate2.setEndpointType(OBGroupName.PISP);
        endpointUsageAggregateRepository.saveAll(Arrays.asList(aggregate1, aggregate2));

        // When
        Table<EndpointUsageAggregate> table = callEndpointUsageAggregated(EndpointTableRequest.builder()
                .from(DateTime.now().minusDays(1))
                .to(DateTime.now().plusDays(1))
                .sort(Arrays.asList(new SortOrder("count", Sort.Direction.DESC)))
                .fields(Arrays.asList("endpoint", "endpointType")).filters(Arrays.asList(EndpointTableFilter.builder()
                        .field("endpointType")
                        .regex(OBGroupName.PISP.name())
                        .build()))
                .build());

        // Then
        assertThat(table.getData().get(0).getEndpointType()).isEqualTo(OBGroupName.PISP);
    }

    @Test
    public void getEndpointUsageAggregateFilterApplication() throws Exception {
        // Given

        EndpointUsageAggregate aggregate1 = aggregate("/account-access-consent");
        EndpointUsageAggregate aggregate2 = aggregate("/account-access-consent");
        aggregate2.setApplication(ApplicationType.JWKMS.toString());
        endpointUsageAggregateRepository.saveAll(Arrays.asList(aggregate1, aggregate2));

        // When
        Table<EndpointUsageAggregate> table = callEndpointUsageAggregated(EndpointTableRequest.builder()
                .from(DateTime.now().minusDays(1))
                .to(DateTime.now().plusDays(1))
                .sort(Arrays.asList(new SortOrder("count", Sort.Direction.DESC)))
                .fields(Arrays.asList("endpoint", "application"))
                .filters(Arrays.asList(EndpointTableFilter.builder()
                        .field("application")
                        .regex("jwkms")
                        .build()))
                .build());

        // Then
        assertThat(table.getData().get(0).getApplication()).isEqualTo("jwkms");
    }


    @Test
    public void testEndpointStatistic() throws Exception {
        // Given
        DateTime startDate = DateTime.parse("2018-01-01T00:00:00.000-00:00");
        DateTime endDate = DateTime.parse("2018-01-06T00:00:00.000-00:00");
        DateTime currentDate = startDate;
        String endpoint = "/toto";
        String tpp = "bob";
        String status = "200";

        List<EndpointUsageEntry> endpointUsageEntries = new ArrayList<>();

        while (currentDate.isBefore(endDate)) {
            for(int i = 0; i < 10; i++) {
                endpointUsageEntries.add(
                        EndpointUsageEntry.builder()
                                .endpoint(endpoint)
                                .date(currentDate)
                                .identityId(tpp)
                                .responseStatus(status)
                                .responseTime(200l)
                                .build());
            }
            currentDate = currentDate.plusDays(1);
        }

        callAddEntries(endpointUsageEntries);

        // When
        EndpointStatisticKPI data = callEndpointStatistic(endpoint, startDate, endDate);
        // Then
        assertThat(data).isNotNull();
        assertThat(data.getValuesByAggregationMethods().get(AggregationMethod.BY_NB_CALLS)).isEqualTo(10 * 5);
        assertThat(data.getValuesByAggregationMethods().get(AggregationMethod.BY_RESPONSE_TIME_AVERAGE)).isEqualTo(200l);
    }

    @Test
    public void testEndpointStatisticWithDifferentResponseTime() throws Exception {
        // Given
        DateTime startDate = DateTime.parse("2018-01-01T00:00:00.000-00:00");
        DateTime endDate = DateTime.parse("2018-01-06T00:00:00.000-00:00");
        DateTime currentDate = startDate;
        String endpoint = "/toto";
        String tpp = "bob";
        String status = "200";

        List<EndpointUsageEntry> endpointUsageEntries = new ArrayList<>();

        endpointUsageEntries.add(
                EndpointUsageEntry.builder().endpoint(endpoint)
                        .date(currentDate)
                        .identityId(tpp).responseStatus(status)
                        .responseTime(100l)
                        .build());


        endpointUsageEntries.add(
                EndpointUsageEntry.builder().endpoint(endpoint)
                        .date(currentDate.plusDays(1)).identityId(tpp).responseStatus(status)
                        .responseTime(200l)
                        .build());
        endpointUsageEntries.add(
                EndpointUsageEntry.builder().endpoint(endpoint)
                        .date(currentDate.plusDays(1)).identityId(tpp).responseStatus(status)
                        .responseTime(200l)
                        .build());

        endpointUsageEntries.add(
                EndpointUsageEntry.builder().endpoint(endpoint)
                        .date(currentDate.plusDays(2))
                        .identityId(tpp).responseStatus(status)
                        .responseTime(300l)
                        .build());
        endpointUsageEntries.add(
                EndpointUsageEntry.builder().endpoint(endpoint)
                        .date(currentDate.plusDays(2))
                        .identityId(tpp).responseStatus(status)
                        .responseTime(300l)
                        .build());
        endpointUsageEntries.add(
                EndpointUsageEntry.builder().endpoint(endpoint)
                        .date(currentDate.plusDays(2))
                        .identityId(tpp).responseStatus(status)
                        .responseTime(300l)
                        .build());

        endpointUsageEntries.add(
                EndpointUsageEntry.builder().endpoint(endpoint)
                        .date(currentDate.plusDays(3))
                        .identityId(tpp).responseStatus(status)
                        .responseTime(200l)
                        .build());
        endpointUsageEntries.add(
                EndpointUsageEntry.builder().endpoint(endpoint)
                        .date(currentDate.plusDays(3))
                        .identityId(tpp).responseStatus(status)
                        .responseTime(200l)
                        .build());

        endpointUsageEntries.add(
                EndpointUsageEntry.builder().endpoint(endpoint)
                        .date(currentDate.plusDays(4))
                        .identityId(tpp).responseStatus(status)
                        .responseTime(100l)
                        .build());
        endpointUsageEntries.add(
                EndpointUsageEntry.builder().endpoint(endpoint)
                        .date(currentDate.plusDays(4))
                        .identityId(tpp).responseStatus(status)
                        .responseTime(100l)
                        .build());

        callAddEntries(endpointUsageEntries);

        // When
        EndpointStatisticKPI data = callEndpointStatistic(endpoint, startDate, endDate);
        // Then
        assertThat(data).isNotNull();
        assertThat(data.getValuesByAggregationMethods().get(AggregationMethod.BY_NB_CALLS)).isEqualTo(10);
        assertThat(data.getValuesByAggregationMethods().get(AggregationMethod.BY_RESPONSE_TIME_AVERAGE)).isEqualTo(Math.round((100 * 3 + 200 * 4 + 300 * 3) / 10.0));
    }


    @Test
    public void testEndpointStatisticPercentile() throws Exception {
        // Given
        DateTime startDate = DateTime.parse("2018-01-01T00:00:00.000-00:00");
        DateTime endDate = DateTime.parse("2018-01-06T00:00:00.000-00:00");
        DateTime currentDate = startDate;
        String endpoint = "/toto";
        String tpp = "bob";
        String status = "200";

        List<EndpointUsageEntry> endpointUsageEntries = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            endpointUsageEntries.add(
                    EndpointUsageEntry.builder().endpoint(endpoint)
                            .date(currentDate.plusHours(i % 48))
                            .identityId(tpp).responseStatus(status)
                            .responseTime(i * 100l)
                            .build());
        }

        callAddEntries(endpointUsageEntries);

        // When
        EndpointStatisticKPI data = callEndpointStatistic(endpoint, startDate, endDate);
        // Then
        assertThat(data).isNotNull();
        assertThat(data.getValuesByAggregationMethods().get(AggregationMethod.BY_NB_CALLS)).isEqualTo(100);
        assertThat(data.getValuesByAggregationMethods().get(AggregationMethod.BY_RESPONSE_TIME_PERCENTILE_85)).isEqualTo(8500l);
        assertThat(data.getValuesByAggregationMethods().get(AggregationMethod.BY_RESPONSE_TIME_PERCENTILE_90)).isEqualTo(9000l);
        assertThat(data.getValuesByAggregationMethods().get(AggregationMethod.BY_RESPONSE_TIME_PERCENTILE_95)).isEqualTo(9500l);
        assertThat(data.getValuesByAggregationMethods().get(AggregationMethod.BY_RESPONSE_TIME_PERCENTILE_99)).isEqualTo(9900l);
    }

    @Test
    public void drawResponseTimeSimpleGraphByDate() throws Exception {
        // Given
        DateTime startDate = DateTime.parse("2018-01-01T00:00:00.000-00:00");
        DateTime endDate = DateTime.parse("2018-01-06T00:00:00.000-00:00");
        DateTime currentDate = startDate;
        String endpoint = "/toto";
        String tpp = "bob";
        String status = "200";

        int k = 1;
        List<EndpointUsageEntry> endpointUsageEntries = new ArrayList<>();

        while (currentDate.isBefore(endDate)) {
            for(int i = 0; i < k; i++) {
                endpointUsageEntries.add(
                        EndpointUsageEntry.builder()
                                .endpoint(endpoint)
                                .date(currentDate)
                                .identityId(tpp)
                                .responseStatus(status)
                                .responseTime(k * 100l)
                                .build());
            }
            currentDate = currentDate.plusDays(1);
            k++;
        }

        callAddEntries(endpointUsageEntries);

        // When
        EndpointsUsageKPI data = callEndpointUsage(EndpointUsageKpiRequest.builder()
                .aggregations(new LinkedList<>(Arrays.asList(EndpointsUsageAggregation.BY_DATE)))
                .dateGranularity(EndpointsUsageKPI.DateGranularity.BY_DAY)
                .endpoint(endpoint)
                .filtering(null)
                .from(startDate)
                .to(endDate)
                .aggregationMethod(AggregationMethod.BY_RESPONSE_TIME_AVERAGE)
                .build());
        // Then
        assertThat(data).isNotNull();

        /**
         * The graph should be linear
         *         X
         *       X
         *     X
         *   X
         * X
         */
        currentDate = startDate;
        k = 0;
        while (currentDate.isBefore(endDate)) {
            assertThat(data.getLines()).isNotEmpty();
            assertThat(data.getLines().get(0).getDataset()[k]).isEqualTo(((k + 1) * 100));
            currentDate = currentDate.plusDays(1);
            k++;
        }
    }



    @Test
    public void drawNbCallsSimpleGraphByDate() throws Exception {
        // Given
        DateTime startDate = DateTime.parse("2018-01-01T00:00:00.000-00:00");
        DateTime endDate = DateTime.parse("2018-01-06T00:00:00.000-00:00");
        DateTime currentDate = startDate;
        String endpoint = "/toto";
        String tpp = "bob";
        String status = "200";

        int k = 1;
        List<EndpointUsageEntry> endpointUsageEntries = new ArrayList<>();

        while (currentDate.isBefore(endDate)) {
            for(int i = 0; i < k; i++) {
                endpointUsageEntries.add(
                        EndpointUsageEntry.builder()
                                .endpoint(endpoint)
                                .date(currentDate)
                                .identityId(tpp)
                                .responseStatus(status)
                                .build());
            }
            currentDate = currentDate.plusDays(1);
            k++;
        }

        callAddEntries(endpointUsageEntries);

        // When
        EndpointsUsageKPI data = callEndpointUsage(EndpointUsageKpiRequest.builder()
                .aggregations(new LinkedList<>(Arrays.asList(EndpointsUsageAggregation.BY_DATE)))
                .dateGranularity(EndpointsUsageKPI.DateGranularity.BY_DAY)
                .endpoint(endpoint)
                .filtering(null)
                .from(startDate)
                .to(endDate)
                .build());
        // Then
        assertThat(data).isNotNull();

        /**
         * The graph should be linear
         *         X
         *       X
         *     X
         *   X
         * X
         */
        currentDate = startDate;
        k = 1;
        while (currentDate.isBefore(endDate)) {
            assertThat(data.getLines()).isNotEmpty();
            assertThat(data.getLines().get(0).getDataset()[k - 1]).isEqualTo(k);
            currentDate = currentDate.plusDays(1);
            k++;
        }
    }

    @Test
    public void drawNbCallsGraphByDateAndTpp() throws Exception {
        // Given
        DateTime startDate = DateTime.parse("2018-01-01T00:00:00.000-00:00");
        DateTime endDate = DateTime.parse("2018-01-06T00:00:00.000-00:00");
        String endpoint = "/toto";
        List<String> tpps = Arrays.asList("bob", "alice");
        String status = "200";

        List<EndpointUsageEntry> endpointUsageEntries = new ArrayList<>();
        for (String tpp: tpps) {
            int k = 1;
            DateTime currentDate = startDate;
            while (currentDate.isBefore(endDate)) {
                for (int i = 0; i < k; i++) {
                    endpointUsageEntries.add(
                            EndpointUsageEntry.builder()
                                    .endpoint(endpoint)
                                    .date(currentDate)
                                    .identityId(tpp)
                                    .responseStatus(status)
                                    .build());
                }
                currentDate = currentDate.plusDays(1);
                k++;
            }
        }

        callAddEntries(endpointUsageEntries);

        // When
        EndpointsUsageKPI data = callEndpointUsage(EndpointUsageKpiRequest.builder()
                .aggregations(new LinkedList<>(Arrays.asList(EndpointsUsageAggregation.BY_DATE, EndpointsUsageAggregation.BY_TPP)))
                .dateGranularity(EndpointsUsageKPI.DateGranularity.BY_DAY)
                .endpoint(endpoint)
                .filtering(null)
                .from(startDate)
                .to(endDate)
                .build());
        // Then
        assertThat(data).isNotNull();

        /**
         * The graph should be linear for each TPP
         *         X
         *       X
         *     X
         *   X
         * X
         */

        assertThat(data.getLines().size()).isEqualTo(tpps.size());
        DateTime currentDate = startDate;
        int k = 1;
        while (currentDate.isBefore(endDate)) {
            assertThat(data.getLines()).isNotEmpty();
            assertThat(data.getLines().get(0).getDataset()[k - 1]).isEqualTo(k);
            currentDate = currentDate.plusDays(1);
            k++;
        }
    }

    @Test
    public void drawNbCallsGraphByDateAndStatus() throws Exception {
        // Given
        DateTime startDate = DateTime.parse("2018-01-01T00:00:00.000-00:00");
        DateTime endDate = DateTime.parse("2018-01-06T00:00:00.000-00:00");
        DateTime currentDate = startDate;
        String endpoint = "/toto";
        String tpp = "bob";
        List<String> statuses = Arrays.asList("200", "400", "500");
        List<EndpointUsageEntry> endpointUsageEntries = new ArrayList<>();

        int k = 1;
        while (currentDate.isBefore(endDate)) {
            for(String status: statuses) {
                for (int i = 0; i < k; i++) {
                    endpointUsageEntries.add(
                            EndpointUsageEntry.builder()
                                    .endpoint(endpoint)
                                    .date(currentDate)
                                    .identityId(tpp)
                                    .responseStatus(status)
                                    .build());
                }
            }
            currentDate = currentDate.plusDays(1);
            k++;
        }
        callAddEntries(endpointUsageEntries);

        // When
        EndpointsUsageKPI data = callEndpointUsage(EndpointUsageKpiRequest.builder()
                .aggregations(new LinkedList<>(Arrays.asList(EndpointsUsageAggregation.BY_DATE, EndpointsUsageAggregation.BY_RESPONSE_STATUS)))
                .dateGranularity(EndpointsUsageKPI.DateGranularity.BY_DAY)
                .endpoint(endpoint)
                .filtering(null)
                .from(startDate)
                .to(endDate)
                .build());
        // Then
        assertThat(data).isNotNull();

        /**
         * The graph should be linear
         *         X
         *       X
         *     X
         *   X
         * X
         */
        assertThat(data.getLines().size()).isEqualTo(statuses.size());
        currentDate = startDate;
        k = 1;
        while (currentDate.isBefore(endDate)) {
            assertThat(data.getLines()).isNotEmpty();
            assertThat(data.getLines().get(0).getDataset()[k - 1]).isEqualTo(k);
            currentDate = currentDate.plusDays(1);
            k++;
        }
    }


    @Test
    public void drawNbCallsGraphByWeekDate() throws Exception {
        // Given
        String endpoint = "/toto";
        String tpp = "bob";
        String status = "200";
        DateTime startDate = DateTime.parse("2019-05-27T00:00:00.000-00:00");
        DateTime endDate = DateTime.parse("2019-06-10T00:00:00.000-00:00");

        Map<DateTime, Integer> nbCallsByDate = ImmutableMap.<DateTime, Integer>builder()
                //First Monday
                .put(DateTime.parse("2019-05-27T10:20:00.000-00:00"), 1)
                .put(DateTime.parse("2019-05-27T11:30:00.000-00:00"), 2)
                .put(DateTime.parse("2019-05-27T12:40:00.000-00:00"), 3)
                .put(DateTime.parse("2019-05-27T13:50:00.000-00:00"), 2)
                .put(DateTime.parse("2019-05-27T14:10:00.000-00:00"), 1)
                //First Tuesday
                .put(DateTime.parse("2019-05-28T10:20:00.000-00:00"), 1)
                .put(DateTime.parse("2019-05-28T11:30:00.000-00:00"), 2)
                .put(DateTime.parse("2019-05-28T12:40:00.000-00:00"), 3)
                .put(DateTime.parse("2019-05-28T13:50:00.000-00:00"), 2)
                .put(DateTime.parse("2019-05-28T14:10:00.000-00:00"), 1)
                //First Thursday
                .put(DateTime.parse("2019-05-30T10:20:00.000-00:00"), 1)
                .put(DateTime.parse("2019-05-30T11:30:00.000-00:00"), 2)
                .put(DateTime.parse("2019-05-30T12:40:00.000-00:00"), 3)
                .put(DateTime.parse("2019-05-30T13:50:00.000-00:00"), 2)
                .put(DateTime.parse("2019-05-30T14:10:00.000-00:00"), 1)
                //First Saturday
                .put(DateTime.parse("2019-06-01T10:20:00.000-00:00"), 1)
                .put(DateTime.parse("2019-06-01T11:30:00.000-00:00"), 2)
                .put(DateTime.parse("2019-06-01T12:40:00.000-00:00"), 3)
                .put(DateTime.parse("2019-06-01T13:50:00.000-00:00"), 2)
                .put(DateTime.parse("2019-06-01T14:10:00.000-00:00"), 1)

                //Second Monday
                .put(DateTime.parse("2019-06-03T10:20:00.000-00:00"), 1)
                .put(DateTime.parse("2019-06-03T11:30:00.000-00:00"), 2)
                .put(DateTime.parse("2019-06-03T12:40:00.000-00:00"), 3)
                .put(DateTime.parse("2019-06-03T13:50:00.000-00:00"), 2)
                .put(DateTime.parse("2019-06-03T14:10:00.000-00:00"), 1)
                //Second Wednesday
                .put(DateTime.parse("2019-06-05T10:20:00.000-00:00"), 1)
                .put(DateTime.parse("2019-06-05T11:30:00.000-00:00"), 2)
                .put(DateTime.parse("2019-06-05T12:40:00.000-00:00"), 3)
                .put(DateTime.parse("2019-06-05T13:50:00.000-00:00"), 2)
                .put(DateTime.parse("2019-06-05T14:10:00.000-00:00"), 1)
                //Second Thursday
                .put(DateTime.parse("2019-06-06T10:20:00.000-00:00"), 1)
                .put(DateTime.parse("2019-06-06T11:30:00.000-00:00"), 2)
                .put(DateTime.parse("2019-06-06T12:40:00.000-00:00"), 3)
                .put(DateTime.parse("2019-06-06T13:50:00.000-00:00"), 2)
                .put(DateTime.parse("2019-06-06T14:10:00.000-00:00"), 1)
                //Second Sunday
                .put(DateTime.parse("2019-06-09T10:20:00.000-00:00"), 1)
                .put(DateTime.parse("2019-06-09T11:30:00.000-00:00"), 2)
                .put(DateTime.parse("2019-06-09T12:40:00.000-00:00"), 3)
                .put(DateTime.parse("2019-06-09T13:50:00.000-00:00"), 2)
                .put(DateTime.parse("2019-06-09T14:10:00.000-00:00"), 1)
                .build();
        List<EndpointUsageEntry> endpointUsageEntries = new ArrayList<>();

        nbCallsByDate.entrySet().stream().forEach(e -> {
                    for (int i = 0; i < e.getValue(); i++) {
                        endpointUsageEntries.add(
                                EndpointUsageEntry.builder()
                                        .endpoint(endpoint)
                                        .date(e.getKey())
                                        .identityId(tpp)
                                        .responseStatus(status)
                                        .build());
                    }
        });
        callAddEntries(endpointUsageEntries);

        // When
        EndpointsUsageKPI data = callEndpointUsage(EndpointUsageKpiRequest.builder()
                .aggregations(new LinkedList<>(Arrays.asList(EndpointsUsageAggregation.BY_WEEK_DAY)))
                .endpoint(endpoint)
                .filtering(null)
                .from(startDate)
                .to(endDate)
                .build());

        // Then
        assertThat(data).isNotNull();

        log.debug("Data received from the graph: {}", data);
        //Monday
        assertThat(getValue(data, 0, 9 ,0)).isEqualTo(0l);
        assertThat(getValue(data, 0, 10 ,0)).isEqualTo(2l);
        assertThat(getValue(data, 0, 11 ,0)).isEqualTo(4l);
        assertThat(getValue(data, 0, 12 ,0)).isEqualTo(6l);
        assertThat(getValue(data, 0, 13 ,0)).isEqualTo(4l);
        assertThat(getValue(data, 0, 14 ,0)).isEqualTo(2l);
        assertThat(getValue(data, 0, 15 ,0)).isEqualTo(0l);
        //Tuesday
        assertThat(getValue(data, 1, 9 ,0)).isEqualTo(0l);
        assertThat(getValue(data, 1, 10 ,0)).isEqualTo(1l);
        assertThat(getValue(data, 1, 11 ,0)).isEqualTo(2l);
        assertThat(getValue(data, 1, 12 ,0)).isEqualTo(3l);
        assertThat(getValue(data, 1, 13 ,0)).isEqualTo(2l);
        assertThat(getValue(data, 1, 14 ,0)).isEqualTo(1l);
        assertThat(getValue(data, 1, 15 ,0)).isEqualTo(0l);
        //Wednesday
        assertThat(getValue(data, 2, 9 ,0)).isEqualTo(0l);
        assertThat(getValue(data, 2, 10 ,0)).isEqualTo(1l);
        assertThat(getValue(data, 2, 11 ,0)).isEqualTo(2l);
        assertThat(getValue(data, 2, 12 ,0)).isEqualTo(3l);
        assertThat(getValue(data, 2, 13 ,0)).isEqualTo(2l);
        assertThat(getValue(data, 2, 14 ,0)).isEqualTo(1l);
        assertThat(getValue(data, 2, 15 ,0)).isEqualTo(0l);
        //Thursday
        assertThat(getValue(data, 3, 9 ,0)).isEqualTo(0l);
        assertThat(getValue(data, 3, 10 ,0)).isEqualTo(2l);
        assertThat(getValue(data, 3, 11 ,0)).isEqualTo(4l);
        assertThat(getValue(data, 3, 12 ,0)).isEqualTo(6l);
        assertThat(getValue(data, 3, 13 ,0)).isEqualTo(4l);
        assertThat(getValue(data, 3, 14 ,0)).isEqualTo(2l);
        assertThat(getValue(data, 3, 15 ,0)).isEqualTo(0l);
        //Friday
        assertThat(getValue(data, 4, 9 ,0)).isEqualTo(0l);
        assertThat(getValue(data, 4, 10 ,0)).isEqualTo(0l);
        assertThat(getValue(data, 4, 11 ,0)).isEqualTo(0l);
        assertThat(getValue(data, 4, 12 ,0)).isEqualTo(0l);
        assertThat(getValue(data, 4, 13 ,0)).isEqualTo(0l);
        assertThat(getValue(data, 4, 14 ,0)).isEqualTo(0l);
        assertThat(getValue(data, 4, 15 ,0)).isEqualTo(0l);
        //Saturday
        assertThat(getValue(data, 5, 9 ,0)).isEqualTo(0l);
        assertThat(getValue(data, 5, 10 ,0)).isEqualTo(1l);
        assertThat(getValue(data, 5, 11 ,0)).isEqualTo(2l);
        assertThat(getValue(data, 5, 12 ,0)).isEqualTo(3l);
        assertThat(getValue(data, 5, 13 ,0)).isEqualTo(2l);
        assertThat(getValue(data, 5, 14 ,0)).isEqualTo(1l);
        assertThat(getValue(data, 5, 15 ,0)).isEqualTo(0l);
        //Sunday
        assertThat(getValue(data, 6, 9 ,0)).isEqualTo(0l);
        assertThat(getValue(data, 6, 10 ,0)).isEqualTo(1l);
        assertThat(getValue(data, 6, 11 ,0)).isEqualTo(2l);
        assertThat(getValue(data, 6, 12 ,0)).isEqualTo(3l);
        assertThat(getValue(data, 6, 13 ,0)).isEqualTo(2l);
        assertThat(getValue(data, 6, 14 ,0)).isEqualTo(1l);
        assertThat(getValue(data, 6, 15 ,0)).isEqualTo(0l);
    }

    @Test
    public void collectGeoIP() throws Exception {
        // Given
        DateTime startDate = DateTime.parse("2018-01-01T00:00:00.000-00:00");
        DateTime endDate = startDate.plusDays(2);
        String endpoint = "/toto";
        String tpp = "bob";
        String status = "200";

        List<EndpointUsageEntry> endpointUsageEntries = new ArrayList<>();

        EndpointUsageEntry geoIpdEntry = EndpointUsageEntry.builder()
                .endpoint(endpoint)
                .date(startDate.plusMinutes(5))
                .identityId(tpp)
                .responseStatus(status)
                .responseTime(Math.round(Math.pow(10, 1)))
                .geoIP(GeoIP.builder()
                        .ipAddress("127.0.0.1")
                        .build())
                .build();

        endpointUsageEntries.add(geoIpdEntry);

        callAddEntries(endpointUsageEntries);

        // When
        Table<EndpointUsageAggregate> table = callEndpointUsageRawData(startDate, endDate);

        // Then
        assertThat(table.getCurrentPage()).isEqualTo(0);
        assertThat(table.getTotalPages()).isEqualTo(1);
        assertThat(table.getTotalResults()).isEqualTo(1);

        EndpointUsageAggregate endpointUsageAggregate = table.getData().get(0);
        assertThat(endpointUsageAggregate.getGeoIP()).isNotNull();
        assertThat(endpointUsageAggregate.getGeoIP().getIpAddress()).isEqualTo("127.0.0.1");
    }

    @Test
    public void drawResponseTime() throws Exception {
        // Given
        DateTime startDate = DateTime.parse("2018-01-01T00:00:00.000-00:00");
        String endpoint = "/toto";
        String tpp = "bob";
        String status = "200";

        List<EndpointUsageEntry> endpointUsageEntries = new ArrayList<>();

        EndpointUsageEntry entry10 = EndpointUsageEntry.builder()
                .endpoint(endpoint)
                .date(startDate)
                .identityId(tpp)
                .responseStatus(status)
                .responseTime(Math.round(Math.pow(10, 1)))
                .build();
        EndpointUsageEntry entry100 = EndpointUsageEntry.builder()
                .endpoint(endpoint)
                .date(startDate)
                .identityId(tpp)
                .responseStatus(status)
                .responseTime(Math.round(Math.pow(10, 2)))
                .build();
        EndpointUsageEntry entry1000 = EndpointUsageEntry.builder()
                .endpoint(endpoint)
                .date(startDate)
                .identityId(tpp)
                .responseStatus(status)
                .responseTime(Math.round(Math.pow(10, 3)))
                .build();

        endpointUsageEntries.add(entry10);
        endpointUsageEntries.add(entry100);
        endpointUsageEntries.add(entry100);
        endpointUsageEntries.add(entry1000);
        endpointUsageEntries.add(entry1000);
        endpointUsageEntries.add(entry1000);

        callAddEntries(endpointUsageEntries);

        // When
        EndpointsUsageKPI data = callEndpointUsage(EndpointUsageKpiRequest.builder()
                .aggregations(new LinkedList<>(Arrays.asList(EndpointsUsageAggregation.BY_RESPONSE_TIME)))
                .endpoint(endpoint)
                .aggregationMethod(AggregationMethod.BY_NB_RESPONSE_TIME)
                .filtering(null)
                .from(startDate)
                .to(startDate.plusDays(1))
                .build());
        // Then
        assertThat(data).isNotNull();

        /**
         * The graph should be linear
         *         X
         *       X
         *     X
         *   X
         * X
         */
        assertThat(data.getLines()).isNotEmpty();
        EndpointsUsageKPI.Line line = data.getLines().get(0);
        assertThat(line.getDataset()[33]).isEqualTo(1);
        assertThat(line.getDataset()[66]).isEqualTo(2);
        assertThat(line.getDataset()[99]).isEqualTo(3);

    }

    private long getValue(EndpointsUsageKPI data, int dayOfWeek, int hour, int indexLine) {
        int index = dayOfWeek * 24 + hour;
        return data.getLines().get(indexLine).getDataset()[index];
    }

    private EndpointsUsageKPI callEndpointUsage(EndpointUsageKpiRequest request) throws ParseException, JOSEException {

        HttpResponse<EndpointsUsageKPI> response = Unirest.post("http://metrics-services:" + port + "/api/kpi/endpoint-usage/")
                .header("Content-Type", "application/json")
                .queryString("application", ApplicationType.JWKMS.name())
                .body(request)
                .asObject(EndpointsUsageKPI.class);

        assertThat(response.isSuccess()).isTrue();

        // Then
        return response.getBody();
    }

    private Table<EndpointUsageAggregate> callEndpointUsageAggregated(EndpointTableRequest request) throws ParseException, JOSEException {


        HttpResponse<Table<EndpointUsageAggregate>> response = Unirest.post("http://metrics-services:" + port + "/api/kpi/endpoint-usage/aggregated")
            .header("Content-Type", "application/json")
            .body(request)
            .asObject(new GenericType<Table<EndpointUsageAggregate>>() {});

        assertThat(response.isSuccess()).isTrue();

        return response.getBody();
    }

    private EndpointStatisticKPI callEndpointStatistic(String endpoint, DateTime from, DateTime to) throws ParseException, JOSEException {

        HttpResponse<EndpointStatisticKPI> response = Unirest.get("http://metrics-services:" + port + "/api/kpi/endpoint-usage/statistic")
                .header("Content-Type", "application/json")
                .queryString("application", ApplicationType.JWKMS.name())
                .queryString("fromDate", from)
                .queryString("toDate", to)
                .queryString("endpoint", endpoint)
                .asObject(EndpointStatisticKPI.class);

        assertThat(response.isSuccess()).isTrue();

        // Then
        return response.getBody();
    }



    private void callAddEntries(List<EndpointUsageEntry> endpointUsageEntries) throws ParseException, JOSEException {

        HttpResponse body = Unirest.post("http://metrics-services:" + port + "/api/kpi/endpoint-usage/add-entries")
                .header("Content-Type", "application/json")
                .queryString("application", ApplicationType.JWKMS.name())
                .body(endpointUsageEntries)
                .asEmpty();

        assertThat(body.isSuccess()).isTrue();

    }

    private Table<EndpointUsageAggregate> callEndpointUsageRawData(DateTime from, DateTime to) throws ParseException, JOSEException {

        HttpResponse<Table<EndpointUsageAggregate>> response = Unirest.get("http://metrics-services:" + port + "/api/kpi/endpoint-usage/history")
                .header("Content-Type", "application/json")
                .queryString("fromDate", from)
                .queryString("toDate", to)
                .asObject(new GenericType<Table<EndpointUsageAggregate>>() {});

        assertThat(response.isSuccess()).isTrue();

        return response.getBody();
    }

    private static EndpointUsageAggregate aggregate(String endpoint) {
        return EndpointUsageAggregate.builder()
                .count(3L)
                .date(DateTime.now())
                .application("rs-api")
                .endpoint(endpoint)
                .identityId("abc")
                .method("POST")
                .responseStatus("200")
                .responseTimesSum(600l)
                .responseTimesHistory(Arrays.asList(100l, 200l, 300l))
                .tppEntry(TppEntry.builder()
                        .oidcClientId("2")
                        .name("forgerock app")
                        .build())
                .endpointType(OBGroupName.AISP)
                .userType(UserContext.UserType.OIDC_CLIENT)
                .build();
    }


    private static JWTClaimsSet claimSet() {
        return new JWTClaimsSet.Builder().claim(OpenBankingConstants.SSAClaims.SOFTWARE_LOGO_URI, "logo").build();
    }
}