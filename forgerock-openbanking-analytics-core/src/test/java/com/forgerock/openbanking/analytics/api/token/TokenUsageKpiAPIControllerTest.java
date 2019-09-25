/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.api.token;

import com.forgerock.openbanking.analytics.charts.Counter;
import com.forgerock.openbanking.analytics.model.entries.TokenUsage;
import com.forgerock.openbanking.analytics.models.TokenUsageEntry;
import com.forgerock.openbanking.analytics.repository.TokenUsageEntryRepository;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class TokenUsageKpiAPIControllerTest {

    @Mock
    private TokenUsageEntryRepository tokenUsageEntryRepository;
    @InjectMocks
    private TokenUsageKpiAPIController tokenUsageKpiAPIController;

    @Test
    public void countGroupingByTokenType() {
        // Given
        given(tokenUsageEntryRepository.findByDayBetween(any(), any())).willReturn(Arrays.asList(
                new TokenUsageEntry(TokenUsage.ACCESS_TOKEN, DateTime.now().minusDays(1), 3L),
                new TokenUsageEntry(TokenUsage.ACCESS_TOKEN, DateTime.now(), 4L),
                new TokenUsageEntry(TokenUsage.ACCESS_TOKEN, DateTime.now().plusDays(1), 3L)));

        // When
        ResponseEntity<Counter> tokenUsage = tokenUsageKpiAPIController.getTokenUsage(TokenUsage.ACCESS_TOKEN, DateTime.now(), DateTime.now());

        // Then
        assertThat(tokenUsage.getBody()).isEqualTo(
                Counter.builder()
                        .type(TokenUsage.ACCESS_TOKEN.name())
                        .counter(10L)
                        .build());
    }
}