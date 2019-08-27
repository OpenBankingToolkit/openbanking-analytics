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


public class FrequencyServiceIntervalDayTest {

    private FrequencyService frequencyService = new FrequencyService();

    @Test
    public void testDayPlus31() {
        // Given
        DateTime previousDate = DateTime.parse("2018-01-23T00:00:00.00Z");

        // When
        DateTime nextDate = frequencyService.getNextDateTime(previousDate, "IntrvlDay:31");

        //Then
        DateTime expectedDate = DateTime.parse("2018-02-23T00:00:00.00Z");
        assertThat(nextDate).isEqualTo(expectedDate);
    }

    @Test
    public void testDayPlus02() {
        // Given
        DateTime previousDate = DateTime.parse("2018-01-23T00:00:00.00Z");

        // When
        DateTime nextDate = frequencyService.getNextDateTime(previousDate, "IntrvlDay:02");

        //Then
        DateTime expectedDate = DateTime.parse("2018-01-25T00:00:00.00Z");
        assertThat(nextDate).isEqualTo(expectedDate);
    }

    @Test
    public void testDayPlus2() {
        // Given
        DateTime previousDate = DateTime.parse("2018-01-23T00:00:00.00Z");

        // When
        DateTime nextDate = frequencyService.getNextDateTime(previousDate, "IntrvlDay:2");

        //Then
        DateTime expectedDate = DateTime.parse("2018-01-25T00:00:00.00Z");
        assertThat(nextDate).isEqualTo(expectedDate);
    }

    @Test
    public void testDayPlus10() {
        // Given
        DateTime previousDate = DateTime.parse("2018-01-23T00:00:00.00Z");

        // When
        DateTime nextDate = frequencyService.getNextDateTime(previousDate, "IntrvlDay:10");

        //Then
        DateTime expectedDate = DateTime.parse("2018-02-02T00:00:00.00Z");
        assertThat(nextDate).isEqualTo(expectedDate);
    }

    @Test
    public void testDayPlus20() {
        // Given
        DateTime previousDate = DateTime.parse("2018-01-23T00:00:00.00Z");

        // When
        DateTime nextDate = frequencyService.getNextDateTime(previousDate, "IntrvlDay:20");

        //Then
        DateTime expectedDate = DateTime.parse("2018-02-12T00:00:00.00Z");
        assertThat(nextDate).isEqualTo(expectedDate);
    }
}