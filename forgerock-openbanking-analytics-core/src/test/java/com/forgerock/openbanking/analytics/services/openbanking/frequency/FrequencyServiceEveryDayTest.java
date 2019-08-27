/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.services.openbanking.frequency;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class FrequencyServiceEveryDayTest {

    private FrequencyService frequencyService = new FrequencyService();

    @Test
    public void testEveryDay() {
        // Given
        DateTime previousDate = DateTime.parse("2018-01-23T00:00:00.00Z");

        // When
        DateTime nextDate = frequencyService.getNextDateTime(previousDate, "EvryDay");

        //Then
        DateTime expectedDate = DateTime.parse("2018-01-24T00:00:00.00Z");
        assertThat(nextDate).isEqualTo(expectedDate);
    }
}