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
