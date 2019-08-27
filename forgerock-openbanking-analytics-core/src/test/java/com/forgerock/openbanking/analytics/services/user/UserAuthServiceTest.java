/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.services.user;

import com.forgerock.openbanking.analytics.auth.UserContext;
import com.forgerock.openbanking.analytics.model.directory.Organisation;
import com.forgerock.openbanking.analytics.model.directory.User;
import com.forgerock.openbanking.analytics.model.oidc.AccessTokenResponse;
import com.forgerock.openbanking.analytics.model.oidc.ExchangeCodeResponse;
import com.forgerock.openbanking.analytics.services.am.OpenIdService;
import com.forgerock.openbanking.analytics.services.aspsp.AMAuthGateway;
import com.forgerock.openbanking.analytics.services.cookie.CookieService;
import com.forgerock.openbanking.analytics.services.token.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;

import javax.servlet.http.HttpServletResponse;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

import static com.forgerock.openbanking.analytics.services.cookie.CookieService.OIDC_ORIGIN_URI_CONTEXT_COOKIE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(properties = { "am.internal.oidc.endpoint.accesstoken=foo" })
public class UserAuthServiceTest {
    private static final String ORIGIN_URL = "http://unit-test";
    private static final String REDIRECT_URL = "http://redirect";
    private static final String SESSION_JWT = "{\"jwt\": \"etc\"}";

    @Mock
    private OpenIdService openIdService;
    @Mock
    private SessionService sessionService;
    @Mock
    private CookieService cookieService;
    @Mock
    private AMAuthGateway amGateway;

    @InjectMocks
    private UserAuthService userAuthService;

    private HttpServletResponse mockResponse;
    private UserContext userContext;
    private Authentication authentication;

    @Before
    public void setUp() {
        mockResponse = mock(HttpServletResponse.class);
        authentication = mock(Authentication.class);


        userContext = UserContext.create("user1", new ArrayList<>(), UserContext.UserType.ANONYMOUS);
        given(authentication.getPrincipal()).willReturn(userContext);
        given(authentication.getName()).willReturn("USER1");
    }

    @Test
    public void createAuthorisationRequest_buildValidResponseAndCreateCookie() {
        // Given
        String expectedResponse = "http://some-value?param=1";
        given(openIdService.generateAuthorisationRequest(any(), eq(REDIRECT_URL), anyList(), anyList()))
            .willReturn(expectedResponse);

        // When
        String actualResponse = userAuthService.createAuthorisationRequest(authentication, ORIGIN_URL, REDIRECT_URL, mockResponse);

        // Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(cookieService, times(1)).createCookie(eq(mockResponse), eq(OIDC_ORIGIN_URI_CONTEXT_COOKIE_NAME), eq(ORIGIN_URL));
    }

    @Test
    public void loginUserWithCode_validCode_returnValidIdToken_createSession() throws Exception {
        // Given
        String authCode = "abcde12345";
        String idToken = "jehfkjhfk";
        String sessionJwt = "{\"jwt\": \"etc\"}";
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        accessTokenResponse.setId_token(idToken);

        given(openIdService.exchangeCode(any(), eq(REDIRECT_URL), any(), eq(authCode)))
                .willReturn(accessTokenResponse);
        given(openIdService.fromIdToken(eq(idToken)))
                .willReturn(userContext);
        given(sessionService.generateSessionContextJwt(any()))
                .willReturn(sessionJwt);

        // When
        ExchangeCodeResponse exchangeCodeResponse = userAuthService.loginUserWithCode(authCode, ORIGIN_URL, REDIRECT_URL, mockResponse);

        // Then
        assertThat(exchangeCodeResponse.getOriginalRequest()).isEqualTo(ORIGIN_URL);
        verify(cookieService, times(1)).createSessionCookie(eq(mockResponse), eq(sessionJwt));
    }

    @Test
    public void logout_success_deleteSessionCookie() {
        // Given
        String sessionJwt = "{\"jwt\": \"etc\"}";
        given(sessionService.expiredSessionContext(eq(userContext)))
                .willReturn(sessionJwt);

        // When
        boolean response = userAuthService.logout(authentication, mockResponse);

        // Then
        assertThat(response).isTrue();
        verify(sessionService, times(1)).expiredSessionContext(eq(userContext));
        verify(cookieService, times(1)).deleteSessionCookie(eq(mockResponse), eq(sessionJwt));
    }

    @Test
    public void getLoggedInUser_userExists_returnUser() throws CertificateEncodingException {
        // Given
        final User existingUser = new User();
        existingUser.setId("test1");
        Function<String, Optional<User>> getUser = f -> Optional.of(existingUser);
        userContext.getAuthorities().add(new SimpleGrantedAuthority("TestRole"));
        given(sessionService.generateSessionContextJwt(any()))
                .willReturn(SESSION_JWT);

        // When
        User returnedUser = userAuthService.getLoggedInUser(authentication,
                getUser,
                persistUser,
                persistOrg,
                mockResponse
        );

        // Then
        assertThat(returnedUser).isEqualTo(existingUser);
        assertThat(returnedUser.getId()).isEqualTo("test1");
        assertThat(returnedUser.getAuthorities()).containsExactly("TestRole");
        verify(cookieService, times(1)).createSessionCookie(eq(mockResponse), eq(SESSION_JWT));
    }

    @Test
    public void getLoggedInUser_userDoesNotExist__createUserAndOrg() throws CertificateEncodingException {
        // Given
        Function<String, Optional<User>> getUser = f -> Optional.empty(); // No user found
        userContext.getAuthorities().add(new SimpleGrantedAuthority("TestRole"));

        given(sessionService.generateSessionContextJwt(any()))
                .willReturn(SESSION_JWT);

        // When
        User returnedUser = userAuthService.getLoggedInUser(authentication,
                getUser,
                persistUser,
                persistOrg,
                mockResponse
        );

        // Then
        assertThat(returnedUser.getId()).isEqualTo("user1");
        assertThat(returnedUser.getOrganisationId()).isEqualTo("org1");
        assertThat(returnedUser.getAuthorities()).containsExactly("TestRole");
        verify(cookieService, times(1)).createSessionCookie(eq(mockResponse), eq(SESSION_JWT));
    }

    private Function<User, User> persistUser = Function.identity();

    private Function<Organisation, Organisation> persistOrg = o -> {
        o.setId("org1");
        return o;
    };
}