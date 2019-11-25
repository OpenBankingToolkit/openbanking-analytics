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
