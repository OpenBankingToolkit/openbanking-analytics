/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.services.openbanking.converter;

import com.forgerock.openbanking.analytics.model.openbanking.v2_0.account.FRStandingOrder2;
import com.forgerock.openbanking.analytics.model.openbanking.v3_1.account.FRStandingOrder4;
import org.joda.time.DateTime;
import org.junit.Test;
import uk.org.openbanking.datamodel.account.*;
import uk.org.openbanking.datamodel.payment.OBActiveOrHistoricCurrencyAndAmount;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class FRStandingOrderConverterTest {
    private static final String ACCOUNT_ID = "12345";
    private static final String ID = "111";
    private static final Date CREATED_DATE = new Date();
    private static final Date UPDATED_DATE = new Date();
    private static final String PISP_ID = "ABC1";

    @Test
    public void toStandingOrder2() {
        FRStandingOrderConverter frStandingOrderConverter = new FRStandingOrderConverter();

        FRStandingOrder4 frStandingOrder4 =  FRStandingOrder4.builder()
                .accountId(ACCOUNT_ID)
                .created(CREATED_DATE)
                .id(ID)
                .pispId(PISP_ID)
                .updated(UPDATED_DATE)
                .standingOrder(new OBStandingOrder4()
                    .firstPaymentAmount(new OBActiveOrHistoricCurrencyAndAmount().amount("100").currency("GBP"))
                    .finalPaymentAmount(new OBActiveOrHistoricCurrencyAndAmount().amount("200").currency("GBP"))
                    .nextPaymentAmount(new OBActiveOrHistoricCurrencyAndAmount().amount("150").currency("GBP"))
                    .firstPaymentDateTime(DateTime.now())
                    .finalPaymentDateTime(DateTime.now())
                    .nextPaymentDateTime(DateTime.now())
                    .creditorAccount(new OBCashAccount3().identification("acc1"))
                    .standingOrderId("std123")
                    .standingOrderStatusCode(OBExternalStandingOrderStatus1Code.ACTIVE)
                    .frequency("IntvDay")
                    .reference("ref1")
                    .accountId(ACCOUNT_ID)
                    .supplementaryData(new OBSupplementaryData1())
                    .creditorAgent(new OBBranchAndFinancialInstitutionIdentification4().identification("abc").schemeName("BICFI"))

                )
                .build();

        //
        FRStandingOrder2 frStandingOrder2 = frStandingOrderConverter.toStandingOrder2(frStandingOrder4);

        assertThat(frStandingOrder2.getAccountId()).isEqualTo(ACCOUNT_ID);
        assertThat(frStandingOrder2.getCreated()).isEqualTo(CREATED_DATE);
        assertThat(frStandingOrder2.getUpdated()).isEqualTo(UPDATED_DATE);
        assertThat(frStandingOrder2.getId()).isEqualTo(ID);

        OBStandingOrder2 obStandingOrder2 = frStandingOrder2.getStandingOrder();
        assertThat(obStandingOrder2.getFirstPaymentAmount()).isEqualTo(frStandingOrder4.getStandingOrder().getFirstPaymentAmount());
        assertThat(obStandingOrder2.getFinalPaymentAmount()).isEqualTo(frStandingOrder4.getStandingOrder().getFinalPaymentAmount());
        assertThat(obStandingOrder2.getNextPaymentAmount()).isEqualTo(frStandingOrder4.getStandingOrder().getNextPaymentAmount());
        assertThat(obStandingOrder2.getFirstPaymentDateTime()).isEqualTo(frStandingOrder4.getStandingOrder().getFirstPaymentDateTime());
        assertThat(obStandingOrder2.getFinalPaymentDateTime()).isEqualTo(frStandingOrder4.getStandingOrder().getFinalPaymentDateTime());
        assertThat(obStandingOrder2.getNextPaymentDateTime()).isEqualTo(frStandingOrder4.getStandingOrder().getNextPaymentDateTime());
        assertThat(obStandingOrder2.getCreditorAccount().getIdentification()).isEqualTo(frStandingOrder4.getStandingOrder().getCreditorAccount().getIdentification());
        assertThat(obStandingOrder2.getStandingOrderId()).isEqualTo(frStandingOrder4.getStandingOrder().getStandingOrderId());
        assertThat(obStandingOrder2.getStandingOrderStatusCode()).isEqualTo(frStandingOrder4.getStandingOrder().getStandingOrderStatusCode());
        assertThat(obStandingOrder2.getFrequency()).isEqualTo(frStandingOrder4.getStandingOrder().getFrequency());
        assertThat(obStandingOrder2.getReference()).isEqualTo(frStandingOrder4.getStandingOrder().getReference());
        assertThat(obStandingOrder2.getAccountId()).isEqualTo(frStandingOrder4.getStandingOrder().getAccountId());
        assertThat(obStandingOrder2.getCreditorAgent().getSchemeName().name()).isEqualTo(frStandingOrder4.getStandingOrder().getCreditorAgent().getSchemeName());

    }
}