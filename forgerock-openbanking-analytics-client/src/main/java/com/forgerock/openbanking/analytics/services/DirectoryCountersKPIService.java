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
import com.forgerock.openbanking.analytics.model.entries.DirectoryCounterType;
import com.forgerock.openbanking.analytics.utils.MetricUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;

@Slf4j
@Service
public class DirectoryCountersKPIService {

    private final WebClient webClient;
    private final MetricsConfiguration metricsConfiguration;
    private final Tracer tracer;

    public DirectoryCountersKPIService(WebClient webClient, MetricsConfiguration metricsConfiguration, Tracer tracer) {
        this.webClient = webClient;
        this.metricsConfiguration = metricsConfiguration;
        this.tracer = tracer;
    }

    public void incrementTokenUsage(DirectoryCounterType... directoryCounterType) {
        if (!MetricUtils.isRequestEnabledForAnalytics(tracer)) {
            log.debug("Request excluded for analytics");
            return;
        }
        log.debug("Incrementing directory counter types={}", Arrays.toString(directoryCounterType));
        try {
            webClient
                    .post()
                    .uri(metricsConfiguration.rootEndpoint + "/api/kpi/directory/")
                    .body(BodyInserters.fromObject(directoryCounterType))
                    .retrieve().bodyToMono(String.class)
                    .log()
                    .subscribe(response -> log.debug("Response from metrics: {}", response));
        } catch (Exception e) {
            log.warn("Couldn't send directory counters {} KPI to metrics", directoryCounterType, e);
        }
    }
}
