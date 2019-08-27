/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.services;

import brave.Tracer;
import com.forgerock.openbanking.analytics.configuration.applications.MetricsConfiguration;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageEntry;
import com.forgerock.openbanking.analytics.model.entries.JwtsGenerationEntry;
import com.forgerock.openbanking.analytics.model.entries.JwtsValidationEntry;
import com.forgerock.openbanking.analytics.utils.MetricUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class MetricService {
    private final static Logger LOGGER = LoggerFactory.getLogger(MetricService.class);
    private static final int MAX_ERROR_METRICS = 10;

    private List<EndpointUsageEntry> endpointUsageMetric;
    private List<JwtsGenerationEntry> jwtsGenerationMetric;
    private List<JwtsValidationEntry> jwtsValidationMetric;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MetricsConfiguration metricsConfiguration;
    @Autowired
    private Tracer tracer;

    private int errorCounter = 0;

    public MetricService() {
        endpointUsageMetric = new CopyOnWriteArrayList<>();
        jwtsGenerationMetric = new CopyOnWriteArrayList<>();
        jwtsValidationMetric = new CopyOnWriteArrayList<>();
    }

    public void addEndpointUsageEntry(EndpointUsageEntry endpointUsageEntry) {
        if (!MetricUtils.isRequestEnabledForAnalytics(tracer)) {
            log.debug("Request excluded for analytics");
            return;
        }
        log.trace("Add to endpoint usage list");
        endpointUsageMetric.add(endpointUsageEntry);
        log.trace("Entry added to endpoint usage list");

    }

    public void addJwtsGenerationEntry(JwtsGenerationEntry jwtsGenerationEntry) {
        if (!MetricUtils.isRequestEnabledForAnalytics(tracer)) {
            log.debug("Request excluded for analytics");
            return;
        }
        log.trace("Add to jwts generation list");
        jwtsGenerationMetric.add(jwtsGenerationEntry);
        log.trace("Entry added to jwts generation list");
    }

    public void addJwtsValidationEntry(JwtsValidationEntry jwtsValidationEntry) {
        if (!MetricUtils.isRequestEnabledForAnalytics(tracer)) {
            log.debug("Request excluded for analytics");
            return;
        }
        log.trace("Add to validation list");
        jwtsValidationMetric.add(jwtsValidationEntry);
        log.trace("Entry added to validation list");
    }

    public void pushMetrics() {
        try {
            if (endpointUsageMetric.size() > 0) {
                HttpEntity<List<EndpointUsageEntry>> request = new HttpEntity<>(new ArrayList<>(endpointUsageMetric), new HttpHeaders());
                restTemplate.exchange(metricsConfiguration.endpointUsageAddEntries, HttpMethod.POST, request, Void.class);
                endpointUsageMetric = new CopyOnWriteArrayList<>();
            }

            if (jwtsGenerationMetric.size() > 0) {
                HttpEntity<List<JwtsGenerationEntry>> request = new HttpEntity<>(new ArrayList<>(jwtsGenerationMetric), new HttpHeaders());
                restTemplate.exchange(metricsConfiguration.jwtsGenerationAddEntries, HttpMethod.POST, request, Void.class);
                jwtsGenerationMetric = new CopyOnWriteArrayList<>();
            }

            if (jwtsValidationMetric.size() > 0) {
                HttpEntity<List<JwtsValidationEntry>> request = new HttpEntity<>(new ArrayList<>(jwtsValidationMetric), new HttpHeaders());
                restTemplate.exchange(metricsConfiguration.jwtsValidationAddEntries, HttpMethod.POST, request, Void.class);
                jwtsValidationMetric = new CopyOnWriteArrayList<>();
            }
            this.errorCounter = 0;
        } catch (Exception e) {
            this.errorCounter++;
            LOGGER.warn("Can't flush to metrics service", e);
            if (MAX_ERROR_METRICS <= this.errorCounter) {
                LOGGER.warn("Metrics service was off for too long, we had {} errors in a row => we flush our metrics buffer to the bin", this.errorCounter, e);
                endpointUsageMetric = new CopyOnWriteArrayList<>();
                jwtsGenerationMetric = new CopyOnWriteArrayList<>();
                jwtsValidationMetric = new CopyOnWriteArrayList<>();
            }
        }
    }

    public List<EndpointUsageEntry> getEndpointUsageMetric() {
        return Collections.unmodifiableList(endpointUsageMetric);
    }

    public List<JwtsGenerationEntry> getJwtsGenerationMetric() {
        return Collections.unmodifiableList(jwtsGenerationMetric);
    }

    public List<JwtsValidationEntry> getJwtsValidationMetric() {
        return Collections.unmodifiableList(jwtsValidationMetric);
    }
}
