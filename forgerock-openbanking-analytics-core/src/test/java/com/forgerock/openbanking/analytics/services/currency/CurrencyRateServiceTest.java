/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.services.currency;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.org.openbanking.datamodel.payment.OBExchangeRate1;
import uk.org.openbanking.datamodel.payment.OBExchangeRate2;
import uk.org.openbanking.datamodel.payment.OBExchangeRateType2Code;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CurrencyRateServiceTest {
    private static final DateTime NOW = DateTime.now();

    @Test
    public void getCalculatedExchangeRate_actual() {
        // Given
        OBExchangeRate1 actualExchangeRate = new OBExchangeRate1()
                .rateType(OBExchangeRateType2Code.ACTUAL)
                .unitCurrency("GBP");

        // When
        OBExchangeRate2 calculatedExchangeRate = CurrencyRateService.getCalculatedExchangeRate(actualExchangeRate, NOW);

        // Then
        assertThat(calculatedExchangeRate.getExchangeRate()).isEqualTo(new BigDecimal("1.5"));
        assertThat(calculatedExchangeRate.getUnitCurrency()).isEqualTo("GBP");
        assertThat(calculatedExchangeRate.getContractIdentification()).isNull();
        assertThat(calculatedExchangeRate.getExpirationDateTime()).isEqualTo(NOW.plusMinutes(60));
        assertThat(calculatedExchangeRate.getRateType()).isEqualTo(OBExchangeRateType2Code.ACTUAL);
    }

    @Test
    public void getCalculatedExchangeRate_actualWithoutDate() {
        // Given
        OBExchangeRate1 actualExchangeRate = new OBExchangeRate1()
                .rateType(OBExchangeRateType2Code.ACTUAL)
                .unitCurrency("GBP");

        // When
        OBExchangeRate2 calculatedExchangeRate = CurrencyRateService.getCalculatedExchangeRate(actualExchangeRate, null);

        // Then
        assertThat(calculatedExchangeRate.getExchangeRate()).isEqualTo(new BigDecimal("1.5"));
        assertThat(calculatedExchangeRate.getUnitCurrency()).isEqualTo("GBP");
        assertThat(calculatedExchangeRate.getContractIdentification()).isNull();
        assertThat(calculatedExchangeRate.getExpirationDateTime()).isNotNull();
        assertThat(calculatedExchangeRate.getRateType()).isEqualTo(OBExchangeRateType2Code.ACTUAL);
    }

    @Test
    public void getCalculatedExchangeRate_indicative() {
        // Given
        OBExchangeRate1 indicativeRate = new OBExchangeRate1()
                .rateType(OBExchangeRateType2Code.INDICATIVE)
                .unitCurrency("GBP");

        // When
        OBExchangeRate2 calculatedExchangeRate = CurrencyRateService.getCalculatedExchangeRate(indicativeRate, NOW);

        // Then
        assertThat(calculatedExchangeRate.getExchangeRate()).isEqualTo(new BigDecimal("1.5"));
        assertThat(calculatedExchangeRate.getUnitCurrency()).isEqualTo("GBP");
        assertThat(calculatedExchangeRate.getContractIdentification()).isNull();
        assertThat(calculatedExchangeRate.getExpirationDateTime()).isNull();
        assertThat(calculatedExchangeRate.getRateType()).isEqualTo(OBExchangeRateType2Code.INDICATIVE);
    }

    @Test
    public void getCalculatedExchangeRate_agreed() {
        // Given
        OBExchangeRate1 agreedRate = new OBExchangeRate1()
                .rateType(OBExchangeRateType2Code.AGREED)
                .unitCurrency("GBP")
                .exchangeRate(new BigDecimal("2.0"))
                .contractIdentification("test123");

        // When
        OBExchangeRate2 calculatedExchangeRate = CurrencyRateService.getCalculatedExchangeRate(agreedRate, NOW);

        // Then
        assertThat(calculatedExchangeRate.getExchangeRate()).isEqualTo(new BigDecimal("2.0"));
        assertThat(calculatedExchangeRate.getUnitCurrency()).isEqualTo("GBP");
        assertThat(calculatedExchangeRate.getContractIdentification()).isEqualTo("test123");
        assertThat(calculatedExchangeRate.getExpirationDateTime()).isNull();
        assertThat(calculatedExchangeRate.getRateType()).isEqualTo(OBExchangeRateType2Code.AGREED);
    }

    @Test
    public void getCalculatedExchangeRate_agreed_missingRate_exception() {
        // Given
        OBExchangeRate1 agreedRate = new OBExchangeRate1()
                .rateType(OBExchangeRateType2Code.AGREED)
                .unitCurrency("GBP")
                // Missing rate
                .contractIdentification("test123");

        // Then
        assertThatThrownBy(() -> CurrencyRateService.getCalculatedExchangeRate(agreedRate, NOW))
                .hasMessage("Missing the exchange rate value which is mandatory for exchange rate type: 'Agreed'")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getCalculatedExchangeRate_noRateType() {
        // Given
        OBExchangeRate1 agreedRate = new OBExchangeRate1()
                .rateType(null)
                .unitCurrency("GBP");

        // Then
        assertThatThrownBy(() -> CurrencyRateService.getCalculatedExchangeRate(agreedRate, NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getCalculatedExchangeRate_nullRate() {
       assertThat(CurrencyRateService.getCalculatedExchangeRate(null, NOW)).isNull();
    }
}