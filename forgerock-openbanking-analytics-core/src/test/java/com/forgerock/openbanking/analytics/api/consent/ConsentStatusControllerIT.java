/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.api.consent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forgerock.openbanking.analytics.charts.Donut;
import com.forgerock.openbanking.analytics.model.IntentType;
import com.forgerock.openbanking.analytics.model.entries.ConsentStatusEntry;
import com.forgerock.openbanking.analytics.model.openbanking.OBGroupName;
import com.forgerock.openbanking.analytics.models.ConsentActivities;
import com.forgerock.openbanking.analytics.models.ConsentTypeCounter;
import com.forgerock.openbanking.analytics.repository.CallBackCounterEntryRepository;
import com.forgerock.openbanking.analytics.repository.ConsentStatusEntryRepository;
import com.nimbusds.jose.JOSEException;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.Unirest;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConsentStatusControllerIT {

    @LocalServerPort
    private int port;
    @Value("${session.issuer-id}")
    private String issuerId;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CallBackCounterEntryRepository callBackCounterEntryRepository;
    @Autowired
    private ConsentStatusEntryRepository consentStatusEntryRepository;

    private String jws;

    @Before
    public void setUp() throws JOSEException, ParseException {
        Unirest.config().setObjectMapper(new JacksonObjectMapper(objectMapper)).verifySsl(false);
        consentStatusEntryRepository.deleteAll();
    }

    @Test
    public void shouldCreateConsentStatusEntry() {
        // Given
        ConsentStatusEntry consentStatusEntry = ConsentStatusEntry.builder().consentId("1").consentStatus("awaiting").consentType(IntentType.ACCOUNT_ACCESS_CONSENT).date(DateTime.now().withMillisOfSecond(0)).build();

        // When
        HttpResponse response = Unirest.post("http://metrics-services:" + port + "/api/kpi/consent")
                .body(consentStatusEntry)
                .header("Cookie", "obri-session=" + jws)
                .header("Content-Type", "application/json")
                .asEmpty();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.ACCEPTED.value());
        assertThat(consentStatusEntryRepository.findAll()).containsOnly(consentStatusEntry);
    }

    @Test
    public void donutShouldHaveTwoSectionsWhenTwoConsentAwaitingOneConsentCompletedAndTypeIsAccountAccessConsent() {
        // Given
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("1").consentStatus("awaiting").consentType(IntentType.ACCOUNT_ACCESS_CONSENT).date(DateTime.now().minusDays(1)).build());
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("2").consentStatus("awaiting").consentType(IntentType.ACCOUNT_ACCESS_CONSENT).date(DateTime.now().minusDays(1)).build());
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("3").consentStatus("awaiting").consentType(IntentType.ACCOUNT_ACCESS_CONSENT).date(DateTime.now().minusDays(1)).build());
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("3").consentStatus("completed").consentType(IntentType.ACCOUNT_ACCESS_CONSENT).date(DateTime.now()).build());

        // When
        HttpResponse<Donut<String>> response = Unirest.get("http://metrics-services:" + port + "/api/kpi/consent/activities")
                .queryString("fromDate", DateTime.now().minusDays(2).toString())
                .queryString("toDate", DateTime.now().plusDays(1).toString())
                .queryString("consentType", IntentType.ACCOUNT_ACCESS_CONSENT.name())
                .header("Cookie", "obri-session=" + jws)
                .header("Content-Type", "application/json")
                .asObject(new GenericType<Donut<String>>() {});

        // Then
        assertThat(response.getBody().getData()).containsOnly(
                Donut.Section.<String>builder().label("awaiting").value(2L).build(),
                Donut.Section.<String>builder().label("completed").value(1L).build()
        );
    }

    @Test
    public void donutShouldExcludePaymentFileConsentWhenFilteringAccountAccess() {
        // Given
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("1").consentStatus("awaiting").consentType(IntentType.ACCOUNT_ACCESS_CONSENT).date(DateTime.now().minusDays(1)).build());
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("2").consentStatus("awaiting").consentType(IntentType.ACCOUNT_ACCESS_CONSENT).date(DateTime.now().minusDays(1)).build());
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("3").consentStatus("awaiting").consentType(IntentType.PAYMENT_FILE_CONSENT).date(DateTime.now().minusDays(1)).build());
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("3").consentStatus("completed").consentType(IntentType.PAYMENT_FILE_CONSENT).date(DateTime.now()).build());

        // When
        HttpResponse<Donut<String>> response = Unirest.get("http://metrics-services:" + port + "/api/kpi/consent/activities")
                .queryString("fromDate", DateTime.now().minusDays(2).toString())
                .queryString("toDate", DateTime.now().plusDays(1).toString())
                .queryString("consentType", IntentType.ACCOUNT_ACCESS_CONSENT.name())
                .header("Cookie", "obri-session=" + jws)
                .header("Content-Type", "application/json")
                .asObject(new GenericType<Donut<String>>() {});

        // Then
        assertThat(response.getBody().getData()).containsOnly(
                Donut.Section.<String>builder().label("awaiting").value(2L).build()
        );
    }

    @Test
    public void donutShouldHaveTwoSectionsWhenTwoConsentTypeDomesticPaymentOneConsentTypePaymentFile() {
        // Given
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("1").consentStatus("awaiting").consentType(IntentType.PAYMENT_DOMESTIC_CONSENT).date(DateTime.now().minusDays(1)).build());
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("2").consentStatus("awaiting").consentType(IntentType.PAYMENT_DOMESTIC_CONSENT).date(DateTime.now().minusDays(1)).build());
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("3").consentStatus("awaiting").consentType(IntentType.PAYMENT_FILE_CONSENT).date(DateTime.now().minusDays(1)).build());
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("3").consentStatus("complete").consentType(IntentType.PAYMENT_FILE_CONSENT).date(DateTime.now()).build());

        // When
        HttpResponse<Donut<String>> response = Unirest.get("http://metrics-services:" + port + "/api/kpi/consent/type")
                .queryString("fromDate", DateTime.now().minusDays(2).toString())
                .queryString("toDate", DateTime.now().plusDays(1).toString())
                .header("Cookie", "obri-session=" + jws)
                .header("Content-Type", "application/json")
                .asObject(new GenericType<Donut<String>>() {});

        // Then
        assertThat(response.getBody().getData()).containsOnly(
                Donut.Section.<String>builder().label(IntentType.PAYMENT_DOMESTIC_CONSENT.name()).value(2L).build(),
                Donut.Section.<String>builder().label(IntentType.PAYMENT_FILE_CONSENT.name()).value(1L).build()
        );
    }

    @Test
    public void shouldGroupByConsentStatus() {
        // Given
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("1").consentStatus("awaiting").consentType(IntentType.ACCOUNT_ACCESS_CONSENT).date(DateTime.now().minusDays(1)).build());
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("1").consentStatus("completed").consentType(IntentType.ACCOUNT_ACCESS_CONSENT).date(DateTime.now()).build());
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("2").consentStatus("awaiting").consentType(IntentType.ACCOUNT_ACCESS_CONSENT).date(DateTime.now().minusDays(1)).build());
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("2").consentStatus("completed").consentType(IntentType.ACCOUNT_ACCESS_CONSENT).date(DateTime.now()).build());

        // When
        List<ConsentActivities> consentStatusEntries = consentStatusEntryRepository.countActiveConsentStatusBetweenDate(DateTime.now().minusDays(2), DateTime.now().plusDays(1), IntentType.ACCOUNT_ACCESS_CONSENT);

        // Then
        assertThat(consentStatusEntries).containsSequence(ConsentActivities.builder()
                .consentStatus("completed")
                .total(2)
                .build()
        );
    }

    @Test
    public void shouldFilterByOBGroupName() {
        // Given
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("1").consentStatus("awaiting").consentType(IntentType.ACCOUNT_ACCESS_CONSENT).date(DateTime.now().minusMonths(1)).build());
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("1").consentStatus("completed").consentType(IntentType.ACCOUNT_ACCESS_CONSENT).date(DateTime.now()).build());

        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("2").consentStatus("awaiting").consentType(IntentType.ACCOUNT_ACCESS_CONSENT).date(DateTime.now().minusMonths(1)).build());
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("2").consentStatus("completed").consentType(IntentType.ACCOUNT_ACCESS_CONSENT).date(DateTime.now()).build());

        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("3").consentStatus("awaiting").consentType(IntentType.ACCOUNT_ACCESS_CONSENT).date(DateTime.now().minusMonths(3)).build());
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("3").consentStatus("completed").consentType(IntentType.ACCOUNT_ACCESS_CONSENT).date(DateTime.now().minusMonths(1)).build());

        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("4").consentStatus("awaiting").consentType(IntentType.PAYMENT_FILE_CONSENT).date(DateTime.now().minusMonths(1)).build());
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("4").consentStatus("completed").consentType(IntentType.PAYMENT_FILE_CONSENT).date(DateTime.now()).build());

        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("5").consentStatus("awaiting").consentType(IntentType.PAYMENT_FILE_CONSENT).date(DateTime.now().minusMonths(1)).build());
        consentStatusEntryRepository.save(ConsentStatusEntry.builder().consentId("5").consentStatus("completed").consentType(IntentType.PAYMENT_FILE_CONSENT).date(DateTime.now()).build());

        List<ConsentTypeCounter> consentTypeCounters = consentStatusEntryRepository.countConsentsCreatedBetweenDate(OBGroupName.AISP, DateTime.now().minusMonths(2), DateTime.now().minusMonths(1).plusDays(1));

        // Then
        assertThat(consentTypeCounters).containsSequence(ConsentTypeCounter.builder()
                        .consentType(IntentType.ACCOUNT_ACCESS_CONSENT)
                        .total(2)
                        .build()
        );
    }
}