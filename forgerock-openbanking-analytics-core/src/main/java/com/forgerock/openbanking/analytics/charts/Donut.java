/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
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
