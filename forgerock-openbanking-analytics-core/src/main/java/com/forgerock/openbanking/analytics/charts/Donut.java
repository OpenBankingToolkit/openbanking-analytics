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

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Donut<T> {

    private Long total;
    private List<Section> data;

    public Optional<Section> getSection(T label) {
        return data.stream().filter(s -> s.getLabel().equals(label)).findAny();
    }

    public static <T> Donut convertCountersToDonus(Map<T, Long> counters) {
        List<Section> sections = new ArrayList<>();
        Long total = 0l;
        for (Map.Entry<T, Long> entry : counters.entrySet()) {
            sections.add(
                    Donut.Section
                            .builder()
                            .label(entry.getKey())
                            .value(entry.getValue())
                            .build()
            );
            total += entry.getValue();
        }
        return Donut
                .builder()
                .data(sections)
                .total(total)
                .build();
    }

    public static Donut fromTppDirectory(Map<String, Integer> dataset) {
        List<Section> sections = new ArrayList<>();
        long total = 0;
        for (Map.Entry<String, Integer> entry : dataset.entrySet()) {
            sections.add(Section.fromTppDirectoryEntry(entry));
            total += entry.getValue();
        }
        return new Donut(total, sections);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @EqualsAndHashCode
    public static class Section<T> {
        private T label;
        private Long value;

        static Section fromTppDirectoryEntry(Map.Entry<String, Integer> entry) {
            return new Section(entry.getKey(), new Long(entry.getValue()));
        }
    }
}
