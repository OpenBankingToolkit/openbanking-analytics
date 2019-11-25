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
package com.forgerock.openbanking.analytics.model.kpi;

import com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate;

import java.util.List;

public enum AggregationMethod {

    BY_NB_CALLS {
        @Override
        public AggregationContext updateContext(Long previousValue, AggregationContext context, EndpointUsageAggregate entry) {
            if (context == null) {
                context = new NbCallsContext();
            }
            NbCallsContext nbCallsContext = (NbCallsContext) context;
            long value = entry.getCount() != null ? entry.getCount() : 0l;
            nbCallsContext.incrementValue(value);
            return context;
        }

        @Override
        public Long getValue(AggregationContext context) {
            if (context == null) return 0l;
            return ((NbCallsContext) context).getNbCalls();
        }

        @Override
        public Unity getUnity() {
            return Unity.NB_REQUESTS;
        }
    },
    BY_RESPONSE_TIME_AVERAGE {
        @Override
        public AggregationContext updateContext(Long previousValue, AggregationContext context, EndpointUsageAggregate entry) {
            return getAverage(context, entry.getCount(), entry.getResponseTimesSum());
        }

        @Override
        public Long getValue(AggregationContext context) {
            if (context == null) return 0l;
            return Math.round(((ResponseTimeAverageContext) context).getAverage());
        }

        @Override
        public Unity getUnity() {
            return Unity.RESPONSE_TIME_MS;
        }
    },
    BY_RESPONSE_TIME_BY_MB_AVERAGE {
        @Override
        public AggregationContext updateContext(Long previousValue, AggregationContext context, EndpointUsageAggregate entry) {
            return getAverage(context, entry.getCount(), entry.getResponseTimesByKbSum());
        }

        @Override
        public Long getValue(AggregationContext context) {
            if (context == null) return 0l;
            return Math.round(((ResponseTimeAverageContext) context).getAverage());
        }

        @Override
        public Unity getUnity() {
            return Unity.RESPONSE_TIME_MS;
        }
    },
    BY_RESPONSE_TIME_PERCENTILE_85 {
        @Override
        public AggregationContext updateContext(Long previousValue, AggregationContext context, EndpointUsageAggregate entry) {
            return AggregationMethod.updatePercentileContext((ResponseTimePercentilContext) context, entry.getResponseTimesHistory());
        }

        @Override
        public Long getValue(AggregationContext context) {
            if (context == null) return 0l;
            return ((ResponseTimePercentilContext) context).getPercentile(85);
        }

        @Override
        public Unity getUnity() {
            return Unity.RESPONSE_TIME_MS;
        }
    },
    BY_RESPONSE_TIME_BY_MB_PERCENTILE_85 {

        @Override
        public AggregationContext updateContext(Long previousValue, AggregationContext context, EndpointUsageAggregate entry) {
            return AggregationMethod.updatePercentileContext((ResponseTimePercentilContext) context, entry.getResponseTimesByKbHistory());
        }

        @Override
        public Long getValue(AggregationContext context) {
            if (context == null) return 0l;
            return ((ResponseTimePercentilContext) context).getPercentile(85);
        }

        @Override
        public Unity getUnity() {
            return Unity.RESPONSE_TIME_MS;
        }
    },
    BY_RESPONSE_TIME_PERCENTILE_90 {
        @Override
        public AggregationContext updateContext(Long previousValue, AggregationContext context, EndpointUsageAggregate entry) {
            return AggregationMethod.updatePercentileContext((ResponseTimePercentilContext) context, entry.getResponseTimesHistory());
        }

        @Override
        public Long getValue(AggregationContext context) {
            if (context == null) return 0l;
            return ((ResponseTimePercentilContext) context).getPercentile(90);
        }

        @Override
        public Unity getUnity() {
            return Unity.RESPONSE_TIME_MS;
        }
    },
    BY_RESPONSE_TIME_BY_MB_PERCENTILE_90 {
        @Override
        public AggregationContext updateContext(Long previousValue, AggregationContext context, EndpointUsageAggregate entry) {
            return AggregationMethod.updatePercentileContext((ResponseTimePercentilContext) context, entry.getResponseTimesByKbHistory());
        }
        @Override
        public Long getValue(AggregationContext context) {
            if (context == null) return 0l;
            return ((ResponseTimePercentilContext) context).getPercentile(90);
        }

        @Override
        public Unity getUnity() {
            return Unity.RESPONSE_TIME_MS;
        }
    },
    BY_RESPONSE_TIME_PERCENTILE_95 {
        @Override
        public AggregationContext updateContext(Long previousValue, AggregationContext context, EndpointUsageAggregate entry) {
            return AggregationMethod.updatePercentileContext((ResponseTimePercentilContext) context, entry.getResponseTimesHistory());
        }
        @Override
        public Long getValue(AggregationContext context) {
            if (context == null) return 0l;
            return ((ResponseTimePercentilContext) context).getPercentile(95);
        }

        @Override
        public Unity getUnity() {
            return Unity.RESPONSE_TIME_MS;
        }
    },
    BY_RESPONSE_TIME_BY_MB_PERCENTILE_95 {
        @Override
        public AggregationContext updateContext(Long previousValue, AggregationContext context, EndpointUsageAggregate entry) {
            return AggregationMethod.updatePercentileContext((ResponseTimePercentilContext) context, entry.getResponseTimesByKbHistory());
        }
        @Override
        public Long getValue(AggregationContext context) {
            if (context == null) return 0l;
            return ((ResponseTimePercentilContext) context).getPercentile(95);
        }

        @Override
        public Unity getUnity() {
            return Unity.RESPONSE_TIME_MS;
        }
    },
    BY_RESPONSE_TIME_PERCENTILE_99 {
        @Override
        public AggregationContext updateContext(Long previousValue, AggregationContext context, EndpointUsageAggregate entry) {
            return AggregationMethod.updatePercentileContext((ResponseTimePercentilContext) context, entry.getResponseTimesHistory());
        }
        @Override
        public Long getValue(AggregationContext context) {
            if (context == null) return 0l;
            return ((ResponseTimePercentilContext) context).getPercentile(99);
        }

        @Override
        public Unity getUnity() {
            return Unity.RESPONSE_TIME_MS;
        }

    },
    BY_RESPONSE_TIME_BY_MB_PERCENTILE_99 {
        @Override
        public AggregationContext updateContext(Long previousValue, AggregationContext context, EndpointUsageAggregate entry) {
            return AggregationMethod.updatePercentileContext((ResponseTimePercentilContext) context, entry.getResponseTimesByKbHistory());
        }
        @Override
        public Long getValue(AggregationContext context) {
            if (context == null) return 0l;
            return ((ResponseTimePercentilContext) context).getPercentile(99);
        }

        @Override
        public Unity getUnity() {
            return Unity.RESPONSE_TIME_MS;
        }

    },
    BY_RESPONSE_TIME_MEDIAN {
        @Override
        public AggregationContext updateContext(Long previousValue, AggregationContext context, EndpointUsageAggregate entry) {
            return AggregationMethod.updatePercentileContext((ResponseTimePercentilContext) context, entry.getResponseTimesHistory());
        }
        @Override
        public Long getValue(AggregationContext context) {
            if (context == null) return 0l;
            return ((ResponseTimePercentilContext) context).getPercentile(50);
        }

        @Override
        public Unity getUnity() {
            return Unity.RESPONSE_TIME_MS;
        }
    },
    BY_RESPONSE_TIME_BY_MB_MEDIAN {
        @Override
        public AggregationContext updateContext(Long previousValue, AggregationContext context, EndpointUsageAggregate entry) {
            return AggregationMethod.updatePercentileContext((ResponseTimePercentilContext) context, entry.getResponseTimesByKbHistory());
        }
        @Override
        public Long getValue(AggregationContext context) {
            if (context == null) return 0l;
            return ((ResponseTimePercentilContext) context).getPercentile(50);
        }

        @Override
        public Unity getUnity() {
            return Unity.RESPONSE_TIME_MS;
        }
    },
    BY_NB_RESPONSE_TIME {
        @Override
        public AggregationContext updateContext(Long previousValue, AggregationContext context, EndpointUsageAggregate entry) {
            if (context == null) {
                context = new NbCallsContext();
            }
            long nbResponseTimeForEntry = entry.getResponseTimesHistory() != null ? entry.getResponseTimesHistory().size() : 0l;
            NbCallsContext nbCallsContext = (NbCallsContext) context;
            nbCallsContext.incrementValue(nbResponseTimeForEntry);
            return nbCallsContext;
        }

        @Override
        public Long getValue(AggregationContext context) {
            if (context == null) return 0l;
            return ((NbCallsContext) context).getNbCalls();
        }

        @Override
        public AggregationContext updateContext(Long previousValue, AggregationContext context, EndpointUsageAggregate entry, Object x) {
            if (context == null) {
                context = new NbCallsContext();
            }
            NbCallsContext nbCallsContext = (NbCallsContext) context;
            nbCallsContext.incrementValue(1);
            return nbCallsContext;
        }

        @Override
        public Unity getUnity() {
            return Unity.NB_REQUESTS;
        }
    },
    ;

    private static AggregationContext getAverage(AggregationContext context, Long count, Long responseTimesSum) {
        if (context == null) {
            context = ResponseTimeAverageContext.builder()
                    .nbCalls(0l)
                    .average(0.0)
                    .build();
        }

        ResponseTimeAverageContext responseTimeAverageContext = (ResponseTimeAverageContext) context;

        long entryResponseTime = responseTimesSum != null ? responseTimesSum : 0l;
        long entryCount = count != null ? count : 0l;

        responseTimeAverageContext.updateAverage(entryResponseTime, entryCount);
        return responseTimeAverageContext;

    }

    public abstract AggregationContext updateContext(Long previousValue, AggregationContext context, EndpointUsageAggregate entry);

    public abstract Long getValue(AggregationContext context);

    public abstract Unity getUnity();


    public AggregationContext updateContext(Long previousValue, AggregationContext context, EndpointUsageAggregate entry, Object x) {
        return updateContext(previousValue, context, entry);
    }

    private static ResponseTimePercentilContext updatePercentileContext(ResponseTimePercentilContext context, List<Long> ms) {
        if (context == null) {
            context = new ResponseTimePercentilContext();
        }
        if (ms != null) {
            context.addAll(ms);
        }
        return context;
    }


    public enum Unity {
        RESPONSE_TIME_MS, NB_REQUESTS
    }
}
