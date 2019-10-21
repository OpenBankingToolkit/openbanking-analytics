/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.api.endpoint;

import com.forgerock.openbanking.analytics.charts.Table;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageEntry;
import com.forgerock.openbanking.analytics.model.kpi.EndpointStatisticKPI;
import com.forgerock.openbanking.analytics.model.kpi.EndpointTableRequest;
import com.forgerock.openbanking.analytics.model.kpi.EndpointUsageKpiRequest;
import com.forgerock.openbanking.analytics.model.kpi.EndpointsUsageKPI;
import com.forgerock.openbanking.exceptions.OBErrorException;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@RequestMapping("/api/kpi/endpoint-usage")
public interface EndpointUsageKpiAPI {

    @RequestMapping(value = "/add-entry", method = RequestMethod.POST)
    ResponseEntity addEntry(@RequestBody EndpointUsageEntry endpointUsageEntry);

    @RequestMapping(value = "/add-entries", method = RequestMethod.POST)
    ResponseEntity addEntries(@RequestBody List<EndpointUsageEntry> endpointUsageEntries);

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    ResponseEntity getAllHistory(
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "page", defaultValue = "0") int page
    );

    @RequestMapping(value = "/history/csv", method = RequestMethod.GET)
    void getAllHistoryAsCSV(
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime,
            HttpServletResponse response
    ) throws IOException;

    @RequestMapping(value = "/history", method = RequestMethod.DELETE)
    ResponseEntity deleteAllHistory(
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime
    );

    @RequestMapping(value = "/aggregated", method = RequestMethod.POST)
    ResponseEntity<Table<EndpointUsageAggregate>> aggregatedEndpoints(
            @RequestBody EndpointTableRequest request
    ) throws OBErrorException;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    ResponseEntity<EndpointsUsageKPI> getKpi(
            @RequestBody EndpointUsageKpiRequest request
    );

    @RequestMapping(value = "/statistic", method = RequestMethod.GET)
    ResponseEntity<EndpointStatisticKPI> getEndpointStat(
            @RequestParam(value = "endpoint") String endpoint,
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime
    );


}
