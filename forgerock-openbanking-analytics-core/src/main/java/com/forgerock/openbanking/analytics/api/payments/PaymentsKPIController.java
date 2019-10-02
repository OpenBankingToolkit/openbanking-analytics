/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
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
