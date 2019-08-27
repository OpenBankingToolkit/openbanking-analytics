/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class IsoDateTimeDeserializerTest {
    @Mock
    private JsonParser jsonParser;
    @Mock
    private DeserializationContext deserializationContext;

    @Test
    public void testDeserializerUTC() throws IOException {

        // Given
        IsoDateTimeDeserializer isoDateTimeDeserializer = new IsoDateTimeDeserializer();
        given(jsonParser.getText()).willReturn("2000-01-02T03:04:05.006Z");

        // When
        DateTime resultingDateTime = isoDateTimeDeserializer.deserialize(jsonParser, deserializationContext);
        
        //Then
        DateTime dateTimeUTC = new DateTime(2000, 1, 2, 3, 4, 5, 6,
                DateTimeZone.UTC);
        assertThat(resultingDateTime).isEqualByComparingTo(dateTimeUTC);
    }

    @Test
    public void testDeserializerUTCWithTimezone() throws IOException {

        // Given
        IsoDateTimeDeserializer isoDateTimeDeserializer = new IsoDateTimeDeserializer();
        given(jsonParser.getText()).willReturn("2000-01-02T03:04:05.006+00:00");

        // When
        DateTime resultingDateTime = isoDateTimeDeserializer.deserialize(jsonParser, deserializationContext);

        //Then
        DateTime dateTimeUTC = new DateTime(2000, 1, 2, 3, 4, 5, 6,
                DateTimeZone.UTC);
        assertThat(resultingDateTime).isEqualByComparingTo(dateTimeUTC);
    }

    @Test
    public void testDeserializerUTCWithTimezoneParis() throws IOException {

        // Given
        IsoDateTimeDeserializer isoDateTimeDeserializer = new IsoDateTimeDeserializer();
        given(jsonParser.getText()).willReturn("2000-01-02T03:04:05.006+01:00");

        // When
        DateTime resultingDateTime = isoDateTimeDeserializer.deserialize(jsonParser, deserializationContext);

        //Then
        DateTime dateTimeUTC = new DateTime(2000, 1, 2, 3, 4, 5, 6,
                DateTimeZone.forID("Europe/Paris"));
        assertThat(resultingDateTime).isEqualByComparingTo(dateTimeUTC);
    }

    @Test
    public void testDeserializerUTCWithTimezoneNoMillis() throws IOException {

        // Given
        IsoDateTimeDeserializer isoDateTimeDeserializer = new IsoDateTimeDeserializer();
        given(jsonParser.getText()).willReturn("2000-01-02T03:04:05+00:00");

        // When
        DateTime resultingDateTime = isoDateTimeDeserializer.deserialize(jsonParser, deserializationContext);

        //Then
        DateTime dateTimeUTC = new DateTime(2000, 1, 2, 3, 4, 5, 0,
                DateTimeZone.UTC);
        assertThat(resultingDateTime).isEqualByComparingTo(dateTimeUTC);
    }

    @Test
    public void testDeserializerUTCWithNanoSeconds() throws IOException {

        // Given
        IsoDateTimeDeserializer isoDateTimeDeserializer = new IsoDateTimeDeserializer();
        given(jsonParser.getText()).willReturn("2000-01-02T03:04:05.601822Z");

        // When
        DateTime resultingDateTime = isoDateTimeDeserializer.deserialize(jsonParser, deserializationContext);

        //Then
        DateTime dateTimeUTC = new DateTime(2000, 1, 2, 3, 4, 5, 601,
                DateTimeZone.UTC);
        assertThat(resultingDateTime).isEqualByComparingTo(dateTimeUTC);
    }

}