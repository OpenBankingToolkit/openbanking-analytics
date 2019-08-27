/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.services.openbanking;

import com.forgerock.openbanking.analytics.exception.OBErrorException;
import com.forgerock.openbanking.analytics.exception.OBErrorResponseException;
import com.forgerock.openbanking.analytics.model.error.OBRIErrorResponseCategory;
import com.forgerock.openbanking.analytics.model.openbanking.forgerock.FRPaymentConsent;
import com.forgerock.openbanking.analytics.model.openbanking.v3_0.payment.FRDomesticConsent1;
import com.google.common.base.Strings;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class IdempotencyServiceTest {
    @Test
    public void verifyIdempotencyKeyHeader_empty() {
        // Given
        String xIdempotencyKey = "";

        // When
        boolean valid = IdempotencyService.isIdempotencyKeyHeaderValid(xIdempotencyKey);

        // Then
        assertThat(valid).isFalse();
    }

    @Test
    public void verifyIdempotencyKeyHeader_null() {
        // Given
        String xIdempotencyKey = null;

        // When
        boolean valid =  IdempotencyService.isIdempotencyKeyHeaderValid(xIdempotencyKey);

        // Then
        assertThat(valid).isFalse();
    }

    @Test
    public void verifyIdempotencyKeyHeader_tooLong() {
        // Given
        String xIdempotencyKey = Strings.repeat("x", 41);

        // When
        boolean valid =  IdempotencyService.isIdempotencyKeyHeaderValid(xIdempotencyKey);

        // Then
        assertThat(valid).isFalse();
    }


    @Test
    public void verifyIdempotencyKeyHeader_validMaxLength() {
        // Given
        String xIdempotencyKey = Strings.repeat("x", 40);

        // When
        boolean valid = IdempotencyService.isIdempotencyKeyHeaderValid(xIdempotencyKey);

        // Then
        assertThat(valid).isTrue();
    }

    @Test
    public void verifyIdempotencyKeyHeader_validMinLength() {
        // Given
        String xIdempotencyKey = "x";

        // When
        boolean valid =  IdempotencyService.isIdempotencyKeyHeaderValid(xIdempotencyKey);

        // Then
        assertThat(valid).isTrue();
    }

    @Test
    public void validateIdempotencyRequest_matchingRequest_within24Hours_valid() throws Exception {
        // Given
        String xIdempotencyKey = UUID.randomUUID().toString();
        String requestBody = "SampleRequestBody";
        FRPaymentConsent consent = FRDomesticConsent1.builder().created(DateTime.now().minusHours(23)).build();

        // When
        assertThatCode(() ->
                IdempotencyService.validateIdempotencyRequest(xIdempotencyKey, requestBody, consent, () -> requestBody))

        // Then
        .doesNotThrowAnyException();

    }

    @Test
    public void validateIdempotencyRequest_requestNotMatching_invalid() throws Exception {
        // Given
        String xIdempotencyKey = UUID.randomUUID().toString();
        String requestBody = "SampleRequestBody";
        String differentRequestBody = "ModifiedRequestBody";
        FRPaymentConsent consent = FRDomesticConsent1.builder()
                .created(DateTime.now().minusHours(23))
                .id("PDC_1")
                .build();

        // When
        assertThatThrownBy(() ->
        IdempotencyService.validateIdempotencyRequest(xIdempotencyKey, requestBody, consent, () -> differentRequestBody))

        // Then Exception Throw
        .isExactlyInstanceOf(OBErrorResponseException.class)
        .hasFieldOrPropertyWithValue("status", HttpStatus.UNAUTHORIZED)
        .hasFieldOrProperty("category")
        .extracting("category").containsExactly(OBRIErrorResponseCategory.REQUEST_INVALID);
    }

    @Test
    public void validateIdempotencyRequest_idempotencyKeyExpired_invalid() throws Exception {
        // Given
        String xIdempotencyKey = UUID.randomUUID().toString();
        String requestBody = "SampleRequestBody";
        String differentRequestBody = "ModifiedRequestBody";
        FRPaymentConsent consent = FRDomesticConsent1.builder()
                .created(DateTime.now().minusHours(24).minusMinutes(1))
                .id("PDC_1")
                .build();

        // When
        assertThatThrownBy(() ->
                IdempotencyService.validateIdempotencyRequest(xIdempotencyKey, requestBody, consent, () -> differentRequestBody))

                // Then Exception Throw
                .isExactlyInstanceOf(OBErrorResponseException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                .hasFieldOrProperty("category")
                .extracting("category").containsExactly(OBRIErrorResponseCategory.REQUEST_INVALID)
           ;
    }
}