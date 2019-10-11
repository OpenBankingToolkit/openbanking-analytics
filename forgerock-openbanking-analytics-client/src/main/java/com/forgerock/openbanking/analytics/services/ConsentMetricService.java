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
import com.forgerock.openbanking.analytics.model.entries.ConsentStatusEntry;
import com.forgerock.openbanking.analytics.utils.MetricUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class ConsentMetricService {

    private final WebClient webClient;
    private final MetricsConfiguration metricsConfiguration;
    private final Tracer tracer;


    public ConsentMetricService(WebClient webClient, MetricsConfiguration metricsConfiguration, Tracer tracer) {
        this.webClient = webClient;
        this.metricsConfiguration = metricsConfiguration;
        this.tracer = tracer;
    }

    public void sendConsentActivity(ConsentStatusEntry consentStatusEntry) {
        if (!MetricUtils.isRequestEnabledForAnalytics(tracer)) {
            log.debug("Request excluded for analytics");
            return;
        }
        log.debug("Incrementing consent activity: {}", consentStatusEntry);
        try {
            webClient
                    .post()
                    .uri(metricsConfiguration.rootEndpoint + "/api/kpi/consent")
                    .body(BodyInserters.fromObject(consentStatusEntry))
                    .retrieve().bodyToMono(String.class)
                    .log()
                    .subscribe(response -> log.debug("Response from metrics: {}", response));
            ;
        } catch (RestClientException e) {
            log.warn("Could not update consent status metrics type={}", consentStatusEntry, e);
        }
    }
}
