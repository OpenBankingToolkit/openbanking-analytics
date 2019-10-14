/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.services;

import brave.Tracer;
import com.forgerock.openbanking.analytics.configuration.MetricsConfiguration;
import com.forgerock.openbanking.analytics.model.entries.TokenUsage;
import com.forgerock.openbanking.model.oidc.AccessTokenResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TokenUsageServiceTest {

    @Spy
    private MetricsConfiguration metricsConfiguration = new MetricsConfiguration() {{
        rootEndpoint = "metrics-service";
    }};

    @Mock
    private WebClient webClient;
    @Mock
    private Tracer tracer;

    @Spy
    private TokenUsageService tokenUsageService = spy(new TokenUsageService(webClient, metricsConfiguration, tracer) {

        @Override
        public void increment(Collection<TokenUsage> tokenUsages) {
        }
    });

    @Test
    public void shouldIncrementAccessToken() {
        // Given
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        accessTokenResponse.setAccess_token("not null");

        // When
        tokenUsageService.incrementTokenUsage(accessTokenResponse);

        // Then
        verify(tokenUsageService).increment( Collections.singletonList(TokenUsage.ACCESS_TOKEN));
    }

    @Test
    public void shouldIncrementIdToken() {
        // Given
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        accessTokenResponse.setId_token("not null");

        // When
        tokenUsageService.incrementTokenUsage(accessTokenResponse);

        // Then
        verify(tokenUsageService).increment( Collections.singletonList(TokenUsage.ID_TOKEN));
    }

    @Test
    public void shouldIncrementAccessTokenAndIdToken() {
        // Given
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        accessTokenResponse.setAccess_token("not null");
        accessTokenResponse.setId_token("not null");

        // When
        tokenUsageService.incrementTokenUsage(accessTokenResponse);

        // Then
        verify(tokenUsageService).increment(Arrays.asList(TokenUsage.ACCESS_TOKEN, TokenUsage.ID_TOKEN));
    }

    @Test
    public void shouldSuppressServerErrorsWhenIncrementThrowsRestClientException() {
        // Given
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        accessTokenResponse.setAccess_token("not null");
        //given(restTemplate.postForEntity(anyString(), any(), any())).willThrow(RestClientException.class);

        // When
        tokenUsageService.incrementTokenUsage(accessTokenResponse);

        // Then no exception
    }
}