/**
 * Copyright 2019 ForgeRock AS.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.forgerock.openbanking.analytics.filters;

import com.forgerock.openbanking.analytics.configuration.MetricsConfigurationProperties;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageEntry;
import com.forgerock.openbanking.analytics.services.MetricService;
import com.forgerock.openbanking.model.UserContext;
import org.apache.catalina.ssi.ByteArrayServletOutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class MetricFilterTest {
    private MetricsConfigurationProperties metricsConfigurationProperties;
    private MetricService metricService;
    private MetricFilter metricFilter;

    private FilterChain filterChain;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;

    @Before
    public void setup() throws IOException {
        metricService = mock(MetricService.class);
        metricsConfigurationProperties = new MetricsConfigurationProperties();
        RequestMappingHandlerMapping mappings = mock(RequestMappingHandlerMapping.class);
        metricFilter = new MetricFilter(metricService, "app1", metricsConfigurationProperties, mappings);

        filterChain = mock(FilterChain.class);
        mockRequest = mock(HttpServletRequest.class);
        given(mockRequest.getHeaderNames()).willReturn(Collections.emptyEnumeration());
        mockResponse = mock(HttpServletResponse.class);
        given(mockResponse.getOutputStream()).willReturn(new ByteArrayServletOutputStream());

        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("test1", ""));
    }

    @After
    public void teardown() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void noWhitelist_noMetricSaved() throws Exception {
        // Given
        metricsConfigurationProperties.setEndpoints(Collections.emptyList());

        // When
        metricFilter.doFilter(mockRequest, mockResponse, filterChain);

        // Then
        verifyZeroInteractions(metricService);
    }

    @Test
    public void noMatchedOnWhitelist_noMetricSaved() throws Exception {
        // Given
        metricsConfigurationProperties.setEndpoints(Collections.singletonList(endpointWithRegex("/test/url/.*")));
        given(mockRequest.getRequestURI()).willReturn("/no/match/to-anything");
        given(mockRequest.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE)).willReturn("/no/match/to-anything");

        // When
        metricFilter.doFilter(mockRequest, mockResponse, filterChain);

        // Then
        verifyZeroInteractions(metricService);
    }

    @Test
    public void matchedWhitelist_noVars_UrlSavedToMetricService() throws Exception {
        // Given
        metricsConfigurationProperties.setEndpoints(Collections.singletonList(endpointWithRegex("/test/url/.*")));
        given(mockRequest.getRequestURI()).willReturn("/test/url/someVal");
        given(mockRequest.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE)).willReturn("/test/url/someVal");
        given(mockRequest.getMethod()).willReturn("GET");
        given(mockResponse.getStatus()).willReturn(200);
        given(mockResponse.getHeaderNames()).willReturn(Arrays.asList("UserType", "UserId"));
        given(mockResponse.getHeader("UserType")).willReturn(UserContext.UserType.OIDC_CLIENT.name());
        given(mockResponse.getHeader("UserId")).willReturn("test1");
        // When
        metricFilter.doFilter(mockRequest, mockResponse, filterChain);

        // Then
        ArgumentCaptor<EndpointUsageEntry> argumentCaptor = ArgumentCaptor.forClass(EndpointUsageEntry.class);
        verify(metricService, times(1)).addEndpointUsageEntry(argumentCaptor.capture());

        EndpointUsageEntry endpointUsageEntry = argumentCaptor.getValue();
        assertThat(endpointUsageEntry.getMethod()).isEqualTo("GET");
        assertThat(endpointUsageEntry.getApplication()).isEqualTo("app1");
        assertThat(endpointUsageEntry.getEndpoint()).isEqualTo("/test/url/someVal");
        assertThat(endpointUsageEntry.getIdentityId()).isEqualTo("test1");
        assertThat(endpointUsageEntry.getResponseStatus()).isEqualTo("200");
    }

    @Test
    public void matchedWhitelist_twoVars_UrlWithVarsSavedToMetricService() throws Exception {
        // Given
        metricsConfigurationProperties.setEndpoints(Collections.singletonList(endpointWithRegex("/test/(.*)/url/(.*)")));
        given(mockRequest.getRequestURI()).willReturn("/test/123/url/456");
        given(mockRequest.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE)).willReturn("/test/{id1}/url/{id2}");
        given(mockRequest.getMethod()).willReturn("POST");
        given(mockResponse.getStatus()).willReturn(201);
        given(mockResponse.getHeaderNames()).willReturn(Arrays.asList("UserType", "UserId"));
        given(mockResponse.getHeader("UserType")).willReturn(UserContext.UserType.OIDC_CLIENT.name());
        given(mockResponse.getHeader("UserId")).willReturn("test1");

        // When
        metricFilter.doFilter(mockRequest, mockResponse, filterChain);

        // Then
        ArgumentCaptor<EndpointUsageEntry> argumentCaptor = ArgumentCaptor.forClass(EndpointUsageEntry.class);
        verify(metricService, times(1)).addEndpointUsageEntry(argumentCaptor.capture());

        EndpointUsageEntry endpointUsageEntry = argumentCaptor.getValue();
        assertThat(endpointUsageEntry.getMethod()).isEqualTo("POST");
        assertThat(endpointUsageEntry.getApplication()).isEqualTo("app1");
        assertThat(endpointUsageEntry.getEndpoint()).isEqualTo("/test/{id1}/url/{id2}");
        assertThat(endpointUsageEntry.getIdentityId()).isEqualTo("test1");
        assertThat(endpointUsageEntry.getResponseStatus()).isEqualTo("201");
    }

    @Test
    public void matchedWhitelistTwice_onlyUseFirstMatch() throws Exception {
        // Given
        metricsConfigurationProperties.setEndpoints(Arrays.asList(
                endpointWithRegex("/test/url/first"),
                endpointWithRegex("/test/url/(.*)", "id")
        ));
        given(mockRequest.getRequestURI()).willReturn("/test/url/first");
        given(mockRequest.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE)).willReturn("/test/url/first");
        given(mockRequest.getMethod()).willReturn("GET");
        given(mockResponse.getStatus()).willReturn(404);
        given(mockResponse.getHeaderNames()).willReturn(Arrays.asList("UserType", "UserId"));
        given(mockResponse.getHeaderNames()).willReturn(Arrays.asList("UserType", "UserId"));
        given(mockResponse.getHeader("UserType")).willReturn(UserContext.UserType.OIDC_CLIENT.name());
        given(mockResponse.getHeader("UserId")).willReturn("test1");

        // When
        metricFilter.doFilter(mockRequest, mockResponse, filterChain);

        // Then
        ArgumentCaptor<EndpointUsageEntry> argumentCaptor = ArgumentCaptor.forClass(EndpointUsageEntry.class);
        verify(metricService, times(1)).addEndpointUsageEntry(argumentCaptor.capture());

        EndpointUsageEntry endpointUsageEntry = argumentCaptor.getValue();
        assertThat(endpointUsageEntry.getMethod()).isEqualTo("GET");
        assertThat(endpointUsageEntry.getApplication()).isEqualTo("app1");
        assertThat(endpointUsageEntry.getEndpoint()).isEqualTo("/test/url/first");
        assertThat(endpointUsageEntry.getIdentityId()).isEqualTo("test1");
        assertThat(endpointUsageEntry.getResponseStatus()).isEqualTo("404");
    }

    private static MetricsConfigurationProperties.Endpoint endpointWithRegex(String regex, String... variables) {
        MetricsConfigurationProperties.Endpoint endpoint = new MetricsConfigurationProperties.Endpoint();
        endpoint.setRegex(regex);
        return endpoint;
    }
}