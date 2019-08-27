/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.openbanking.forgerock.filepayment.v3_0.report;

import com.forgerock.openbanking.analytics.exception.OBErrorResponseException;
import com.forgerock.openbanking.analytics.model.openbanking.forgerock.ConsentStatusCode;
import com.forgerock.openbanking.analytics.model.openbanking.forgerock.filepayment.v3_0.PaymentFileType;
import com.forgerock.openbanking.analytics.model.openbanking.v3_0.payment.FRFileConsent1;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.org.openbanking.datamodel.payment.OBFile1;
import uk.org.openbanking.datamodel.payment.OBWriteDataFileConsent1;
import uk.org.openbanking.datamodel.payment.OBWriteFileConsent1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PaymentReportFile1ServiceTest {
    @Mock
    OBIEPaymentInitiationReport1Builder jsonBuilder;
    @Mock
    OBIEPainXmlReport1Builder xmlBuilder;

    @InjectMocks
    PaymentReportFile1Service paymentReportFile1Service;

    @Test
    public void chooseJsonPaymentReportBuilder() throws Exception{
        // Given
        final String expectedReport = "{\"Data\": {}}";
        FRFileConsent1 consent = FRFileConsent1.builder()
                .writeFileConsent(new OBWriteFileConsent1()
                        .data(new OBWriteDataFileConsent1().initiation(new OBFile1()
                        .fileType(PaymentFileType.UK_OBIE_PAYMENT_INITIATION_V3_0.getFileType())
                )))
                .fileContent("{}")
                .status(ConsentStatusCode.ACCEPTEDSETTLEMENTINPROCESS)
                .build();
        given(jsonBuilder.toPaymentReport(eq(consent))).willReturn(expectedReport);

        // When
        final String paymentReport = paymentReportFile1Service.createPaymentReport(consent);

        // Then
        assertThat(paymentReport).isEqualTo(expectedReport);
    }

    @Test
    public void chooseXmlPaymentReportBuilder() throws Exception {
        // Given
        final String expectedReport = "<data/>";
        FRFileConsent1 consent = FRFileConsent1.builder()
                .writeFileConsent(new OBWriteFileConsent1().data(new OBWriteDataFileConsent1().initiation(new OBFile1().fileType(
                        PaymentFileType.UK_OBIE_PAIN_001.getFileType()
                ))))
                .fileContent("<xml/>")
                .status(ConsentStatusCode.ACCEPTEDSETTLEMENTINPROCESS)
                .build();
        given(xmlBuilder.toPaymentReport(eq(consent))).willReturn(expectedReport);

        // When
        final String paymentReport = paymentReportFile1Service.createPaymentReport(consent);

        // Then
        assertThat(paymentReport).isEqualTo(expectedReport);
    }

    @Test
    public void pendingConsent_reportNotReady() {
        // Given
        final String expectedReport = "<data/>";
        FRFileConsent1 consent = FRFileConsent1.builder()
                .writeFileConsent(new OBWriteFileConsent1().data(new OBWriteDataFileConsent1().initiation(new OBFile1().fileType(
                        PaymentFileType.UK_OBIE_PAIN_001.getFileType()
                ))))
                .fileContent("<xml/>")
                .status(ConsentStatusCode.PENDING)
                .build();

        // When
        assertThatThrownBy(()->
        paymentReportFile1Service.createPaymentReport(consent))
        // Then
        .isInstanceOf(OBErrorResponseException.class);
    }
}