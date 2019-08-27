/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.filters;

import com.forgerock.openbanking.analytics.services.MetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MetricScheduler {

    @Autowired
    private MetricService metricService;

    @Scheduled(fixedDelay = 10000)
    public void exportMetrics() {
        metricService.pushMetrics();
    }

}
