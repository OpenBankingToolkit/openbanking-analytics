/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.openbanking;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IntentTypeTest {

    @Test
    public void generateIntentId_checkLength() {
        assertThat(IntentType.ACCOUNT_REQUEST.generateIntentId().length()).isLessThan(41);
        assertThat(IntentType.PAYMENT_SINGLE_REQUEST.generateIntentId().length()).isLessThan(41);
        assertThat(IntentType.ACCOUNT_ACCESS_CONSENT.generateIntentId().length()).isLessThan(41);

        assertThat(IntentType.PAYMENT_DOMESTIC_CONSENT.generateIntentId().length()).isLessThan(41);
        assertThat(IntentType.PAYMENT_DOMESTIC_SCHEDULED_CONSENT.generateIntentId().length()).isLessThan(41);
        assertThat(IntentType.PAYMENT_DOMESTIC_STANDING_ORDERS_CONSENT.generateIntentId().length()).isLessThan(41);

        assertThat(IntentType.PAYMENT_INTERNATIONAL_CONSENT.generateIntentId().length()).isLessThan(41);
        assertThat(IntentType.PAYMENT_INTERNATIONAL_SCHEDULED_CONSENT.generateIntentId().length()).isLessThan(41);
        assertThat(IntentType.PAYMENT_INTERNATIONAL_STANDING_ORDERS_CONSENT.generateIntentId().length()).isLessThan(41);

        assertThat(IntentType.PAYMENT_FILE_CONSENT.generateIntentId().length()).isLessThan(41);
        assertThat(IntentType.FUNDS_CONFIRMATION_CONSENT.generateIntentId().length()).isLessThan(41);
    }
}