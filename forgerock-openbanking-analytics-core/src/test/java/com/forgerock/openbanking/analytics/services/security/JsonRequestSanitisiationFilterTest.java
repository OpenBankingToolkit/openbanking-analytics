/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.services.security;

import brave.Tracer;
import com.forgerock.openbanking.analytics.rest.ErrorHandler;
import com.forgerock.openbanking.analytics.utils.TracerUtils;
import org.junit.Before;
import org.junit.Test;
import uk.org.openbanking.datamodel.error.OBErrorResponse1;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.StringReader;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class JsonRequestSanitisiationFilterTest {
    private JsonRequestSanitisiationFilter jsonRequestSanitisiationFilter;
    private final Tracer tracer = TracerUtils.mockTracer();
    private FilterChain filterChain;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private ErrorHandler errorHandler;

    @Before
    public void setup() {
        errorHandler = mock(ErrorHandler.class);
        filterChain = mock(FilterChain.class);
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        jsonRequestSanitisiationFilter = new JsonRequestSanitisiationFilter(errorHandler, tracer);
    }

    @Test
    public void doFilter_ignoreGET() throws Exception {
        // Given
        given(mockRequest.getMethod()).willReturn("GET");

        // When
        jsonRequestSanitisiationFilter.doFilter(mockRequest, mockResponse, filterChain);

        // Then
        verifyZeroInteractions(errorHandler);
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    public void doFilter_ignoreDELETE() throws Exception {
        // Given
        given(mockRequest.getMethod()).willReturn("DELETE");

        // When
        jsonRequestSanitisiationFilter.doFilter(mockRequest, mockResponse, filterChain);

        // Then
        verifyZeroInteractions(errorHandler);
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    public void doFilter_ignoreNonJsonPOST() throws Exception {
        // Given
        given(mockRequest.getMethod()).willReturn("POST");
        given(mockRequest.getContentType()).willReturn("application/xml");

        // When
        jsonRequestSanitisiationFilter.doFilter(mockRequest, mockResponse, filterChain);

        // Then
        verifyZeroInteractions(errorHandler);
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    public void doFilter_jsonWithNoHtml_passThrough() throws Exception {
        // Given
        given(mockRequest.getMethod()).willReturn("POST");
        given(mockRequest.getContentType()).willReturn("application/json");
        given(mockRequest.getReader()).willReturn(new BufferedReader(new StringReader("{\"key\": \"value\"}")));

        // When
        jsonRequestSanitisiationFilter.doFilter(mockRequest, mockResponse, filterChain);

        // Then
        verifyZeroInteractions(errorHandler);
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    public void doFilter_jsonWithHtml_reject() throws Exception {
        // Given
        given(mockRequest.getMethod()).willReturn("POST");
        given(mockRequest.getContentType()).willReturn("application/json");
        given(mockRequest.getReader()).willReturn(new BufferedReader(new StringReader("{\"key\": \"<script>alert()</script>\"}")));

        // When
        jsonRequestSanitisiationFilter.doFilter(mockRequest, mockResponse, filterChain);

        // Then
        verify(errorHandler).setHttpResponseToError(eq(mockResponse), any(OBErrorResponse1.class), eq(400));
        verifyZeroInteractions(filterChain);
    }
}