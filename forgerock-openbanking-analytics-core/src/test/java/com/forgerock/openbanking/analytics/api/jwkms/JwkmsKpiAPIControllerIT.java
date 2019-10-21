/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.api.jwkms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forgerock.openbanking.analytics.charts.Donut;
import com.forgerock.openbanking.analytics.model.entries.JwtsGenerationEntry;
import com.forgerock.openbanking.analytics.model.entries.JwtsValidationEntry;
import com.forgerock.openbanking.analytics.models.ApplicationType;
import com.forgerock.openbanking.analytics.repository.JwtsGenerationEntriesRepository;
import com.forgerock.openbanking.analytics.repository.JwtsValidationEntriesRepository;
import com.google.common.collect.ImmutableList;
import com.nimbusds.jose.JOSEException;
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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;

import static com.forgerock.openbanking.analytics.api.jwkms.JWT.jws;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class JwkmsKpiAPIControllerIT {

    @LocalServerPort
    private int port;

    @Value("${session.issuer-id}")
    private String issuerId;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtsGenerationEntriesRepository jwtsGenerationEntriesRepository;
    @Autowired
    private JwtsValidationEntriesRepository jwtsValidationEntriesRepository;



    @Before
    public void setUp() {
        Unirest.config().setObjectMapper(new JacksonObjectMapper(objectMapper)).verifySsl(false);
        jwtsGenerationEntriesRepository.deleteAll();
        jwtsValidationEntriesRepository.deleteAll();
    }

    @After
    public void tearDown() {
        jwtsGenerationEntriesRepository.deleteAll();
        jwtsValidationEntriesRepository.deleteAll();
    }

    @Test
    public void testGeneratedJWTsKPI() throws Exception {
        DateTime now = DateTime.now();
        // Given
        addGenerationJwtEntries(ImmutableList.builder()
                .add(JwtsGenerationEntry.builder()
                        .appId("toto")
                        .date(now)
                        .jwtType(JwtsGenerationEntry.JwtType.JWS)
                        .build())
                .add(JwtsGenerationEntry.builder()
                        .appId("toto")
                        .date(now.withMinuteOfHour(5))
                        .jwtType(JwtsGenerationEntry.JwtType.JWS)
                        .build())
                .add(JwtsGenerationEntry.builder()
                        .appId("toto")
                        .date(now.withMinuteOfHour(15))
                        .jwtType(JwtsGenerationEntry.JwtType.JWS)
                        .build())
                .add(JwtsGenerationEntry.builder()
                        .appId("toto")
                        .date(now.withMinuteOfHour(25))
                        .jwtType(JwtsGenerationEntry.JwtType.JWE_JWS)
                        .build())
                .add(JwtsGenerationEntry.builder()
                        .appId("toto")
                        .date(now.withMinuteOfHour(55))
                        .jwtType(JwtsGenerationEntry.JwtType.JWE_JWS)
                        .build())
                .build());

        // When
        Donut jwtsGenerationKPI = getJwtsGenerationKPI(now.minusDays(10), now.plusDays(10));
        log.debug("jwtsGenerationKPI = {}", jwtsGenerationKPI);

        // Then
        assertThat(jwtsGenerationKPI.getTotal()).isEqualTo(5l);
        assertThat(jwtsGenerationKPI.getData()).containsExactlyInAnyOrder(
                Donut.Section.builder()
                        .label(JwtsGenerationEntry.JwtType.JWE_JWS.name())
                        .value(2l)
                        .build(),
                Donut.Section.builder()
                        .label(JwtsGenerationEntry.JwtType.JWS.name())
                        .value(3l)
                        .build(),
                Donut.Section.builder()
                        .label(JwtsGenerationEntry.JwtType.JWE.name())
                        .value(0l)
                        .build(),
                Donut.Section.builder()
                        .label(JwtsGenerationEntry.JwtType.JWS_JWE.name())
                        .value(0l)
                        .build()
        );
    }

    @Test
    public void testValidationsJWTsKPI() throws Exception {
        DateTime now = DateTime.now();
        // Given
        addJwtsValidationEntries(ImmutableList.builder()
                .add(JwtsValidationEntry.builder()
                        .appId("toto")
                        .date(now)
                        .wasValid(true)
                        .build())
                .add(JwtsValidationEntry.builder()
                        .appId("toto")
                        .date(now.withMinuteOfHour(5))
                        .wasValid(true)
                        .build())
                .add(JwtsValidationEntry.builder()
                        .appId("toto")
                        .date(now.withMinuteOfHour(25))
                        .wasValid(true)
                        .build())
                .add(JwtsValidationEntry.builder()
                        .appId("toto")
                        .date(now.withMinuteOfHour(35))
                        .wasValid(false)
                        .build())
                .add(JwtsValidationEntry.builder()
                        .appId("toto")
                        .date(now.withMinuteOfHour(45))
                        .wasValid(false)
                        .build())
                .build());

        // When
        Donut jwtsValidationKPI = getJwtsValidationKPI(now.minusDays(10), now.plusDays(10));

        // Then
        assertThat(jwtsValidationKPI.getTotal()).isEqualTo(5l);
        assertThat(jwtsValidationKPI.getData()).containsExactlyInAnyOrder(
                Donut.Section.builder()
                        .label(true)
                        .value(3l)
                        .build(),
                Donut.Section.builder()
                        .label(false)
                        .value(2l)
                        .build()
        );
    }

    private Donut getJwtsGenerationKPI(DateTime from, DateTime to) throws ParseException, JOSEException {
        String jws = jws("accounts", "GROUP_FORGEROCK");

        HttpResponse<Donut> response = Unirest.get("http://metrics-services:" + port + "/api/kpi/jwts/jwts-generation/")
                .header("Cookie", "obri-session=" + jws)
                .header("Content-Type", "application/json")
                .queryString("application", ApplicationType.JWKMS.name())
                .queryString("fromDate", from)
                .queryString("toDate", to)
                .asObject(Donut.class);

        // Then
        return response.getBody();
    }

    private void addGenerationJwtEntries(ImmutableList<Object> entries) throws ParseException, JOSEException {
        String jws = jws("accounts", "GROUP_FORGEROCK");

        HttpResponse body = Unirest.post("http://metrics-services:" + port + "/api/kpi/jwts/jwts-generation/add-entries")
                .header("Cookie", "obri-session=" + jws)
                .header("Content-Type", "application/json")
                .queryString("application", ApplicationType.JWKMS.name())
                .body(entries)
                .asEmpty();

        assertThat(body.isSuccess()).isTrue();
    }

    private Donut getJwtsValidationKPI(DateTime from, DateTime to) throws ParseException, JOSEException {
        String jws = jws("accounts", "GROUP_FORGEROCK");

        HttpResponse<Donut> response = Unirest.get("http://metrics-services:" + port + "/api/kpi/jwts/jwts-validation/")
                .header("Cookie", "obri-session=" + jws)
                .header("Content-Type", "application/json")
                .queryString("application", ApplicationType.JWKMS.name())
                .queryString("fromDate", from)
                .queryString("toDate", to)
                .asObject(Donut.class);

        // Then
        return response.getBody();
    }

    private void addJwtsValidationEntries(ImmutableList<Object> entries) throws ParseException, JOSEException {
        String jws = jws("accounts", "GROUP_FORGEROCK");

        HttpResponse body = Unirest.post("http://metrics-services:" + port + "/api/kpi/jwts/jwts-validation/add-entries")
                .header("Cookie", "obri-session=" + jws)
                .header("Content-Type", "application/json")
                .queryString("application", ApplicationType.JWKMS.name())
                .body(entries)
                .asEmpty();

        assertThat(body.isSuccess()).isTrue();
    }
}