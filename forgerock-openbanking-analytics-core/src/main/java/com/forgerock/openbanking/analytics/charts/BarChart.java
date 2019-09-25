/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.charts;

import com.forgerock.openbanking.model.SoftwareStatementRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.forgerock.openbanking.analytics.charts.BarChart.Bar.fromTppTypeEntry;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BarChart {

    private Integer total;
    private List<Bar> data;

    public static BarChart fromTppTypes(Map<SoftwareStatementRole, Integer> dataset) {
        List<Bar> bars = new ArrayList<>();
        int total = 0;
        for (Map.Entry<SoftwareStatementRole, Integer> entry : dataset.entrySet()) {
            bars.add(fromTppTypeEntry(entry));
            total += entry.getValue();
        }
        return new BarChart(total, bars);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Bar {

        private String label;
        private Integer value;

        static Bar fromTppTypeEntry(Map.Entry<SoftwareStatementRole, Integer> entry) {
            return new Bar(entry.getKey().name(), entry.getValue());
        }
    }
}
