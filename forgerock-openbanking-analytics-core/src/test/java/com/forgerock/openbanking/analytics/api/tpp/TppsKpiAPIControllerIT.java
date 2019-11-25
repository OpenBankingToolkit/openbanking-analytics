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
package com.forgerock.openbanking.analytics.api.tpp;

import com.forgerock.openbanking.analytics.charts.BarChart;
import com.forgerock.openbanking.analytics.charts.Counter;
import com.forgerock.openbanking.analytics.charts.Donut;
import com.forgerock.openbanking.analytics.model.entries.TppEntry;
import com.forgerock.openbanking.model.SoftwareStatementRole;
import com.nimbusds.jose.JOSEException;
import kong.unirest.HttpResponse;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.Unirest;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.forgerock.openbanking.analytics.api.jwkms.JWT.jws;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TppsKpiAPIControllerIT {

    @LocalServerPort
    private int port;

    @Value("${session.issuer-id}")
    private String issuerId;


    @Before
    public void setUp() {
        Unirest.config().setObjectMapper(new JacksonObjectMapper()).verifySsl(false);
    }

    @Test
    public void getTppsDirectoriesKpi() throws Exception {
        // Given
        DateTime now = DateTime.now();
        pushTppEntry(Arrays.asList(
                TppEntry.builder()
                        .oidcClientId("1")
                        .types(Collections.singleton(SoftwareStatementRole.AISP))
                        .softwareId("1")
                        .organisationId("1")
                        .created(now)
                        .directoryId("OpenBanking")
                        .build(),
                TppEntry.builder()
                        .oidcClientId("2")
                        .types(Collections.singleton(SoftwareStatementRole.AISP))
                        .softwareId("2")
                        .organisationId("2")
                        .created(now)
                        .directoryId("OpenBanking")
                        .build(),
                TppEntry.builder()
                        .oidcClientId("3")
                        .types(Collections.singleton(SoftwareStatementRole.PISP))
                        .softwareId("3")
                        .organisationId("3")
                        .created(now)
                        .directoryId("ForgeRock")
                        .build()
        ));
        // When
        String jws = jws("accounts", "GROUP_FORGEROCK");
        HttpResponse<Donut> response = Unirest.get("http://metrics-services:" + port + "/api/kpi/tpps/directories/")
                .header("Cookie", "obri-session=" + jws)
                .queryString("fromDate", now.minusDays(1))
                .queryString("toDate", now.plusDays(1))
                .asObject(Donut.class);

        // Then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody().getData())
                .contains(new Donut.Section("OpenBanking", 2l))
                .contains(new Donut.Section("ForgeRock", 1l));
    }

    @Test
    public void getTppsTypesKpi() throws Exception {
        // Given
        DateTime now = DateTime.now();
        pushTppEntry(Arrays.asList(
                TppEntry.builder()
                        .oidcClientId("1")
                        .types(Collections.singleton(SoftwareStatementRole.AISP))
                        .softwareId("1")
                        .organisationId("1")
                        .created(now)
                        .directoryId("OpenBanking")
                        .build(),
                TppEntry.builder()
                        .oidcClientId("2")
                        .types(Collections.singleton(SoftwareStatementRole.AISP))
                        .softwareId("2")
                        .organisationId("2")
                        .created(now)
                        .directoryId("OpenBanking")
                        .build(),
                TppEntry.builder()
                        .oidcClientId("3")
                        .types(Collections.singleton(SoftwareStatementRole.PISP))
                        .softwareId("3")
                        .organisationId("3")
                        .created(now)
                        .directoryId("ForgeRock")
                        .build()
        ));
        // When
        String jws = jws("accounts", "GROUP_FORGEROCK");
        HttpResponse<BarChart> response = Unirest.get("http://metrics-services:" + port + "/api/kpi/tpps/type")
                .header("Cookie", "obri-session=" + jws)
                .queryString("fromDate", now.minusDays(1))
                .queryString("toDate", now.plusDays(1))
                .asObject(BarChart.class);

        // Then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody().getData())
                .contains(new BarChart.Bar("AISP", 2))
                .contains(new BarChart.Bar("PISP", 1));
    }

    @Test
    public void getTppsCountKpi() throws Exception {
        // Given
        DateTime now = DateTime.now();
        pushTppEntry(Arrays.asList(
                TppEntry.builder()
                        .oidcClientId("1")
                        .types(Collections.singleton(SoftwareStatementRole.AISP))
                        .softwareId("1")
                        .organisationId("1")
                        .created(now)
                        .directoryId("OpenBanking")
                        .build(),
                TppEntry.builder()
                        .oidcClientId("2")
                        .types(Collections.singleton(SoftwareStatementRole.AISP))
                        .softwareId("2")
                        .organisationId("2")
                        .created(now)
                        .directoryId("OpenBanking")
                        .build(),
                TppEntry.builder()
                        .oidcClientId("3")
                        .types(Collections.singleton(SoftwareStatementRole.PISP))
                        .softwareId("3")
                        .organisationId("3")
                        .created(now)
                        .directoryId("ForgeRock")
                        .build()
        ));
        // When
        String jws = jws("accounts", "GROUP_FORGEROCK");
        HttpResponse<Counter> response = Unirest.get("http://metrics-services:" + port + "/api/kpi/tpps/count")
                .header("Cookie", "obri-session=" + jws)
                .queryString("fromDate", now.minusDays(1))
                .queryString("toDate", now.plusDays(1))
                .asObject(Counter.class);

        // Then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody().getCounter()).isEqualTo(3l);
    }


    private void pushTppEntry(List<TppEntry> tppEntries) throws JOSEException, ParseException {
        // Given
        String jws = jws("accounts", "GROUP_FORGEROCK");

        // When

        HttpResponse response = Unirest.post("http://metrics-services:" + port + "/api/kpi/tpps/entries")
                .header("Cookie", "obri-session=" + jws)
                .header("Content-Type", "application/json")
                .body(tppEntries)
                .asEmpty();
    }

}