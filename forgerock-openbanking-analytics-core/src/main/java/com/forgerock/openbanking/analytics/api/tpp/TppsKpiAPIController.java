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
package com.forgerock.openbanking.analytics.api.tpp;

import com.forgerock.openbanking.analytics.charts.BarChart;
import com.forgerock.openbanking.analytics.charts.Counter;
import com.forgerock.openbanking.analytics.charts.Donut;
import com.forgerock.openbanking.analytics.charts.Table;
import com.forgerock.openbanking.analytics.model.entries.TppEntry;
import com.forgerock.openbanking.analytics.models.TppsActivitiesKPI;
import com.forgerock.openbanking.analytics.models.TppsTypeKPI;
import com.forgerock.openbanking.analytics.repository.TppEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;


@RestController
@RequestMapping("/api/kpi/tpps")

@Slf4j
public class TppsKpiAPIController {

    @Autowired
    private TppEntryRepository tppEntryRepository;

    @RequestMapping(value = "/entries", method = RequestMethod.POST)
    public ResponseEntity postTPPEntries(@RequestBody Collection<TppEntry> tppEntries) {

        tppEntryRepository.saveAll(tppEntries);
        return ResponseEntity.accepted().build();
    }

    @RequestMapping(value = "/entries", method = RequestMethod.GET)
    public ResponseEntity getTPPEntries(
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "page", defaultValue = "0") int page) {

        Pageable sortedByDate = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"created"));

        Page<TppEntry> data = tppEntryRepository.findByCreatedBetweenAndNameRegex(fromDateTime, toDateTime, ".*", sortedByDate);
        return ResponseEntity.ok(Table.<TppEntry>builder()
                .data(data.getContent())
                .totalPages(data.getTotalPages())
                .currentPage(page)
                .totalResults(data.getTotalElements())
                .build());
    }


    @RequestMapping(value = "/entries/csv", method = RequestMethod.GET)
    public void exportEntriesCSV(
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime,
            HttpServletResponse response) throws IOException {

        String csvFileName = "tpp-entries-" +
                fromDateTime.toString(ISODateTimeFormat.dateTime()) + "-" +
                toDateTime.toString(ISODateTimeFormat.dateTime()) + ".csv";
        response.setContentType("text/csv");

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
        response.setHeader(headerKey, headerValue);


        Stream<TppEntry> data = tppEntryRepository.findByCreatedBetweenAndNameRegex(fromDateTime, toDateTime, ".*");
        // uses the Super CSV API to generate CSV data from the model data
        try (ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE)) {

            String[] header = {
                    "oidcClientId", "name", "logoUri", "types", "directoryId",
                    "softwareId", "organisationId", "organisationName",
                    "created", "deleted"
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


    @RequestMapping(value = "/directories", method = RequestMethod.GET)
    public ResponseEntity<Donut> getDirectoryKpi(
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime
    ) {
        log.debug("Getting TPP kpis by directory from {} to {}", fromDateTime, toDateTime);

        TppsActivitiesKPI tppsActivitiesKPI = new TppsActivitiesKPI();
        for (TppEntry tpp : tppEntryRepository.findByCreatedBetween(fromDateTime, toDateTime)) {
            tppsActivitiesKPI.increment(tpp);
        }
        return ResponseEntity.ok(Donut.fromTppDirectory(tppsActivitiesKPI.getDataset()));
    }

    @RequestMapping(value = "/type", method = RequestMethod.GET)
    public ResponseEntity<BarChart> getTypeKPi(
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime
    ) {
        log.debug("Getting TPP kpis by type from {} to {}", fromDateTime, toDateTime);

        TppsTypeKPI tppsTypeKPI = new TppsTypeKPI();
        for (TppEntry tpp : tppEntryRepository.findByCreatedBetween(fromDateTime, toDateTime)) {
            tpp.getTypes().forEach(tppsTypeKPI::increment);
        }
        return ResponseEntity.ok(BarChart.fromTppTypes(tppsTypeKPI.getDataset()));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public ResponseEntity<Counter> count(
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime
    ) {
        log.debug("Getting TPP counter from {} to {}", fromDateTime, toDateTime);

        return ResponseEntity.ok(Counter.builder()
                .counter(tppEntryRepository.findByCreatedBetween(fromDateTime, toDateTime).stream().count())
                .type("All")
                .build());
    }
}
