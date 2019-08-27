/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.services.openbanking;

import com.forgerock.openbanking.analytics.model.openbanking.v1_1.account.FRBalance1;
import com.forgerock.openbanking.analytics.services.store.balance.BalanceStoreService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class FundsAvailabilityServiceTest {

    private static final String ACCOUNT_NO = "11111111";

    @Mock
    private BalanceStoreService balanceStoreService;

    @InjectMocks
    private FundsAvailabilityService fundsAvailabilityService;

      @Test
    public void fundsAvailable() {
        // Given
        givenABalanceOf("201.0");

        // Then
        assertThat(fundsAvailabilityService.isFundsAvailable(ACCOUNT_NO, "200.0"))
                .isTrue();
    }

    @Test
    public void fundsNotAvailable() {
        // Given
        givenABalanceOf("199.9");

        // Then
        assertThat(fundsAvailabilityService.isFundsAvailable(ACCOUNT_NO, "200.0"))
                .isFalse();
    }

    @Test
    public void exactFundsAvailable() {
        // Given
        givenABalanceOf("200.0");

        // Then
        assertThat(fundsAvailabilityService.isFundsAvailable(ACCOUNT_NO, "200.0"))
                .isTrue();
    }

    @Test
    public void zeroFundsRequested() {
        // Given
        givenABalanceOf("200.0");

        // Then
        assertThat(fundsAvailabilityService.isFundsAvailable(ACCOUNT_NO, "0.0"))
                .isTrue();
    }

    @Test
    public void accountNotFoundThrowsIllegalStateException() {
        // Given
        given(balanceStoreService.getBalance(any(), any()))
                .willReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> fundsAvailabilityService.isFundsAvailable(ACCOUNT_NO, "100.0"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void noAccountNumberParamThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> fundsAvailabilityService.isFundsAvailable(null, "100.0"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void noAmountParamThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> fundsAvailabilityService.isFundsAvailable(ACCOUNT_NO, ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private void givenABalanceOf(String balanceAmount) {
        final FRBalance1 balance = mock(FRBalance1.class);
        given(balance.getAmount()).willReturn(new BigDecimal(balanceAmount));
        given(balanceStoreService.getBalance(any(), any()))
                .willReturn(Optional.of(balance));
    }
}