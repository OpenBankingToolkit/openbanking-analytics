/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.org.openbanking.datamodel.error.OBError1;
import uk.org.openbanking.datamodel.error.OBErrorResponse1;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ErrorHandlerTest {
    private final ErrorHandler errorHandler = new ErrorHandler(new ObjectMapper());

    @Test
    public void setHttpResponseToError() throws Exception{
        // Given
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        given(mockResponse.getOutputStream()).willReturn(servletOutputStream);
        OBErrorResponse1 obError = new OBErrorResponse1()
                .code("")
                .id("")
                .message("")
                .errors(Collections.singletonList(new OBError1().path("").errorCode("").message("").url("")));

        // When
        errorHandler.setHttpResponseToError(mockResponse, obError, 400);

        // Then
        verify(mockResponse).resetBuffer();
        verify(servletOutputStream).write(any());
        verify(mockResponse).setStatus(eq(400));
    }

    @Test
    public void setHttpResponseToError_reject200() {
        assertThatThrownBy(() ->
            errorHandler.setHttpResponseToError(mock(HttpServletResponse.class), new OBErrorResponse1(), 200)
        ).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    public void setHttpResponseToError_reject300() {
        assertThatThrownBy(() ->
                errorHandler.setHttpResponseToError(mock(HttpServletResponse.class), new OBErrorResponse1(), 300)
        ).isInstanceOf(IllegalArgumentException.class);

    }

}