/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class IsoDateTimeSerializerTest {
    @Mock
    private JsonGenerator jsonGenerator;
    @Mock
    private SerializerProvider serializerProvider;

    @Test
    public void testSerialisationParis() throws IOException {

        // Given
        IsoDateTimeSerializer isoDateTimeSerializer = new IsoDateTimeSerializer();
        DateTime dateTimeParis = new DateTime(2000, 1, 2, 3, 4, 5, 6,
                DateTimeZone.forID("Europe/Paris"));

        // When
        isoDateTimeSerializer.serialize(dateTimeParis, jsonGenerator, serializerProvider);

        //Then
        verify(jsonGenerator).writeObject("2000-01-02T03:04:05.006+01:00");
    }

    @Test
    public void testSerialisationUTC() throws IOException {

        // Given
        IsoDateTimeSerializer isoDateTimeSerializer = new IsoDateTimeSerializer();
        DateTime dateTimeUTC = new DateTime(2000, 1, 2, 3, 4, 5, 6,
                DateTimeZone.UTC);

        // When
        isoDateTimeSerializer.serialize(dateTimeUTC, jsonGenerator, serializerProvider);

        //Then
        verify(jsonGenerator).writeObject("2000-01-02T03:04:05.006Z");
    }

}