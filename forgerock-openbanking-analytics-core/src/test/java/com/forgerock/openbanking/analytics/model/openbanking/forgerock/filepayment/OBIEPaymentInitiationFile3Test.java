/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.openbanking.forgerock.filepayment;

import com.forgerock.openbanking.analytics.model.openbanking.forgerock.filepayment.v3_0.FRFilePayment;
import com.forgerock.openbanking.analytics.model.openbanking.forgerock.filepayment.v3_0.OBIEPaymentInitiationFile3;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class OBIEPaymentInitiationFile3Test {

    @Test
    public void parseExampleJsonFile() throws Exception {
        // Given
        String fileContent = utf8FileToString.apply("payment/OBIEPaymentInitiation_3_0.json");

        // When
        final OBIEPaymentInitiationFile3 obiePaymentInitiationFile3 = new OBIEPaymentInitiationFile3(fileContent);

        // Then
        assertThat(obiePaymentInitiationFile3.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(obiePaymentInitiationFile3.getControlSum()).isEqualTo(new BigDecimal("66.00"));
        assertThat(obiePaymentInitiationFile3.getNumberOfTransactions()).isEqualTo(3);

        final FRFilePayment frFilePayment1 = obiePaymentInitiationFile3.getPayments().get(0);
        assertThat(frFilePayment1.getStatus()).isEqualTo(FRFilePayment.PaymentStatus.PENDING);
        assertThat(frFilePayment1.getCreditorAccountIdentification()).isEqualTo("08080021325698");
        assertThat(frFilePayment1.getInstructedAmount().getAmount()).isEqualTo("21.00");
        assertThat(frFilePayment1.getInstructedAmount().getCurrency()).isEqualTo("GBP");
        assertThat(frFilePayment1.getRemittanceReference()).isEqualTo("FRESCO-037");
        assertThat(frFilePayment1.getRemittanceUnstructured()).isEqualTo("Internal ops code 5120103");
    }

    private static final Function<String, String> utf8FileToString = fileName -> {
        try {
            return Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    };
}