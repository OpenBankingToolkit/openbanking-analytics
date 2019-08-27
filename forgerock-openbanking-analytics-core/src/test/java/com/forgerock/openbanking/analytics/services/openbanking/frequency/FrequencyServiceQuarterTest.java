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


public class FrequencyServiceQuarterTest {

    private FrequencyService frequencyService = new FrequencyService();

    @Test
    public void test_FirstQuarter_English_From_Middle_Quarter() {
        // Given
        DateTime previousDate = DateTime.parse("2018-03-14T00:00:00.00Z");

        // When
        DateTime nextDate = frequencyService.getNextDateTime(previousDate, "QtrDay:ENGLISH");

        //Then
        DateTime expectedDate = DateTime.parse("2018-03-25T00:00:00.00Z");
        assertThat(nextDate).isEqualTo(expectedDate);
    }

    @Test
    public void test_SecondQuarter_English_From_Middle_Quarter() {
        // Given
        DateTime previousDate = DateTime.parse("2018-04-24T00:00:00.00Z");

        // When
        DateTime nextDate = frequencyService.getNextDateTime(previousDate, "QtrDay:ENGLISH");

        //Then
        DateTime expectedDate = DateTime.parse("2018-06-24T00:00:00.00Z");
        assertThat(nextDate).isEqualTo(expectedDate);
    }

    @Test
    public void test_ThirdQuarter_English_From_Middle_Quarter() {
        // Given
        DateTime previousDate = DateTime.parse("2018-07-24T00:00:00.00Z");

        // When
        DateTime nextDate = frequencyService.getNextDateTime(previousDate, "QtrDay:ENGLISH");

        //Then
        DateTime expectedDate = DateTime.parse("2018-09-29T00:00:00.00Z");
        assertThat(nextDate).isEqualTo(expectedDate);
    }

    @Test
    public void test_FourthQuarter_English_From_Middle_Quarter() {
        // Given
        DateTime previousDate = DateTime.parse("2018-10-24T00:00:00.00Z");

        // When
        DateTime nextDate = frequencyService.getNextDateTime(previousDate, "QtrDay:ENGLISH");

        //Then
        DateTime expectedDate = DateTime.parse("2018-12-25T00:00:00.00Z");
        assertThat(nextDate).isEqualTo(expectedDate);
    }

    @Test
    public void test_FirstQuarter_English_From_PreviousQuarter() {
        // Given
        DateTime previousDate = DateTime.parse("2017-12-25T00:00:00.00Z");

        // When
        DateTime nextDate = frequencyService.getNextDateTime(previousDate, "QtrDay:ENGLISH");

        //Then
        DateTime expectedDate = DateTime.parse("2018-03-25T00:00:00.00Z");
        assertThat(nextDate).isEqualTo(expectedDate);
    }

}