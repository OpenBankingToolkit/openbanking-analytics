/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.services.store;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RsStoreGatewayTest {

    @Mock
    private RestTemplate restTemplate;

    private RsStoreGateway rsStoreGateway;

    @Before
    public void setUp() {
        rsStoreGateway = new RsStoreGateway(restTemplate, "http://test.com");
    }

    @Test
    public void shouldAddAdditionalHeaders() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("additional-header", "value");

        rsStoreGateway.toRsStore(request, httpHeaders, String.class);

        verify(restTemplate).exchange(any(),
                eq(HttpMethod.valueOf(request.getMethod())),
                argThat(a -> a.getHeaders().containsKey("additional-header") && a.getHeaders().get("additional-header").contains("value")),
                eq(String.class));
    }

    @Test
    public void shouldAddAdditionalParams() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        Map<String, String> param = ImmutableMap.of("date", "today");

        rsStoreGateway.toRsStore(request, new HttpHeaders(), param, String.class);

        verify(restTemplate).exchange(eq(URI.create("http://test.com?date=today")),
                eq(HttpMethod.valueOf(request.getMethod())),
                any(),
                eq(String.class));
    }

    @Test
    public void shouldOverrideExistingParams() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("?date=yesterday");
        request.setMethod("GET");
        Map<String, String> param = ImmutableMap.of("date", "today");

        rsStoreGateway.toRsStore(request, new HttpHeaders(), param, String.class);

        verify(restTemplate).exchange(eq(URI.create("http://test.com?date=today")),
                eq(HttpMethod.valueOf(request.getMethod())),
                any(),
                eq(String.class));
    }
}