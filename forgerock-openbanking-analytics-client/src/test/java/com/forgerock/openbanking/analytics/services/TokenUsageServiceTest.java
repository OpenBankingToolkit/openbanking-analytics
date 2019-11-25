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