/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.api.callback;

import com.forgerock.openbanking.analytics.charts.Counter;
import com.forgerock.openbanking.analytics.charts.Donut;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate;
import com.forgerock.openbanking.analytics.model.entries.callback.CallBackCounterEntry;
import com.forgerock.openbanking.analytics.model.entries.callback.CallBackResponseStatus;
import com.forgerock.openbanking.analytics.model.openbanking.OBReference;
import com.forgerock.openbanking.analytics.repository.CallBackCounterEntryRepository;
import com.forgerock.openbanking.analytics.repository.EndpointUsageAggregateRepository;
import com.google.common.collect.ImmutableList;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Api(tags = "Callback", description = "Callbacks KPIs related to the Open Banking UK Event notification APIs")
@RequestMapping("/api/kpi/callbacks")
@RestController
public class CallBackController {

    private CallBackCounterEntryRepository callBackCounterEntryRepository;
    private EndpointUsageAggregateRepository endpointUsageAggregateRepository;

    public CallBackController(CallBackCounterEntryRepository callBackCounterEntryRepository,
                              EndpointUsageAggregateRepository endpointUsageAggregateRepository) {
        this.callBackCounterEntryRepository = callBackCounterEntryRepository;
        this.endpointUsageAggregateRepository = endpointUsageAggregateRepository;
    }

    @ApiOperation(
            value = "Push callback entries metrics",
            tags={ "PUSH" })
    @ApiResponses(value = {
            @ApiResponse(
                    code = 202,
                    message = "Callback entries added with success"
            )
    })
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity addCallBackEntries(@RequestBody List<CallBackCounterEntry> callBackCounterEntries) {
        log.debug("Add callback entries in database: {}", callBackCounterEntries);
        callBackCounterEntryRepository.saveAll(callBackCounterEntries);
        return ResponseEntity.accepted().build();
    }

    @ApiOperation(
            value = "Read callbacks by TPP response status KPI",
            notes = "When the ASPSP callback the TPP, the response from the TPP may not always be a success. This" +
                    "KPI allows you to get the repartition of callbacks by TPP response code.",
            tags={ "PUSH" })
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "Callbacks by TPP response status KPI, in a donut format",
                    response = Donut.class
            )
    })
    @RequestMapping(value = "/byResponseStatus", method = RequestMethod.GET)
    public ResponseEntity<Donut> getByResponseStatusCounters(
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime
    ) {
        log.debug("Getting CallBack counter by response status from {} to {}", fromDateTime, toDateTime);
        Map<CallBackResponseStatus, Long> counters = callBackCounterEntryRepository.findByDateBetween(fromDateTime, toDateTime)
                .stream()
                .collect(Collectors.groupingBy(CallBackCounterEntry::getCallBackResponseStatus,
                        Collectors.counting()));
       return ResponseEntity.ok(Donut.convertCountersToDonus(counters));
    }

    @ApiOperation(
            value = "Read number of callbacks created KPI",
            notes = "TPPs register callbacks to the ASPSP. This KPI correspond of the number of callbacks registered",
            tags={ "PUSH" })
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "Number of callbacks created.",
                    response = Counter.class
            )
    })
    @RequestMapping(value = "/created", method = RequestMethod.GET)
    public ResponseEntity<Counter> createdCallBackCounter(
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime
    ) {
        log.debug("Count number of callback created from {} to {}", fromDateTime, toDateTime);
        long count = endpointUsageAggregateRepository.findByObReferenceInAndResponseStatusInAndDateBetween(
                ImmutableList.of(OBReference.CREATE_CALLBACK_URL),
                ImmutableList.of("201"),
                fromDateTime,
                toDateTime).mapToLong(EndpointUsageAggregate::getCount).sum();

        return ResponseEntity.ok(Counter.builder()
                .counter(count)
                .type(OBReference.CREATE_CALLBACK_URL.getReference())
                .build());
    }
}
