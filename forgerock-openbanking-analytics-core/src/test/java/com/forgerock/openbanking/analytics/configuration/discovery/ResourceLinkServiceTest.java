/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.configuration.discovery;

import com.forgerock.openbanking.analytics.model.openbanking.v3_0.payment.FRDomesticConsent1;
import com.forgerock.openbanking.analytics.model.openbanking.v3_1.payment.FRDomesticConsent2;
import com.forgerock.openbanking.analytics.model.openbanking.v3_1.payment.FRDomesticPaymentSubmission2;
import com.github.jsonzou.jmockdata.JMockData;
import org.junit.Before;
import org.junit.Test;
import uk.org.openbanking.datamodel.account.Links;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceLinkServiceTest {
    private ResourceLinkService resourceLinkService;
    private DiscoveryConfigurationProperties discoveryProps;

    @Before
    public void setUp() {
        discoveryProps = JMockData.mock(DiscoveryConfigurationProperties.class);
        resourceLinkService = new ResourceLinkService(discoveryProps);
    }

    @Test
    public void consent_toSelfLink() {
        // Given
        discoveryProps.getApis().getPayments().getV_3_0().setGetDomesticPaymentConsent("http://localhost/open-banking/v3.0/domestic-payment-consent/{ConsentId}");
        FRDomesticConsent1 consent = FRDomesticConsent1.builder().id("123").build();

        // When
        Links links = resourceLinkService.toSelfLink(consent, d -> d.getV_3_0().getGetDomesticPaymentConsent());

        // Then
        assertThat(links.getSelf()).isEqualTo("http://localhost/open-banking/v3.0/domestic-payment-consent/123");
    }

    @Test
    public void paymentSubmission_toSelfLink() {
        // Given
        discoveryProps.getApis().getPayments().getV_3_1().setGetDomesticPayment("http://localhost/open-banking/v3.1/domestic-payment/{DomesticPaymentId}");
        FRDomesticPaymentSubmission2 consent = FRDomesticPaymentSubmission2.builder().id("123").build();

        // When
        Links links = resourceLinkService.toSelfLink(consent, d -> d.getV_3_1().getGetDomesticPayment());

        // Then
        assertThat(links.getSelf()).isEqualTo("http://localhost/open-banking/v3.1/domestic-payment/123");
    }
}