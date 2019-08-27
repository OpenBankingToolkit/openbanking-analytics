/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.serializer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.nimbusds.jose.jwk.KeyUse;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class KeyUseSerializerTest {

    @Test
    public void testSerialisation() throws IOException {
        // Given
        KeyUse signature = KeyUse.SIGNATURE;
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        JsonGenerator generator = new JsonFactory().createGenerator(s);

        // When
        new KeyUseSerializer().serialize(signature, generator, null);
        generator.flush();

        // Then
        assertEquals(s.toString(), "\"sig\"");
    }
}