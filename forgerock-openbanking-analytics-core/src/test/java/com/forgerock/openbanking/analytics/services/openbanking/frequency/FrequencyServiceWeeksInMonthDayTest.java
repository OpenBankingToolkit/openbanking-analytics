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


public class FrequencyServiceWeeksInMonthDayTest {

    private FrequencyService frequencyService = new FrequencyService();

    @Test
    public void test_EveryMonth_SecondWeeks_thirdDayOfWeek() {
        // Given
        DateTime previousDate = DateTime.parse("2018-01-17T00:00:00.00Z");

        // When
        DateTime nextDate = frequencyService.getNextDateTime(previousDate, "WkInMnthDay:2:3");

        //Then
        DateTime expectedDate = DateTime.parse("2018-02-14T00:00:00.00Z");
        assertThat(nextDate).isEqualTo(expectedDate);
    }
}