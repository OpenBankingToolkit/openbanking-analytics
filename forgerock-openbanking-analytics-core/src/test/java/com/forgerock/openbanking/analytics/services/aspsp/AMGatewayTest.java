/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.services.aspsp;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class AMGatewayTest {

    private String amRoot = "https://am";
    private RestTemplate restTemplate;
    private AMGateway aMGateway;

    @Before
    public void setUp() throws Exception {
        restTemplate = mock(RestTemplate.class);
        aMGateway = new AMGateway(restTemplate, amRoot, null, null);
    }

    @Test
    public void splitQuery() throws UnsupportedEncodingException {
        //Given
        String query = "response_type=code%20id_token&client_id=e3a591cf-f7db-4fa8-a7b8-90bc382f5b0f" +
                "&state=5bfd102ef1d7daa5b461f710&nonce=5bfd102ef1d7daa5b461f710&scope=accounts%20openid%20payments" +
                "&redirect_uri=https://tpp-core.dev-ob.forgerock.financial:8074/open-banking/v1.1/exchange_code/pisp/" +
                "&acr=urn%3Aopenbanking%3Apsd2%3Aca";

        //When
        Map<String, String> queryParsed = AMGateway.splitQuery(query);

        //Then
        assertThat(queryParsed.get("response_type")).isEqualTo("code id_token");
        assertThat(queryParsed.get("acr")).isEqualTo("urn:openbanking:psd2:ca");
    }

    @Test
    public void shouldShowAMResponseWhenClientError() throws URISyntaxException {
        //Given
        HttpServletRequest request = new MockHttpServletRequest();
        ParameterizedTypeReference<String> parameterizedTypeReference = new ParameterizedTypeReference<String>() {
        };
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", "AM Error".getBytes(), Charset.defaultCharset());
        given(restTemplate.exchange(eq(new URI(amRoot)), any(), any(), eq(parameterizedTypeReference)))
                .willThrow(exception);

        //When
        ResponseEntity responseEntity = aMGateway.toAM(request, new HttpHeaders(), parameterizedTypeReference);

        //Then
        assertThat(responseEntity.getBody()).isEqualTo("AM Error".getBytes());
    }

    @Test
    public void shouldReturnAMStatusCodeWhenClientError() throws URISyntaxException {
        //Given
        HttpServletRequest request = new MockHttpServletRequest();
        ParameterizedTypeReference<String> parameterizedTypeReference = new ParameterizedTypeReference<String>() {
        };
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", "AM Error".getBytes(), Charset.defaultCharset());
        given(restTemplate.exchange(eq(new URI(amRoot)), any(), any(), eq(parameterizedTypeReference)))
                .willThrow(exception);

        //When
        ResponseEntity responseEntity = aMGateway.toAM(request, new HttpHeaders(), parameterizedTypeReference);

        //Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldReturnAMStatusCodeWhenServerError() throws URISyntaxException {
        //Given
        HttpServletRequest request = new MockHttpServletRequest();
        ParameterizedTypeReference<String> parameterizedTypeReference = new ParameterizedTypeReference<String>() {
        };
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Bad Request", "AM Error".getBytes(), Charset.defaultCharset());
        given(restTemplate.exchange(eq(new URI(amRoot)), any(), any(), eq(parameterizedTypeReference)))
                .willThrow(exception);

        //When
        ResponseEntity responseEntity = aMGateway.toAM(request, new HttpHeaders(), parameterizedTypeReference);

        //Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}