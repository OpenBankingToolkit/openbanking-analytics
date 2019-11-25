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
package com.forgerock.openbanking.analytics.api.endpoint;

import com.forgerock.openbanking.analytics.charts.Table;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageEntry;
import com.forgerock.openbanking.analytics.model.kpi.*;
import com.forgerock.openbanking.analytics.repository.EndpointUsageAggregateRepository;
import com.forgerock.openbanking.exceptions.OBErrorException;
import com.forgerock.openbanking.model.error.OBRIErrorType;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.forgerock.openbanking.analytics.model.kpi.EndpointsUsageAggregation.formatterByGranularity;

@Slf4j
@RestController
public class EndpointUsageKpiAPIController implements EndpointUsageKpiAPI {

    public static final String ALL = "ALL";

    private EndpointUsageEntryEnricher endpointUsageEntryEnricher;
    private EndpointUsageAggregator endpointUsageAggregator;
    private EndpointUsageAggregateRepository endpointUsageAggregateRepository;


    public EndpointUsageKpiAPIController(
                                         EndpointUsageEntryEnricher endpointUsageEntryEnricher,
                                         EndpointUsageAggregator endpointUsageAggregator,
                                         EndpointUsageAggregateRepository endpointUsageAggregateRepository) {
        this.endpointUsageEntryEnricher = endpointUsageEntryEnricher;
        this.endpointUsageAggregator = endpointUsageAggregator;
        this.endpointUsageAggregateRepository = endpointUsageAggregateRepository;
    }

    @Override
    public ResponseEntity addEntry(@RequestBody EndpointUsageEntry endpointUsageEntry) {
        saveEntries(Collections.singletonList(endpointUsageEntry));
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity addEntries(@RequestBody List<EndpointUsageEntry> endpointUsageEntries) {
        saveEntries(endpointUsageEntries);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity getAllHistory(DateTime fromDateTime, DateTime toDateTime, int size, int page) {
        Pageable sortedByDate = PageRequest.of(page, size, Sort.by("date"));

        Page<EndpointUsageAggregate> data = endpointUsageAggregateRepository.findByDateBetween(fromDateTime.minusMinutes(1), toDateTime.plusMinutes(1), sortedByDate);
        return ResponseEntity.ok(Table.<EndpointUsageAggregate>builder()
                .data(data.getContent())
                .totalPages(data.getTotalPages())
                .currentPage(page)
                .totalResults(data.getTotalElements())
                .build());
    }

    @Override
    public void getAllHistoryAsCSV(DateTime fromDateTime, DateTime toDateTime, HttpServletResponse response) throws IOException {

        String csvFileName = "endpointUsages-" +
                fromDateTime.toString(ISODateTimeFormat.dateTime()) + "-" +
                toDateTime.toString(ISODateTimeFormat.dateTime()) + ".csv";
        response.setContentType("text/csv");

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
        response.setHeader(headerKey, headerValue);


        Stream<EndpointUsageAggregate> data = endpointUsageAggregateRepository.findByDateBetween(fromDateTime, toDateTime);
        // uses the Super CSV API to generate CSV data from the model data
        try (ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE)) {

            String[] header = {
                    "identityId", "endpoint", "date", "endpointType", "method", "application", "geoIP", "responseStatus",
                    "userType", "obVersion", "obReference", "tppEntry", "count", "responseTimesSum", "responseTimesHistory",
                    "responseTimesByKbSum", "responseTimesByKbHistory"
            };
            csvWriter.writeHeader(header);

            data.forEach(e -> {
                try {
                    csvWriter.write(e, header);
                } catch (IOException ex) {
                    log.error("Couldn't write entry {} in CSV", e, ex);
                }
            });
        }
    }

    @Override
    public ResponseEntity deleteAllHistory(DateTime fromDateTime, DateTime toDateTime) {
        endpointUsageAggregateRepository.deleteByDateBetween(fromDateTime, toDateTime);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Table<EndpointUsageAggregate>> aggregatedEndpoints(
            @RequestBody EndpointTableRequest request
                                              ) throws OBErrorException {

        log.debug("EndpointTableRequest {}", request);
        if (request.getPage() < 0) {
            log.info("Page number {} cannot be negative", request.getPage());
            throw new OBErrorException(OBRIErrorType.INVALID_PAGE_NUMBER);
        }

        return ResponseEntity.ok(endpointUsageAggregator.getAggregation(request));
    }

    @Override
    public ResponseEntity<EndpointStatisticKPI> getEndpointStat(String endpoint, DateTime fromDateTime, DateTime toDateTime) {
        EndpointStatisticKPI endpointStatisticKPI = new EndpointStatisticKPI();
        endpointStatisticKPI.setEndpointName(endpoint);

        Stream<EndpointUsageAggregate> byDateBetween = endpointUsageAggregateRepository.findByDateBetween(fromDateTime.minusMinutes(1), toDateTime.plusMinutes(1));
        byDateBetween.forEach(e -> endpointStatisticKPI.incrementCounters(e));

        return ResponseEntity.ok(endpointStatisticKPI);
    }


    @Override
    @Cacheable(value="EndpointUsageKpiRequest", condition="#request.isCacheable()")
    public ResponseEntity<EndpointsUsageKPI> getKpi(@RequestBody EndpointUsageKpiRequest request) {

        log.debug("KPI ennpoint usage request : {}", request);
        EndpointsUsageKPI.DateGranularity dateGranularity = getDateGranularity(request);
        log.debug("Date granularity for this request: {}", dateGranularity);

        DateTimeFormatter dateGranularityFormatter = formatterByGranularity.get(dateGranularity);

        // Round the dates definition set
        DateTime fromRounded = roundDate(request.from, dateGranularity);
        DateTime toRounded = roundDate(request.to.plusMinutes(1), dateGranularity);
        log.debug("Date from {} and to {} rounded", fromRounded, toRounded);


        if (request.getAggregations().size() > 2) {
            throw new IllegalArgumentException("Can't graph more than 2 aggrega");
        }

        log.debug("Compute definition set");
        Map<EndpointsUsageAggregation, List> definitions = getSetDefinition(request, fromRounded, toRounded, dateGranularity, request.aggregations);


        log.debug("Initialisation of the lines based on the definitions set");
        Map<Object, EndpointsUsageKPI.Line> lines = initiateLines(request, definitions);

        long tStart = System.currentTimeMillis();

        log.debug("Get entries stream");
        Stream<EndpointUsageAggregate> entries = getEndpointUsageAggregateEntries(request).parallel();
        log.debug("KB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024);

        // Format the data and store them in the dataset
        log.debug("Compute each entry");
        int[] count = new int[1];

        long[] addToDatasetTimer = new long[1];
        entries.forEach(e -> {
            long tStartA = System.currentTimeMillis();
            addToDataset(lines, definitions, e, request, dateGranularityFormatter);
            addToDatasetTimer[0] += System.currentTimeMillis() - tStartA;

            count[0]++;
        });
        log.debug("Computed {} entries", count[0]);

        log.debug("Time {} ms", System.currentTimeMillis() - tStart);
        log.debug("Time addToDataset {} ms", addToDatasetTimer[0]);

        // Compute line with context
        lines.values().forEach(l -> {
            for (int i = 0; i < l.getContext().length; i++) {
                l.getDataset()[i] = l.getAggregationMethod().getValue(l.getContext()[i]);
            }
        });

        //Definition set needs some formatting, to be more user friendly
        formatDefinitionsSet(dateGranularity, definitions);
        log.debug("Definition set now rounded for a better reading on the UI: {}", definitions);
        log.debug("KB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024);

        return ResponseEntity.ok( new EndpointsUsageKPI(request.endpoint, dateGranularity, lines.values(), definitions));
    }

    /**
     * Initialise the lines with zero values
     * @param request our request
     * @param definitions the defintion set
     * @return lines with zero every where
     */
    private Map<Object, EndpointsUsageKPI.Line> initiateLines(@RequestBody EndpointUsageKpiRequest request, Map<EndpointsUsageAggregation, List> definitions) {
        Map<Object, EndpointsUsageKPI.Line> lines = new HashMap<>();

        EndpointsUsageAggregation dimension1 = request.aggregations.get(0);
        if (request.aggregations.size() == 1) {
            lines.put(ALL, EndpointsUsageKPI.Line.builder()
                    .name(ALL)
                    .aggregationMethod(request.getAggregationMethod())
                    .dataset(new long[definitions.get(dimension1).size()])
                    .context(new AggregationContext[definitions.get(dimension1).size()])
                    .build());
        }

        if (request.aggregations.size() == 2) {
            EndpointsUsageAggregation dimension2 = request.aggregations.get(1);
            for (Object dimension2Item : definitions.get(dimension2)) {
                lines.put(dimension2Item, EndpointsUsageKPI.Line.builder()
                        .name(dimension2Item.toString())
                        .aggregationMethod(dimension2.getAggregatorMethod(request.getAggregationMethod(), dimension2Item))
                        .dataset(new long[definitions.get(dimension1).size()])
                        .context(new AggregationContext[definitions.get(dimension1).size()])
                        .build());
            }
        }
        return lines;
    }


    /**
     * Get the date granularity to use.
     * The request can define one or otherwise, we compute a default one based on the period of time requested
     * @param request graph request
     * @return the date granularity to use
     */
    private EndpointsUsageKPI.DateGranularity getDateGranularity(@RequestBody EndpointUsageKpiRequest request) {
        //Initialise the date granularity and format
        EndpointsUsageKPI.DateGranularity dateGranularity;
        if (request.dateGranularity != null) {
            dateGranularity = request.dateGranularity;
        } else {
            dateGranularity = defaultGranularity(request.from, request.to);
        }
        return dateGranularity;
    }

    /**
     * Based on the request dates definition set, we compute the most elegant default granularity
     * @param from The first date we want to draw data
     * @param to the last date we want to draw data
     * @return a granularity adapted for the date set definition
     */
    private EndpointsUsageKPI.DateGranularity defaultGranularity(DateTime from, DateTime to) {
        Interval interv = new Interval(from, to);
        if (interv.toDuration().getStandardDays() > 30 * 12 * 2) {
            return EndpointsUsageKPI.DateGranularity.BY_YEAR;
        }
        if (interv.toDuration().getStandardDays() > 30 * 5) {
            return EndpointsUsageKPI.DateGranularity.BY_MONTH;
        }
        if (interv.toDuration().getStandardDays() > 5) {
            return EndpointsUsageKPI.DateGranularity.BY_DAY;
        }
        return EndpointsUsageKPI.DateGranularity.BY_HOUR;

    }

    /**
     * Dates need to be rounded with the current date granularity.
     * @param dateTime
     * @param dateGranularity
     * @return
     */
    private DateTime roundDate(DateTime dateTime, EndpointsUsageKPI.DateGranularity dateGranularity) {
        switch (dateGranularity) {
            case BY_YEAR:
                dateTime = dateTime.withMonthOfYear(1);
            case BY_MONTH:
                dateTime = dateTime.withDayOfMonth(1);
            case BY_DAY:
                dateTime = dateTime.withHourOfDay(0);
            case BY_HOUR:
                dateTime = dateTime.withMinuteOfHour(0);
                dateTime = dateTime.withSecondOfMinute(0);
                dateTime = dateTime.withMillisOfSecond(0);
                break;
        }
        return dateTime;
    }

    public void saveEntries(List<EndpointUsageEntry> endpointUsageEntries) {
        endpointUsageEntries.forEach(endpointUsageEntryEnricher::enrich);
        endpointUsageAggregator.aggregate(endpointUsageEntries);
    }


    private Map<EndpointsUsageAggregation, List> getSetDefinition(EndpointUsageKpiRequest request, DateTime from, DateTime to, EndpointsUsageKPI.DateGranularity dateGranularityFormat, List<EndpointsUsageAggregation> filters) {
        Map<EndpointsUsageAggregation, List> setOfDefinition = new HashMap<>();

        // We do the first dimension
        for(EndpointsUsageAggregation filter : filters) {
            setOfDefinition.put(filter, filter.getSetDefinition(request, to, dateGranularityFormat, (f, t, field) -> endpointUsageAggregateRepository.getSetDefinition(request, f, t, field), from, endpointUsageAggregateRepository));
        }
        return setOfDefinition;
    }

    private void addToDataset(Map<Object, EndpointsUsageKPI.Line> lines,
                              Map<EndpointsUsageAggregation, List> definitions,
                              EndpointUsageAggregate entry,
                              EndpointUsageKpiRequest request,
                              DateTimeFormatter dateGranularityFormatter) {

        //Skip the entry if it can't contribute for the dimension 1 of the graph
        EndpointsUsageAggregation dimension1 = request.aggregations.get(0);
        if (dimension1.skipEntry(entry, dateGranularityFormatter, definitions.get(dimension1))) {
            return;
        }

        //Filter the lines that this entry can contribute on
        List<EndpointsUsageKPI.Line> linesForCurrentEntry;
        if (request.aggregations.size() == 1) {
            linesForCurrentEntry = lines.values().stream().collect(Collectors.toList());
        } else {
            EndpointsUsageAggregation dimension2 = request.aggregations.get(1);
            List linesIndexes = dimension2.getValuesFromEntry(entry, dateGranularityFormatter, definitions.get(dimension2));
            linesForCurrentEntry = lines.entrySet().stream()
                    .filter(e -> linesIndexes.contains(e.getKey()))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        }

        //For each line
        for(EndpointsUsageKPI.Line line: linesForCurrentEntry) {
            List<Object> xs = dimension1.getValuesFromEntry(entry, dateGranularityFormatter, definitions.get(dimension1));
            //For all the x coordinates
            for (Object x : xs) {
                updateLineForX(entry, x, line, definitions.get(dimension1));
            }
        }
    }

    private void updateLineForX(EndpointUsageAggregate entry, Object x, EndpointsUsageKPI.Line line, List xDefinitionSet) {
        int xIndex = xDefinitionSet.indexOf(x);
        if (xIndex == -1) {
            log.error("Couldn't find the element {} in the set of definition {}", x, xDefinitionSet);
            return;
        }
        if (line.getDataset().length <= xIndex) {
            log.error("Couldn't update element {} as the line dataset size is {}", x, line.getDataset().length);
            return;
        }
        if (line.getContext().length <= xIndex) {
            log.error("Couldn't update element {} as the line context size is {}", x, line.getContext().length);
            return;
        }
        AggregationContext context = line.getAggregationMethod().updateContext(
                line.getDataset()[xIndex],
                line.getContext()[xIndex],
                entry,
                x
        );

        line.getContext()[xIndex] = context;
    }


    /**
     * Request the database for the endpoint usage entries, based on the graph we want to do
     * @param request
     * @return
     */
    private Stream<EndpointUsageAggregate> getEndpointUsageAggregateEntries(@RequestBody EndpointUsageKpiRequest request) {
        // Fetch the data to graph based on the filter

        if (request.filtering != null && request.filtering.obGroupNames != null) {

            if (request.filtering.status != null && request.filtering.tpps != null) {
                return endpointUsageAggregateRepository.findByEndpointTypeInAndIdentityIdInAndResponseStatusInAndDateBetween(request.filtering.obGroupNames, request.filtering.tpps, request.filtering.status, request.from.minusMinutes(1), request.to.plusMinutes(1));
            }

            if (request.filtering.status != null) {
                return endpointUsageAggregateRepository.findByEndpointTypeInAndResponseStatusInAndDateBetween(request.filtering.obGroupNames, request.filtering.status, request.from.minusMinutes(1), request.to.plusMinutes(1));
            }

            if (request.filtering.tpps != null) {
                return endpointUsageAggregateRepository.findByEndpointTypeInAndIdentityIdInAndDateBetween(request.filtering.obGroupNames, request.filtering.tpps, request.from.minusMinutes(1), request.to.plusMinutes(1));
            }

            return endpointUsageAggregateRepository.findByEndpointTypeInAndDateBetween(request.filtering.obGroupNames, request.from.minusMinutes(1), request.to.plusMinutes(1));
        }


        if (request.endpoint == null) {
            return endpointUsageAggregateRepository.findByDateBetween(request.from.minusMinutes(1), request.to.plusMinutes(1));
        }

        if (request.filtering == null) {
            return endpointUsageAggregateRepository.findByEndpointAndDateBetween(request.endpoint, request.from.minusMinutes(1), request.to.plusMinutes(1));
        }

        if (request.filtering.status != null && request.filtering.tpps != null) {
            return endpointUsageAggregateRepository.findByEndpointAndIdentityIdInAndResponseStatusInAndDateBetween(request.endpoint, request.filtering.tpps, request.filtering.status, request.from.minusMinutes(1), request.to.plusMinutes(1));
        }

        if (request.filtering.status != null) {
            return endpointUsageAggregateRepository.findByEndpointAndResponseStatusInAndDateBetween(request.endpoint, request.filtering.status, request.from.minusMinutes(1), request.to.plusMinutes(1));
        }

        if (request.filtering.tpps != null) {
            return endpointUsageAggregateRepository.findByEndpointAndIdentityIdInAndDateBetween(request.endpoint, request.filtering.tpps, request.from.minusMinutes(1), request.to.plusMinutes(1));
        }

        return endpointUsageAggregateRepository.findByDateBetween(request.from.minusMinutes(1), request.to.plusMinutes(1));
    }

    /**
     * The definition set, which is the x axe for example, needs to be formatted for the end user
     * @param dateGranularity
     * @param definition
     */
    private void formatDefinitionsSet(EndpointsUsageKPI.DateGranularity dateGranularity, Map<EndpointsUsageAggregation, List> definition) {
        log.debug("Format definition set");
        //Format definition
        for (Map.Entry<EndpointsUsageAggregation, List> entry: definition.entrySet()) {
            definition.put(entry.getKey(), entry.getKey().formatDefinitionsSet(dateGranularity, entry.getValue()));
        }
    }


}
