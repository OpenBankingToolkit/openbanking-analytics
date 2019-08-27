/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.openbanking.forgerock.filepayment;

import com.forgerock.openbanking.analytics.model.openbanking.forgerock.filepayment.v3_0.FRFilePayment;
import com.forgerock.openbanking.analytics.model.openbanking.forgerock.filepayment.v3_0.OBIEPain001File;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class OBIEPain001FileTest {

    @Test
    public void parseExampleXmlFile() throws Exception {
        // Given
        String fileContent = utf8FileToString.apply("payment/OBIEExamplePain001.xml");

        // When
        final OBIEPain001File obiePain001File = new OBIEPain001File(fileContent);

        // Then
        assertThat(obiePain001File.getContentType()).isEqualTo(MediaType.TEXT_XML);
        assertThat(obiePain001File.getControlSum()).isEqualTo(new BigDecimal("11500000"));
        assertThat(obiePain001File.getNumberOfTransactions()).isEqualTo(3);

        final FRFilePayment frFilePayment1 = obiePain001File.getPayments().get(0);
        assertThat(frFilePayment1.getStatus()).isEqualTo(FRFilePayment.PaymentStatus.PENDING);
        assertThat(frFilePayment1.getCreditorAccountIdentification()).isEqualTo("23683707994125");
        assertThat(frFilePayment1.getInstructedAmount().getAmount()).isEqualTo("10000000");
        assertThat(frFilePayment1.getInstructedAmount().getCurrency()).isEqualTo("JPY");
        assertThat(frFilePayment1.getRemittanceReference()).isEqualTo("1234567");

        assertThat( obiePain001File.getPayments().get(2).getRemittanceUnstructured()).isEqualTo("RemittanceInformation3");
    }

    private static final Function<String, String> utf8FileToString = fileName -> {
        try {
            return Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    };
}