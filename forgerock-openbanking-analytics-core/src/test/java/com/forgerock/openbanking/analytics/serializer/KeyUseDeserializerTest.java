/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.serializer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.KeyUse;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class KeyUseDeserializerTest {

    @Test
    public void testSerialisation() throws IOException {
        // Given
        JsonParser jsonParser = new JsonFactory().createParser("\"sig\"");
        jsonParser.setCodec(new ObjectMapper());

        // When
        KeyUse keyUse = new KeyUseDeserializer().deserialize(jsonParser, null);

        // Then
        assertEquals(keyUse, KeyUse.SIGNATURE);
    }

}