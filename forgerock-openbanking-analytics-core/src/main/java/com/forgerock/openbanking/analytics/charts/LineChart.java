/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.charts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LineChart {

    private Axis x;
    private Axis y;
    private List<Coordinate> data;

    public static LineChart fromTppRegistration(Axis xAxis, Axis yAxis, Map<LocalDate, Integer> dataset) {
        List<Coordinate> sections = dataset.entrySet()
                .stream()
                .map(Coordinate::fromTppRegistrationEntry)
                .collect(Collectors.toList());
        return new LineChart(xAxis, yAxis, sections);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Coordinate {
        private String x;
        private Integer y;

        public static Coordinate fromTppRegistrationEntry(Map.Entry<LocalDate, Integer> entry) {
            return new Coordinate(entry.getKey().toString(), entry.getValue());
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Axis {
        private String label;
        private DataType dataType;
    }

    public enum DataType {
        DATE,
        INTEGER
    }
}
