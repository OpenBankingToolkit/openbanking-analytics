/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.utils;

import brave.Tracer;
import brave.internal.PredefinedPropagationFields;

public class MetricUtils {
    public final static String ANALYTICS_ENABLED_HEADER_NAME = "x-obri-analytics-enabled";

    public static boolean isRequestEnabledForAnalytics(Tracer tracer) {
        Boolean isAnalyticsEnabled = true;

        if (tracer != null
                && tracer.currentSpan() != null
                && tracer.currentSpan().context().extra().size() > 0) {
            PredefinedPropagationFields predefinedPropagationFields = (PredefinedPropagationFields) tracer.currentSpan().context().extra().get(0);
            if ("false".equals(predefinedPropagationFields.get(ANALYTICS_ENABLED_HEADER_NAME))) {
                isAnalyticsEnabled = false;
            }
        }
        return isAnalyticsEnabled;
    }

}
