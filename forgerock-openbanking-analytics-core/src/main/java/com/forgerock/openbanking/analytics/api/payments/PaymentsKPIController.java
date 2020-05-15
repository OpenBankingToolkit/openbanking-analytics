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
package com.forgerock.openbanking.analytics.api.payments;

import com.forgerock.openbanking.analytics.charts.Donut;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate;
import com.forgerock.openbanking.analytics.model.openbanking.OBReference;
import com.forgerock.openbanking.analytics.repository.EndpointUsageAggregateRepository;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@PreAuthorize("hasAnyAuthority('GROUP_ANALYTICS')")
@RequestMapping("/api/kpi/payments")
@RestController
public class PaymentsKPIController {

    private EndpointUsageAggregateRepository endpointUsageAggregateRepository;

    public PaymentsKPIController(EndpointUsageAggregateRepository endpointUsageAggregateRepository) {
        this.endpointUsageAggregateRepository = endpointUsageAggregateRepository;
    }

    @RequestMapping(value = "/confirmation-of-fund", method = RequestMethod.GET)
    public ResponseEntity<Donut> paymentConfirmationOfFund(
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime
    ) {
        log.debug("Count number of payment confirmation of funds from {} to {}", fromDateTime, toDateTime);
        Map<OBReference, Long> counters = endpointUsageAggregateRepository.findByObReferenceInAndDateBetween(
                ImmutableList.of(
                        OBReference.GET_DOMESTIC_PAYMENT_CONSENTS_CONSENT_ID_FUNDS_CONFIRMATION,
                        OBReference.GET_INTERNATIONAL_PAYMENT_CONSENTS_CONSENT_ID_FUNDS_CONFIRMATION,
                        OBReference.GET_INTERNATIONAL_SCHEDULED_PAYMENT_CONSENTS_CONSENT_ID_FUNDS_CONFIRMATION
                ),
                fromDateTime,
                toDateTime)
                .collect(Collectors.groupingBy(EndpointUsageAggregate::getObReference,
                        Collectors.counting()));

        return ResponseEntity.ok(Donut.convertCountersToDonus(counters));
    }
}
