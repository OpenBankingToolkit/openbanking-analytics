/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.services.openbanking.converter.transaction;

import com.forgerock.openbanking.analytics.model.openbanking.v3_1.account.FRTransaction4;
import com.forgerock.openbanking.analytics.model.openbanking.v3_1_1.account.FRTransaction5;
import org.joda.time.DateTime;
import org.junit.Test;
import uk.org.openbanking.datamodel.account.*;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;


public class FRTransactionConverterTest {

    @Test
    public void toTransaction4() {
        // Given
        DateTime now = DateTime.now();
        FRTransaction5 frTransaction5 = FRTransaction5.builder()
                .accountId("123")
                .bookingDateTime(now)
                .id("222")
                .statementIds(Collections.singletonList("4523"))
                .created(now.minusDays(2).toDate())
                .updated(now.minusDays(1).toDate())
                .transaction(new OBTransaction5()
                        .amount(new OBActiveOrHistoricCurrencyAndAmount().amount("10.0"))
                        .bookingDateTime(now)
                        .bankTransactionCode(new OBBankTransactionCodeStructure1().code("dw2fyjih"))
                        .balance(new OBTransactionCashBalance().amount(new uk.org.openbanking.datamodel.payment.OBActiveOrHistoricCurrencyAndAmount().amount("34.0")))
                        .chargeAmount(new OBActiveOrHistoricCurrencyAndAmount().amount("45.85"))
                        .cardInstrument(new OBTransactionCardInstrument1().identification("dfwfejhw"))
                        .creditDebitIndicator(OBTransaction5.CreditDebitIndicatorEnum.CREDIT)
                        .creditorAgent(new OBBranchAndFinancialInstitutionIdentification6().identification("38987fef"))
                        .creditorAccount(new OBCashAccount6().identification("fewfwhkfk"))
                        .status(OBEntryStatus1Code.BOOKED)
                        .proprietaryBankTransactionCode(new OBTransaction5ProprietaryBankTransactionCode().code("3fwefw"))
                        .transactionId("TX44353")
                        .transactionReference("hkhkjhkjhjkh")
                        .valueDateTime(now.plusDays(1))
                )
                .build();

        // When
        FRTransaction4 frTransaction4 = FRTransactionConverter.toTransaction4(frTransaction5);

        // Then
        assertThat(frTransaction4.getAccountId()).isEqualTo(frTransaction5.getAccountId());
        assertThat(frTransaction4.getCreated()).isEqualTo(frTransaction5.getCreated());
        assertThat(frTransaction4.getUpdated()).isEqualTo(frTransaction5.getUpdated());
        assertThat(frTransaction4.getId()).isEqualTo(frTransaction5.getId());
        assertThat(frTransaction4.getBookingDateTime()).isEqualTo(frTransaction5.getBookingDateTime());

        OBTransaction4 obTransaction4 = frTransaction4.getTransaction();
        OBTransaction5 obTransaction5 = frTransaction5.getTransaction();

        assertThat(obTransaction4.getAmount().getAmount()).isEqualTo(obTransaction5.getAmount().getAmount());
        assertThat(obTransaction4.getBookingDateTime()).isEqualTo(obTransaction5.getBookingDateTime());
        assertThat(obTransaction4.getBankTransactionCode().getCode()).isEqualTo(obTransaction5.getBankTransactionCode().getCode());
        assertThat(obTransaction4.getBalance().getAmount()).isEqualTo(obTransaction5.getBalance().getAmount());
        assertThat(obTransaction4.getChargeAmount().getAmount()).isEqualTo(obTransaction5.getChargeAmount().getAmount());
        assertThat(obTransaction4.getCardInstrument()).isEqualTo(obTransaction5.getCardInstrument());
        assertThat(obTransaction4.getCreditDebitIndicator().name()).isEqualTo(obTransaction5.getCreditDebitIndicator().name());
        assertThat(obTransaction4.getCreditorAgent().getIdentification()).isEqualTo(obTransaction5.getCreditorAgent().getIdentification());
        assertThat(obTransaction4.getStatus()).isEqualTo(obTransaction5.getStatus());
        assertThat(obTransaction4.getProprietaryBankTransactionCode().getCode()).isEqualTo(obTransaction5.getProprietaryBankTransactionCode().getCode());
        assertThat(obTransaction4.getTransactionReference()).isEqualTo(obTransaction5.getTransactionReference());
        assertThat(obTransaction4.getValueDateTime()).isEqualTo(obTransaction5.getValueDateTime());

    }
}