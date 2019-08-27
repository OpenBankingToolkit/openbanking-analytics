/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.openbanking.obie.payment;

import org.junit.Test;
import uk.org.openbanking.datamodel.account.OBCashAccount3;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountSchemeNameTest {

    @Test
    public void PAN() {
        assertThat(AccountSchemeName.UK_OBIE_PAN.getNamespacedValue()).isEqualTo("UK.OBIE.PAN");

        assertThat(AccountSchemeName.UK_OBIE_PAN.isEqual("UK.OBIE.PAN")).isTrue();
        assertThat(AccountSchemeName.UK_OBIE_PAN.isEqual("PAN")).isTrue();

        assertThat(AccountSchemeName.UK_OBIE_PAN.isEqual("SortCodeAccountNumber")).isFalse();
        assertThat(AccountSchemeName.UK_OBIE_PAN.isEqual("")).isFalse();
        assertThat(AccountSchemeName.UK_OBIE_PAN.isEqual(null)).isFalse();

        assertThat(AccountSchemeName.isAccountOfType(new OBCashAccount3(), AccountSchemeName.UK_OBIE_PAN)).isFalse();
        assertThat(AccountSchemeName.isAccountOfType(new OBCashAccount3().schemeName("UK.OBIE.PAN"), AccountSchemeName.UK_OBIE_PAN)).isTrue();
        assertThat(AccountSchemeName.isAccountOfType(new OBCashAccount3().schemeName("PAN"), AccountSchemeName.UK_OBIE_PAN)).isTrue();
    }


    @Test
    public void SortCodeAccountNumber() {
        assertThat(AccountSchemeName.UK_OBIE_SORTCODEACCOUNTNUMBER.getNamespacedValue()).isEqualTo("UK.OBIE.SortCodeAccountNumber");

        assertThat(AccountSchemeName.UK_OBIE_SORTCODEACCOUNTNUMBER.isEqual("UK.OBIE.SortCodeAccountNumber")).isTrue();
        assertThat(AccountSchemeName.UK_OBIE_SORTCODEACCOUNTNUMBER.isEqual("SortCodeAccountNumber")).isTrue();

        assertThat(AccountSchemeName.UK_OBIE_SORTCODEACCOUNTNUMBER.isEqual("PAN")).isFalse();
        assertThat(AccountSchemeName.UK_OBIE_SORTCODEACCOUNTNUMBER.isEqual("")).isFalse();
        assertThat(AccountSchemeName.UK_OBIE_SORTCODEACCOUNTNUMBER.isEqual(null)).isFalse();

        assertThat(AccountSchemeName.isAccountOfType(new OBCashAccount3().schemeName(""), AccountSchemeName.UK_OBIE_SORTCODEACCOUNTNUMBER)).isFalse();
        assertThat(AccountSchemeName.isAccountOfType(new OBCashAccount3().schemeName("UK.OBIE.SortCodeAccountNumber"), AccountSchemeName.UK_OBIE_SORTCODEACCOUNTNUMBER)).isTrue();
        assertThat(AccountSchemeName.isAccountOfType(new OBCashAccount3().schemeName("SortCodeAccountNumber"), AccountSchemeName.UK_OBIE_SORTCODEACCOUNTNUMBER)).isTrue();
    }

    @Test
    public void PayM() {
        assertThat(AccountSchemeName.UK_OBIE_PAYM.getNamespacedValue()).isEqualTo("UK.OBIE.Paym");

        assertThat(AccountSchemeName.UK_OBIE_PAYM.isEqual("UK.OBIE.Paym")).isTrue();
        assertThat(AccountSchemeName.UK_OBIE_PAYM.isEqual("UK.OBIE.PAYM")).isTrue();
        assertThat(AccountSchemeName.UK_OBIE_PAYM.isEqual("Paym")).isTrue();

        assertThat(AccountSchemeName.UK_OBIE_PAYM.isEqual("PAN")).isFalse();
        assertThat(AccountSchemeName.UK_OBIE_PAYM.isEqual("")).isFalse();
        assertThat(AccountSchemeName.UK_OBIE_PAYM.isEqual(null)).isFalse();

        assertThat(AccountSchemeName.isAccountOfType(null, AccountSchemeName.UK_OBIE_PAYM)).isFalse();
        assertThat(AccountSchemeName.isAccountOfType(new OBCashAccount3().schemeName("UK.OBIE.Paym"), AccountSchemeName.UK_OBIE_PAYM)).isTrue();
        assertThat(AccountSchemeName.isAccountOfType(new OBCashAccount3().schemeName("Paym"), AccountSchemeName.UK_OBIE_PAYM)).isTrue();
    }
}