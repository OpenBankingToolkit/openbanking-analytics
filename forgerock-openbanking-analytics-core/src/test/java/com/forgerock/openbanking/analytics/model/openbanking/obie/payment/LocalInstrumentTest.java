/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.openbanking.obie.payment;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalInstrumentTest {

    @Test
    public void isEqual_balanceTransfer() {
        assertThat(LocalInstrument.UK_OBIE_BalanceTransfer.getNamespacedValue()).isEqualTo("UK.OBIE.BalanceTransfer");

        assertThat(LocalInstrument.UK_OBIE_BalanceTransfer.isEqual("UK.OBIE.BalanceTransfer")).isTrue();
        assertThat(LocalInstrument.UK_OBIE_BalanceTransfer.isEqual("BalanceTransfer")).isTrue();

        assertThat(LocalInstrument.UK_OBIE_BalanceTransfer.isEqual("Balance.Transfer")).isFalse();
        assertThat(LocalInstrument.UK_OBIE_BalanceTransfer.isEqual("UK.OBIE.MoneyTransfer")).isFalse();
        assertThat(LocalInstrument.UK_OBIE_BalanceTransfer.isEqual("")).isFalse();
        assertThat(LocalInstrument.UK_OBIE_BalanceTransfer.isEqual(null)).isFalse();
    }

    @Test
    public void isEqual_moneyTransfer() {
        assertThat(LocalInstrument.UK_OBIE_MoneyTransfer.getNamespacedValue()).isEqualTo("UK.OBIE.MoneyTransfer");

        assertThat(LocalInstrument.UK_OBIE_MoneyTransfer.isEqual("UK.OBIE.MoneyTransfer")).isTrue();
        assertThat(LocalInstrument.UK_OBIE_MoneyTransfer.isEqual("MoneyTransfer")).isTrue();

        assertThat(LocalInstrument.UK_OBIE_MoneyTransfer.isEqual("Money.Transfer")).isFalse();
        assertThat(LocalInstrument.UK_OBIE_MoneyTransfer.isEqual("UK.OBIE.BalanceTransfer")).isFalse();
        assertThat(LocalInstrument.UK_OBIE_MoneyTransfer.isEqual("")).isFalse();
        assertThat(LocalInstrument.UK_OBIE_MoneyTransfer.isEqual(null)).isFalse();
    }

    @Test
    public void isEqual_paym() {
        assertThat(LocalInstrument.UK_OBIE_Paym.getNamespacedValue()).isEqualTo("UK.OBIE.Paym");

        assertThat(LocalInstrument.UK_OBIE_Paym.isEqual("UK.OBIE.Paym")).isTrue();
        assertThat(LocalInstrument.UK_OBIE_Paym.isEqual("Paym")).isTrue();

        assertThat(LocalInstrument.UK_OBIE_Paym.isEqual("Pay M")).isFalse();
        assertThat(LocalInstrument.UK_OBIE_Paym.isEqual("UK.OBIE.BalanceTransfer")).isFalse();
        assertThat(LocalInstrument.UK_OBIE_Paym.isEqual("")).isFalse();
        assertThat(LocalInstrument.UK_OBIE_Paym.isEqual(null)).isFalse();
    }
}