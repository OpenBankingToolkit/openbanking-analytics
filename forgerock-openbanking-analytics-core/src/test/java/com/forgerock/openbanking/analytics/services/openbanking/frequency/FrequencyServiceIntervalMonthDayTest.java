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


public class FrequencyServiceIntervalMonthDayTest {

    private FrequencyService frequencyService = new FrequencyService();

    @Test
    public void test_EveryLastDayOfTheMonth() {
        // Given
        DateTime previousDate = DateTime.parse("2018-01-31T00:00:00.00Z");

        // When
        DateTime nextDate = frequencyService.getNextDateTime(previousDate, "IntrvlMnthDay:1:-1");

        //Then
        DateTime expectedDate = DateTime.parse("2018-02-28T00:00:00.00Z");
        assertThat(nextDate).isEqualTo(expectedDate);
    }

    @Test
    public void test_EverySixthMonthOnTheFifteenDay() {
        // Given
        DateTime previousDate = DateTime.parse("2018-01-15T00:00:00.00Z");

        // When
        DateTime nextDate = frequencyService.getNextDateTime(previousDate, "IntrvlMnthDay:6:15");

        //Then
        DateTime expectedDate = DateTime.parse("2018-07-15T00:00:00.00Z");
        assertThat(nextDate).isEqualTo(expectedDate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_EndOfMonthsForbidden() {
        // Given
        DateTime previousDate = DateTime.parse("2018-01-31T00:00:00.00Z");

        // When
        DateTime nextDate = frequencyService.getNextDateTime(previousDate, "IntrvlMnthDay:6:29");
    }
}