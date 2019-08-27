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


public class FrequencyServiceEveryWorkingDayTest {

    private FrequencyService frequencyService = new FrequencyService();

    @Test
    public void testDuringWeek() {
        // Given
        DateTime previousDate = DateTime.parse("2018-01-23");

        // When
        DateTime nextDate = frequencyService.getNextDateTime(previousDate, "EvryWorkgDay");

        //Then
        DateTime expectedDate = DateTime.parse("2018-01-24");
        assertThat(nextDate).isEqualTo(expectedDate);
    }

    @Test
    public void testEndOfTheWeek() {
        // Given
        DateTime previousDate = DateTime.parse("2018-01-26");

        // When
        DateTime nextDate = frequencyService.getNextDateTime(previousDate, "EvryWorkgDay");

        //Then
        DateTime expectedDate = DateTime.parse("2018-01-29");
        assertThat(nextDate).isEqualTo(expectedDate);
    }

    @Test
    public void testDuringWeekend() {
        // Given
        DateTime previousDate = DateTime.parse("2018-01-27");

        // When
        DateTime nextDate = frequencyService.getNextDateTime(previousDate, "EvryWorkgDay");

        //Then
        DateTime expectedDate = DateTime.parse("2018-01-29");
        assertThat(nextDate).isEqualTo(expectedDate);
    }
}