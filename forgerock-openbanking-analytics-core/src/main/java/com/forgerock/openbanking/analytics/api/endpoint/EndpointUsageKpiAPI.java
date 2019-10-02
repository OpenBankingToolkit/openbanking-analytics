/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.api.endpoint;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.forgerock.openbanking.analytics.charts.Table;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageEntry;
import com.forgerock.openbanking.analytics.model.kpi.AggregationMethod;
import com.forgerock.openbanking.analytics.model.kpi.EndpointStatisticKPI;
import com.forgerock.openbanking.analytics.model.kpi.EndpointsUsageAggregation;
import com.forgerock.openbanking.analytics.model.kpi.EndpointsUsageKPI;
import com.forgerock.openbanking.analytics.model.openbanking.OBGroupName;
import com.forgerock.openbanking.exceptions.OBErrorException;
import com.forgerock.openbanking.serialiser.IsoDateTimeDeserializer;
import com.forgerock.openbanking.serialiser.IsoDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    class EndpointUsageKpiRequest {
        public String endpoint;
        @JsonDeserialize(using = IsoDateTimeDeserializer.class)
        @JsonSerialize(using = IsoDateTimeSerializer.class)
        public DateTime from;
        @JsonDeserialize(using = IsoDateTimeDeserializer.class)
        @JsonSerialize(using = IsoDateTimeSerializer.class)
        public DateTime to;
        public EndpointsUsageKPI.DateGranularity dateGranularity;
        public LinkedList<EndpointsUsageAggregation> aggregations;
        public EndpointUsageFiltering filtering;
        public AggregationMethod aggregationMethod;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    class EndpointUsageFiltering {
        public List<String> tpps;
        public List<String> status;
        public List<OBGroupName> obGroupNames;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    class EndpointTableRequest {
        public String endpoint;
        @JsonDeserialize(using = IsoDateTimeDeserializer.class)
        @JsonSerialize(using = IsoDateTimeSerializer.class)
        public DateTime from;
        @JsonDeserialize(using = IsoDateTimeDeserializer.class)
        @JsonSerialize(using = IsoDateTimeSerializer.class)
        public DateTime to;
        public Integer page = DEFAULT_PAGE;
        public Integer size = DEFAULT_SIZE;
        public List<SortOrder> sort = DEFAULT_SORT;
        public List<String> fields;
        public List<EndpointTableFilter> filters = new ArrayList<>();
    }

    Integer DEFAULT_SIZE = 10;
    List<SortOrder> DEFAULT_SORT = Arrays.asList(new SortOrder("count", Sort.Direction.DESC));
    Integer DEFAULT_PAGE = 0;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    class EndpointTableFilter {
        public String field;
        public String regex;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    class SortOrder {
        public String field;
        public Sort.Direction direction;
    }
}
