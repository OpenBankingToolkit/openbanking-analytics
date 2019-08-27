/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.auth;

import com.forgerock.openbanking.analytics.auth.extractor.TokenExtractor;
import com.forgerock.openbanking.analytics.auth.jwt.JwtAuthenticationFailureHandler;
import com.forgerock.openbanking.analytics.auth.jwt.JwtAuthenticationToken;
import com.forgerock.openbanking.analytics.auth.x509.ForgeRockAppMATLService;
import com.forgerock.openbanking.analytics.configuration.auth.JwtAuthConfigurationProperties;
import com.forgerock.openbanking.analytics.configuration.auth.MATLSConfigurationProperties;
import com.forgerock.openbanking.analytics.services.keystore.KeyStoreService;
import com.nimbusds.jose.jwk.JWK;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.Cookie;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.forgerock.openbanking.analytics.auth.Authenticator.AUTHENTICATION_HEADER_NAME;
import static com.forgerock.openbanking.analytics.auth.Authenticator.CLIENT_JWK_HEADER_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticatorTest {

    private static final String CERT_JWK = "{\"kty\":\"RSA\",\"x5t#S256\":\"foZp8HwyfS5uViA-A7ZlhMxr91tWzDH0YCz7f6iMeos\",\"e\":\"AQAB\",\"kid\":\"106975682923824153417588681451978436149874917717\",\"x5c\":[\"MIIFfDCCBGSgAwIBAgIUErz0nWF6uQi0Pm2CzqSWP51+AVUwDQYJKoZIhvcNAQELBQAwezELMAkGA1UEBhMCVUsxDTALBgNVBAgTBEF2b24xEDAOBgNVBAcTB0JyaXN0b2wxEjAQBgNVBAoTCUZvcmdlUm9jazEcMBoGA1UECxMTZm9yZ2Vyb2NrLmZpbmFuY2lhbDEZMBcGA1UEAxMQb2JyaS1leHRlcm5hbC1jYTAgFw0xODA2MTcxNTA4NDJaGA8yMTE5MDUyNDE1MDg0Mlowgb0xLTArBgNVBAMMJDI0NDZkNzM0LWJjMGYtNDAxZC1iZjFjLWYxNzJhM2Y3MmI5ZTEhMB8GA1UECwwYNWNmYTVhYmU3NTkwYjAwMWE0MTdiNTUxMRIwEAYDVQQKDAlGb3JnZVJvY2sxEDAOBgNVBAcMB0JyaXN0b2wxDTALBgNVBAgMBEF2b24xCzAJBgNVBAYTAlVLMScwJQYDVQRhDB5QU0RHQi01Y2ZhNWFiZTc1OTBiMDAxYTQxN2I1NTEwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC4ck9HUKH66dPl+5W\\/LmBa\\/WROdb1EcExaOCzat002miLeD79oqct\\/2E1EsPojEg\\/hd5WLCwGtESRbx4elJl8+R1VslWfLy3rvG\\/lhysLj2Jj\\/zLlFo3qKje4wEn1jZcuwpZuvkCoayVqIlam9uN9lGn33OlGauXBh8pRLrvGbUFWgwZf5g+k1CB6CaQ+cC17TaNPkAxSbHxe9HQGbp5WVyj4iHriut\\/1cz6yhEs8710IE\\/Cfqbt3z\\/l5IYhDCq3xCo6eG6XVMK14Y3K7zrx0oGQMzMDWkGiGvFbYyjvEbbKlfV8OYQUA90nQviIT2QK1txq97b7rw8J4S+e9OyTeXAgMBAAGjggGxMIIBrTCB1AYIKwYBBQUHAQEEgccwgcQwYAYIKwYBBQUHMAKGVGh0dHBzOi8vc2VydmljZS5kaXJlY3RvcnkuZGV2LW9iLmZvcmdlcm9jay5maW5hbmNpYWw6ODA3NC9hcGkvZGlyZWN0b3J5L2tleXMvandrX3VyaTBgBggrBgEFBQcwAYZUaHR0cHM6Ly9zZXJ2aWNlLmRpcmVjdG9yeS5kZXYtb2IuZm9yZ2Vyb2NrLmZpbmFuY2lhbDo4MDc0L2FwaS9kaXJlY3Rvcnkva2V5cy9qd2tfdXJpMIHTBggrBgEFBQcBAwSBxjCBwzAIBgYEAI5GAQEwCQYHBACORgEGAzAJBgcEAIvsSQECMIGgBgYEAIGYJwIwgZUwajApBgcEAIGYJwEEDB5DYXJkIEJhc2VkIFBheW1lbnQgSW5zdHJ1bWVudHMwHgYHBACBmCcBAwwTQWNjb3VudCBJbmZvcm1hdGlvbjAdBgcEAIGYJwECDBJQYXltZW50IEluaXRpYXRpb24MHUZvcmdlUm9jayBGaW5hbmNpYWwgQXV0aG9yaXR5DAhGUi1BQUFBQTANBgkqhkiG9w0BAQsFAAOCAQEAr46F0w4yXKm1tu6Z8eu9qmoyhBilrg+8YfFBbyZ1mv5nLtzx0oj4kzDQ1cO95ErKsIyn6jwIkaOgzum6JAV4+tkw\\/iimujWnlmp2tzkpj70CfX18t4pfAoMUAIeKxIAnd8j077W86npZrqNoT\\/M4xkhX1nmeWOagj+yexFIeiwN7DhHircmvZ26l3UKIjT92F4Qj5Hcw8AKgyCA2vTs7HnZ1tERyH3yQoXUjsiZOfT4EUj2NH+vtA990ngJ10El0eHVk9x+sSFI9TBPHR425u+g0du3RXIxhdUDg+5U05G4VMnP8fuR43K6JNHKEwm4CjSZKmus6IvuvPMH+XcFyoA==\"],\"n\":\"uHJPR1Ch-unT5fuVvy5gWv1kTnW9RHBMWjgs2rdNNpoi3g-_aKnLf9hNRLD6IxIP4XeViwsBrREkW8eHpSZfPkdVbJVny8t67xv5YcrC49iY_8y5RaN6io3uMBJ9Y2XLsKWbr5AqGslaiJWpvbjfZRp99zpRmrlwYfKUS67xm1BVoMGX-YPpNQgegmkPnAte02jT5AMUmx8XvR0Bm6eVlco-Ih64rrf9XM-soRLPO9dCBPwn6m7d8_5eSGIQwqt8QqOnhul1TCteGNyu868dKBkDMzA1pBohrxW2Mo7xG2ypX1fDmEFAPdJ0L4iE9kCtbcave2-68PCeEvnvTsk3lw\"}";

    @Mock
    private JwtAuthConfigurationProperties properties;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private ForgeRockAppMATLService userDetailsService;

    private Authenticator authenticator;

    @Before
    public void setUp() {
        given(properties.isCookie()).willReturn(true);
        authenticator = new Authenticator(properties, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @Test
    public void shouldAuthenticateWhenUsingCookie() {
        // Given
        String cookieValue = "value";
        JwtAuthenticationFailureHandler.RawAccessJwtToken token = new JwtAuthenticationFailureHandler.RawAccessJwtToken(
                JwtAuthenticationFailureHandler.RawAccessJwtToken.Type.ID_TOKEN, cookieValue);
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(token, null);
        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);
        given(authenticationManager.authenticate(captor.capture())).willReturn(jwtAuthenticationToken);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("obri-session", cookieValue));

        // When
        Authentication authentication = authenticator.authenticate(request, authenticationManager);

        // Then
        assertThat(authentication).isEqualToComparingFieldByFieldRecursively(jwtAuthenticationToken);
    }

    @Test
    public void shouldBindCertificateToAuthenticationWhenUsingCookieAndJwkClientHeader() throws ParseException {
        // Given
        String cookieValue = "value";
        JwtAuthenticationFailureHandler.RawAccessJwtToken token = new JwtAuthenticationFailureHandler.RawAccessJwtToken(
                JwtAuthenticationFailureHandler.RawAccessJwtToken.Type.ID_TOKEN, cookieValue);
        JWK jwk = JWK.parse(CERT_JWK);
        X509Certificate[] certs = jwk.getParsedX509CertChain().toArray(new X509Certificate[]{});
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(token, certs);
        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);
        given(authenticationManager.authenticate(captor.capture())).willReturn(jwtAuthenticationToken);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("obri-session", cookieValue));
        request.addHeader(CLIENT_JWK_HEADER_NAME, CERT_JWK);

        // When
        JwtAuthenticationToken authentication = (JwtAuthenticationToken) authenticator.authenticate(request, authenticationManager);

        // Then
        assertThat(authentication.getClientCerts()).isNotNull();
        assertThat(authentication.getClientCerts()).as("Authentication should contain certificates from X-Client-Jwk header").isEqualTo(certs);
    }

    @Test
    public void shouldAuthenticateWhenUsingCertificate() {
        // Given
        User user = new User("test", "test", Collections.singleton(new SimpleGrantedAuthority("PowerUser")));
        TestingAuthenticationToken certificateAuthentication = new TestingAuthenticationToken(user, null);
        SecurityContext context = new SecurityContextImpl(certificateAuthentication);
        SecurityContextHolder.setContext(context);

        // When
        Authentication authentication = authenticator.authenticate(new MockHttpServletRequest(), authenticationManager);

        // Then
        assertThat(authentication).isEqualToComparingFieldByFieldRecursively(certificateAuthentication);
    }

    @Test
    public void shouldAuthenticateWhenUsingJWKHeader() {
        // Given
        String jwk = "{\n" +
                "            \"kty\": \"RSA\",\n" +
                "            \"x5t#S256\": \"hztnswawqME4gf2h3iHWfGA6a8_N4s0DqtjR85vapM0\",\n" +
                "            \"e\": \"AQAB\",\n" +
                "            \"use\": \"enc\",\n" +
                "            \"kid\": \"ea59e418004c38652d68684cae0754699307b676\",\n" +
                "            \"x5c\": [\n" +
                "                \"MIIDlDCCAnygAwIBAgIUX62wNZk2L5KoXmGMtW1J9q3KcR8wDQYJKoZIhvcNAQELBQAwezELMAkGA1UEBhMCVUsxDTALBgNVBAgTBEF2b24xEDAOBgNVBAcTB0JyaXN0b2wxEjAQBgNVBAoTCUZvcmdlUm9jazEcMBoGA1UECxMTZm9yZ2Vyb2NrLmZpbmFuY2lhbDEZMBcGA1UEAxMQb2JyaS1leHRlcm5hbC1jYTAgFw0xODAxMjExNjQxNTlaGA8yMTE4MTIyODE2NDE1OVowgYoxKDAmBgNVBAMMH2FzLmFzcHNwLm9iLmZvcmdlcm9jay5maW5hbmNpYWwxHDAaBgNVBAsME2Zvcmdlcm9jay5maW5hbmNpYWwxEjAQBgNVBAoMCUZvcmdlUm9jazEQMA4GA1UEBwwHQnJpc3RvbDENMAsGA1UECAwEQXZvbjELMAkGA1UEBhMCVUswggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCMuZdKZZ+oeEnni1Jc4t3Q16riBoS+Y0SRPwhQaVz1RFaN9mfVaaZ0/RN1vsLyJ2SK9It1CDId1m0smck40YBnogmKrtNmxBbCIiTZmhfXVD+zNB2p0LMk/iynICbGcVAb7AGkm6IWqVb6r48GJCIQVgMAFSXHHgiYHyklHEo0eHVtcPMT5KNTQGtAVM5MlmxrWqa5uzB9Pd0dGCBa+MA74aUCI+m6QRU/lj8ll4XCb2UHb1YA06rZOCPkQBmqEb74hwAswvRFnrxl9D/1Xzq4LhdFP6+xRedmxacAAcvwcskBZo0SoGXAsGRFCfb0THNj6PqeItIXRTUYaFOU6xTrAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAF9VV/lERAdYVSrXrkJWMiWogY0t7X18f329/Xv4AzcN9EeCmJOII1sfKcZHfWc8M67MAMpk84TeP4gbBKlKOfc9POlJAQQjLOneFVjqdixAtUFXudIIWRd7dio4Le1G8524BlQF+MyLlpBYUNw/M0/LkpM4dTPPbccscZC04/2GaGJ3nrn+zOHlUz4F3MWaBTFgAkeA5+KMxa44lTvi08RRazZfsW/oKqbzGzpQgTzsT4YKXRsIc/QihURMS3JofUhr2qOj9V01qyU8DAR+XCMW6okf+bS0UblHJzKmkul6p+Go6gIGlYZ5kYIhPqOGlAZdCQIHH6Vdb3BI7K3vZ4M=\",\n" +
                "                \"MIIDozCCAougAwIBAgIEJoO/8jANBgkqhkiG9w0BAQsFADB7MQswCQYDVQQGEwJVSzENMAsGA1UECBMEQXZvbjEQMA4GA1UEBxMHQnJpc3RvbDESMBAGA1UEChMJRm9yZ2VSb2NrMRwwGgYDVQQLExNmb3JnZXJvY2suZmluYW5jaWFsMRkwFwYDVQQDExBvYnJpLWV4dGVybmFsLWNhMB4XDTE4MDgwMjE2MTg1NVoXDTI4MDczMDE2MTg1NVowezELMAkGA1UEBhMCVUsxDTALBgNVBAgTBEF2b24xEDAOBgNVBAcTB0JyaXN0b2wxEjAQBgNVBAoTCUZvcmdlUm9jazEcMBoGA1UECxMTZm9yZ2Vyb2NrLmZpbmFuY2lhbDEZMBcGA1UEAxMQb2JyaS1leHRlcm5hbC1jYTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAIs4bQZ1tVO08KYv/nAOn/xo+hKHq52IMmyEvhuo07wM1W+hhcxlmceL/0Rjq8mGVqMMoUDFS+SIa+6Tqgt56fZgud+O7KmGWTPDZXDofecYFT56b64u6Bi0Psp5afsSst3gWyCV5b7DaxlZoQ2H8BfsERnmJuGG/633p3LPNzKTxTFGRfNsWIoHi+Bw4GVL0STGxC3BCCEWr7JYUpYbuAgL1T17oYJQq38BwLl5liu+W7zu7nOqGJlenBYwhfPS8vl2Unr4HLSs8mn1XYnD7J+Bn8rN8kBsWozBsD4Hb3A93omiM8c8ClGzuquCVg7NjRTuc7n2+5eLsvS7siWjApsCAwEAAaMvMC0wDAYDVR0TBAUwAwEB/zAdBgNVHQ4EFgQUJ2HaSAFW+VzVjRpb086QeYIyn/UwDQYJKoZIhvcNAQELBQADggEBAFbcKVhpvmCZz/xSAkNJr8ilXK6gukNLPD/BdNKOflfMf0VwyJRbgzyZ7LEcY4pLU7U/QCAhg6BQ9UgzL8jFv0jKOXi3DUNG5Dxevcb5fJyEgded2nJvKLJ9qHCE4Q8IGMWnIFX6IvIiizKXrJgi2GXpXmPrkZin98zM7yn3R0TaHOHo5ARG8n8UmhuQKPkoLoawu9n97XJ+KB27Y8mC5uYbKeFL3ocw6G2oA49vuCyWFHuZAMLBxd6H9fQVtozKR2rzlbnpo3sb4iJvfid7o57ybMt7CpDCOmTK2QJXgIl/mTsMz2TrnbI1OosGZX7HkSfzphNj0mT8P8ZM971lPYY=\"\n" +
                "            ],\n" +
                "            \"alg\": \"RSA-OAEP-256\",\n" +
                "            \"n\": \"jLmXSmWfqHhJ54tSXOLd0Neq4gaEvmNEkT8IUGlc9URWjfZn1WmmdP0Tdb7C8idkivSLdQgyHdZtLJnJONGAZ6IJiq7TZsQWwiIk2ZoX11Q_szQdqdCzJP4spyAmxnFQG-wBpJuiFqlW-q-PBiQiEFYDABUlxx4ImB8pJRxKNHh1bXDzE-SjU0BrQFTOTJZsa1qmubswfT3dHRggWvjAO-GlAiPpukEVP5Y_JZeFwm9lB29WANOq2Tgj5EAZqhG--IcALML0RZ68ZfQ_9V86uC4XRT-vsUXnZsWnAAHL8HLJAWaNEqBlwLBkRQn29ExzY-j6niLSF0U1GGhTlOsU6w\"\n" +
                "        }";
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("PowerUser"));
        UserContext context = UserContext.create("test", authorities, UserContext.UserType.ANONYMOUS);
        given(userDetailsService.isInternalForgeRockApp(any())).willReturn(Optional.empty());
        given(userDetailsService.isExternalForgeRockAppFromJWKHeader(jwk)).willReturn(Optional.of(context));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(CLIENT_JWK_HEADER_NAME, jwk);

        // When
        Authentication authentication = authenticator.authenticate(request, authenticationManager);

        // Then
        UsernamePasswordAuthenticationToken expected = new UsernamePasswordAuthenticationToken(context, "", context.getAuthorities());
        assertThat(authentication).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    public void shouldBeAnonymousWhenNoAuthentication() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // When
        Authentication authentication = authenticator.authenticate(request, authenticationManager);

        // Then
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(OBRIRole.ROLE_ANONYMOUS);
        UserContext context = UserContext.create("anonymous", authorities, UserContext.UserType.ANONYMOUS);
        assertThat(authentication.getPrincipal()).isEqualToComparingFieldByFieldRecursively(context);
        assertThat(authentication.getAuthorities()).isEqualTo(context.getAuthorities());
    }

}