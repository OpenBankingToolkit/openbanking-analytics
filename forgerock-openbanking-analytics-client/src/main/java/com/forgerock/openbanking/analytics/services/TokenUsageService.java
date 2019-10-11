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
import com.forgerock.openbanking.analytics.utils.MetricUtils;
import com.forgerock.openbanking.model.oidc.AccessTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Slf4j
@Service
public class TokenUsageService {

    private final WebClient webClient;
    private final MetricsConfiguration metricsConfiguration;
    private final Tracer tracer;

    public TokenUsageService(WebClient webClient, MetricsConfiguration metricsConfiguration, Tracer tracer) {
        this.webClient = webClient;
        this.metricsConfiguration = metricsConfiguration;
        this.tracer = tracer;
    }

    public void incrementTokenUsage(TokenUsage... tokenUsage) {
        if (!MetricUtils.isRequestEnabledForAnalytics(tracer)) {
            log.debug("Request excluded for analytics");
            return;
        }
        log.debug("Incrementing token usage types={}", Arrays.toString(tokenUsage));
        increment(Arrays.asList(tokenUsage));
    }

    public void incrementTokenUsage(AccessTokenResponse accessTokenResponse) {
        if (!MetricUtils.isRequestEnabledForAnalytics(tracer)) {
            log.debug("Request excluded for analytics");
            return;
        }

        if (accessTokenResponse.getIdToken() == null && accessTokenResponse.getAccess_token() == null) {
            return;
        }

        Collection<TokenUsage> tokenUsages = Collections.emptyList();
        if (accessTokenResponse.getIdToken() != null && accessTokenResponse.getAccess_token() != null) {
            tokenUsages = Arrays.asList(TokenUsage.ACCESS_TOKEN, TokenUsage.ID_TOKEN);
        } else if (accessTokenResponse.getIdToken() != null) {
            tokenUsages = Collections.singletonList(TokenUsage.ID_TOKEN);
        } else if (accessTokenResponse.getAccess_token() != null) {
            tokenUsages = Collections.singletonList(TokenUsage.ACCESS_TOKEN);
        }
        log.debug("Incrementing token usage types={}", tokenUsages);
        increment(tokenUsages);
    }

    public void increment(Collection<TokenUsage> tokenUsages) {
        if (!MetricUtils.isRequestEnabledForAnalytics(tracer)) {
            log.debug("Request excluded for analytics");
            return;
        }

        try {
            webClient
                    .post()
                    .uri(metricsConfiguration.rootEndpoint + "/api/kpi/token-usage")
                    .body(BodyInserters.fromObject(tokenUsages))
                    .retrieve().bodyToMono(String.class)
                    .log()
                    .subscribe(response -> log.debug("Response from metrics: {}", response));
        } catch (RestClientException e) {
            log.warn("Could not update token metrics types={}", tokenUsages, e);
        }
    }
}
