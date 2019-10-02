/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.kpi;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EndpointsUsageAggregationTest {

    @Test
    public void getValuesFromEntryForMs_valid() {
        // Given
        List definitionsSet = ImmutableList.of(10.0, 10.0, 1000.0, 10000.0);

        // When
        List result = EndpointsUsageAggregation.BY_RESPONSE_TIME.getValuesFromEntryForMs(Collections.singletonList(10L), definitionsSet);

        // Then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(10.0);
    }

    @Test
    public void getValuesFromEntryForMs_empty() {
        // Given
        List definitionsSet = Collections.emptyList();

        // When
        List result = EndpointsUsageAggregation.BY_RESPONSE_TIME.getValuesFromEntryForMs(Collections.singletonList(10L), definitionsSet);

        // Then
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void getValuesFromEntryForMs_noLogScale() {
        // Given
        List definitionsSet = Collections.singletonList(1L);

        // When
        List result = EndpointsUsageAggregation.BY_RESPONSE_TIME.getValuesFromEntryForMs(Collections.singletonList(10L), definitionsSet);

        // Then
        assertThat(result.isEmpty()).isTrue();
    }
}