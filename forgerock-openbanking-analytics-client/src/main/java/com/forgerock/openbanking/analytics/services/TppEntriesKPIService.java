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
import com.forgerock.openbanking.analytics.model.entries.TppEntry;
import com.forgerock.openbanking.analytics.utils.MetricUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Slf4j
@Service
public class TppEntriesKPIService {

    private final RestTemplate restTemplate;
    private final MetricsConfiguration metricsConfiguration;
    private final Tracer tracer;

    public TppEntriesKPIService(RestTemplate restTemplate, MetricsConfiguration metricsConfiguration, Tracer tracer) {
        this.restTemplate = restTemplate;
        this.metricsConfiguration = metricsConfiguration;
        this.tracer = tracer;
    }

    public void pushTppEntry(TppEntry entry) {
        if (!MetricUtils.isRequestEnabledForAnalytics(tracer)) {
            log.debug("Request excluded for analytics");
            return;
        }
        log.debug("Push tpp entry={}", entry);
        try {
            restTemplate.postForEntity(metricsConfiguration.rootEndpoint + "/api/kpi/tpps/entries", Arrays.asList(entry), Void.class);
        } catch (Exception e) {
            log.warn("Couldn't send Tpp entry", entry, e);
        }
    }
}
