/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.auth.jwt;

import com.forgerock.openbanking.analytics.auth.OBRIRole;
import com.forgerock.openbanking.analytics.auth.UserContext;
import com.forgerock.openbanking.analytics.exception.OBErrorAuthenticationException;
import com.forgerock.openbanking.analytics.model.error.OBRIErrorType;
import com.forgerock.openbanking.analytics.services.token.SessionService;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Collections;

import static com.forgerock.cert.SubjectHash.hash;
import static com.forgerock.openbanking.analytics.auth.jwt.JwtAuthenticationFailureHandler.RawAccessJwtToken.Type.SSO_TOKEN;
import static com.forgerock.openbanking.analytics.openbanking.OpenBankingConstants.SSOClaim.MTLS_SUBJECT_HASH;
import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.apis.BDDCatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class JwtAuthenticationProviderTest {

    private static final String CERT_JWK = "{\"kty\":\"RSA\",\"x5t#S256\":\"foZp8HwyfS5uViA-A7ZlhMxr91tWzDH0YCz7f6iMeos\",\"e\":\"AQAB\",\"kid\":\"106975682923824153417588681451978436149874917717\",\"x5c\":[\"MIIFfDCCBGSgAwIBAgIUErz0nWF6uQi0Pm2CzqSWP51+AVUwDQYJKoZIhvcNAQELBQAwezELMAkGA1UEBhMCVUsxDTALBgNVBAgTBEF2b24xEDAOBgNVBAcTB0JyaXN0b2wxEjAQBgNVBAoTCUZvcmdlUm9jazEcMBoGA1UECxMTZm9yZ2Vyb2NrLmZpbmFuY2lhbDEZMBcGA1UEAxMQb2JyaS1leHRlcm5hbC1jYTAgFw0xODA2MTcxNTA4NDJaGA8yMTE5MDUyNDE1MDg0Mlowgb0xLTArBgNVBAMMJDI0NDZkNzM0LWJjMGYtNDAxZC1iZjFjLWYxNzJhM2Y3MmI5ZTEhMB8GA1UECwwYNWNmYTVhYmU3NTkwYjAwMWE0MTdiNTUxMRIwEAYDVQQKDAlGb3JnZVJvY2sxEDAOBgNVBAcMB0JyaXN0b2wxDTALBgNVBAgMBEF2b24xCzAJBgNVBAYTAlVLMScwJQYDVQRhDB5QU0RHQi01Y2ZhNWFiZTc1OTBiMDAxYTQxN2I1NTEwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC4ck9HUKH66dPl+5W\\/LmBa\\/WROdb1EcExaOCzat002miLeD79oqct\\/2E1EsPojEg\\/hd5WLCwGtESRbx4elJl8+R1VslWfLy3rvG\\/lhysLj2Jj\\/zLlFo3qKje4wEn1jZcuwpZuvkCoayVqIlam9uN9lGn33OlGauXBh8pRLrvGbUFWgwZf5g+k1CB6CaQ+cC17TaNPkAxSbHxe9HQGbp5WVyj4iHriut\\/1cz6yhEs8710IE\\/Cfqbt3z\\/l5IYhDCq3xCo6eG6XVMK14Y3K7zrx0oGQMzMDWkGiGvFbYyjvEbbKlfV8OYQUA90nQviIT2QK1txq97b7rw8J4S+e9OyTeXAgMBAAGjggGxMIIBrTCB1AYIKwYBBQUHAQEEgccwgcQwYAYIKwYBBQUHMAKGVGh0dHBzOi8vc2VydmljZS5kaXJlY3RvcnkuZGV2LW9iLmZvcmdlcm9jay5maW5hbmNpYWw6ODA3NC9hcGkvZGlyZWN0b3J5L2tleXMvandrX3VyaTBgBggrBgEFBQcwAYZUaHR0cHM6Ly9zZXJ2aWNlLmRpcmVjdG9yeS5kZXYtb2IuZm9yZ2Vyb2NrLmZpbmFuY2lhbDo4MDc0L2FwaS9kaXJlY3Rvcnkva2V5cy9qd2tfdXJpMIHTBggrBgEFBQcBAwSBxjCBwzAIBgYEAI5GAQEwCQYHBACORgEGAzAJBgcEAIvsSQECMIGgBgYEAIGYJwIwgZUwajApBgcEAIGYJwEEDB5DYXJkIEJhc2VkIFBheW1lbnQgSW5zdHJ1bWVudHMwHgYHBACBmCcBAwwTQWNjb3VudCBJbmZvcm1hdGlvbjAdBgcEAIGYJwECDBJQYXltZW50IEluaXRpYXRpb24MHUZvcmdlUm9jayBGaW5hbmNpYWwgQXV0aG9yaXR5DAhGUi1BQUFBQTANBgkqhkiG9w0BAQsFAAOCAQEAr46F0w4yXKm1tu6Z8eu9qmoyhBilrg+8YfFBbyZ1mv5nLtzx0oj4kzDQ1cO95ErKsIyn6jwIkaOgzum6JAV4+tkw\\/iimujWnlmp2tzkpj70CfX18t4pfAoMUAIeKxIAnd8j077W86npZrqNoT\\/M4xkhX1nmeWOagj+yexFIeiwN7DhHircmvZ26l3UKIjT92F4Qj5Hcw8AKgyCA2vTs7HnZ1tERyH3yQoXUjsiZOfT4EUj2NH+vtA990ngJ10El0eHVk9x+sSFI9TBPHR425u+g0du3RXIxhdUDg+5U05G4VMnP8fuR43K6JNHKEwm4CjSZKmus6IvuvPMH+XcFyoA==\"],\"n\":\"uHJPR1Ch-unT5fuVvy5gWv1kTnW9RHBMWjgs2rdNNpoi3g-_aKnLf9hNRLD6IxIP4XeViwsBrREkW8eHpSZfPkdVbJVny8t67xv5YcrC49iY_8y5RaN6io3uMBJ9Y2XLsKWbr5AqGslaiJWpvbjfZRp99zpRmrlwYfKUS67xm1BVoMGX-YPpNQgegmkPnAte02jT5AMUmx8XvR0Bm6eVlco-Ih64rrf9XM-soRLPO9dCBPwn6m7d8_5eSGIQwqt8QqOnhul1TCteGNyu868dKBkDMzA1pBohrxW2Mo7xG2ypX1fDmEFAPdJ0L4iE9kCtbcave2-68PCeEvnvTsk3lw\"}";

    @Mock
    private SessionService sessionService;
    @InjectMocks
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Test
    public void shouldBeAnonymousWhenNoToken() {
        // Given
        Authentication authentication = new JwtAuthenticationToken(null, Collections.emptyList());

        // When
        JwtAuthenticationToken authenticate = (JwtAuthenticationToken) jwtAuthenticationProvider.authenticate(authentication);

        // Then
        assertThat(authenticate.getAuthorities()).contains(OBRIRole.ROLE_ANONYMOUS);
    }

    @Test
    public void shouldAuthenticateWhenSsoToken() throws ParseException {
        // Given
        JwtAuthenticationFailureHandler.RawAccessJwtToken token = new JwtAuthenticationFailureHandler.RawAccessJwtToken(SSO_TOKEN, "");
        Authentication authentication = new JwtAuthenticationToken(token, null);
        given(sessionService.getUserContext(token.getToken())).willReturn(UserContext.builder()
                .authorities(Collections.singletonList(OBRIRole.ROLE_DATA))
                .claims(new JWTClaimsSet.Builder()
                        .build())
                .build());

        // When
        JwtAuthenticationToken authenticate = (JwtAuthenticationToken) jwtAuthenticationProvider.authenticate(authentication);

        // Then
        assertThat(authenticate.getAuthorities()).contains(OBRIRole.ROLE_DATA);
    }

    @Test
    public void shouldAuthenticateWhenSsoTokenAndMTLS() throws ParseException, CertificateEncodingException {
        // Given
        JwtAuthenticationFailureHandler.RawAccessJwtToken token = new JwtAuthenticationFailureHandler.RawAccessJwtToken(SSO_TOKEN, "");
        JWK jwk = JWK.parse(CERT_JWK);
        X509Certificate[] certs = jwk.getParsedX509CertChain().toArray(new X509Certificate[]{});
        Authentication authentication = new JwtAuthenticationToken(token, certs);
        given(sessionService.getUserContext(token.getToken())).willReturn(UserContext.builder()
                .authorities(Collections.singletonList(OBRIRole.ROLE_DATA))
                .claims(new JWTClaimsSet.Builder().claim(MTLS_SUBJECT_HASH, hash(certs).get())
                        .build())
                .build());

        // When
        JwtAuthenticationToken authenticate = (JwtAuthenticationToken) jwtAuthenticationProvider.authenticate(authentication);

        // Then
        assertThat(authenticate.getAuthorities()).contains(OBRIRole.ROLE_DATA);
    }

    @Test
    public void shouldNotAuthenticateWhenSsoTokenAndWrongMTLS() throws ParseException {
        // Given
        JwtAuthenticationFailureHandler.RawAccessJwtToken token = new JwtAuthenticationFailureHandler.RawAccessJwtToken(SSO_TOKEN, "");
        JWK jwk = JWK.parse(CERT_JWK);
        X509Certificate[] certs = jwk.getParsedX509CertChain().toArray(new X509Certificate[]{});
        Authentication authentication = new JwtAuthenticationToken(token, certs);
        String differentHash = "123";
        given(sessionService.getUserContext(token.getToken())).willReturn(UserContext.builder()
                .authorities(Collections.singletonList(OBRIRole.ROLE_DATA))
                .claims(new JWTClaimsSet.Builder().claim(MTLS_SUBJECT_HASH, differentHash)
                        .build())
                .build());

        // When
        catchException(() -> jwtAuthenticationProvider.authenticate(authentication));

        // Then exception thrown
        assertThat(caughtException()).isInstanceOf(OBErrorAuthenticationException.class);
        assertThat(caughtException()).hasMessageContaining(OBRIErrorType.CLIENT_CERTIFICATE_NOT_MATCH.getCode().getValue());
    }

    @Test
    public void shouldNotAuthenticateWhenSsoTokenAndNoMTLS() throws ParseException {
        // Given
        JwtAuthenticationFailureHandler.RawAccessJwtToken token = new JwtAuthenticationFailureHandler.RawAccessJwtToken(SSO_TOKEN, "");
        Authentication authentication = new JwtAuthenticationToken(token, null);
        given(sessionService.getUserContext(token.getToken())).willReturn(UserContext.builder()
                .authorities(Collections.singletonList(OBRIRole.ROLE_DATA))
                .claims(new JWTClaimsSet.Builder().claim(MTLS_SUBJECT_HASH, "123")
                        .build())
                .build());

        // When
        catchException(() -> jwtAuthenticationProvider.authenticate(authentication));

        // Then exception thrown
        assertThat(caughtException()).isInstanceOf(OBErrorAuthenticationException.class);
        assertThat(caughtException()).hasMessageContaining(OBRIErrorType.CLIENT_CERTIFICATE_NOT_PROVIDED.getCode().getValue());
    }
}