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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.actuate.trace.http.HttpTrace;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class HttpTraceSerialiserTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @InjectMocks
    private HttpTraceSerialiser httpTraceSerialiser;

    @Test
    public void shouldSerialiseHttpTraceWithMillisecondsTimestamp() throws IOException, JSONException {
        // Given
        HttpTrace.Request request = new HttpTrace.Request("GET", URI.create("https://example.org"), Collections.emptyMap(), "https://example.org");
        Instant now = Instant.now();
        HttpTrace.Response response = new HttpTrace.Response(200, Collections.emptyMap());
        HttpTrace.Principal principal = new HttpTrace.Principal("testuser");
        HttpTrace.Session session = new HttpTrace.Session("session");
        HttpTrace httpTrace = new HttpTrace(request, response, now, principal, session, 100L);
        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory(OBJECT_MAPPER).createGenerator(stringWriter);

        // When
        httpTraceSerialiser.serialize(httpTrace, jsonGenerator, null);

        // Then
        jsonGenerator.close();
        JsonNode jsonNode = OBJECT_MAPPER.readTree(stringWriter.toString());
        assertThat(jsonNode.get("timestamp").asLong()).isEqualTo(now.toEpochMilli());
    }
}