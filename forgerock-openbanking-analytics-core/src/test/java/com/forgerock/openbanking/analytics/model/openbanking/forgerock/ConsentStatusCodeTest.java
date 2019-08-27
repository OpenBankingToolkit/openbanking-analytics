/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.openbanking.forgerock;

import org.junit.Test;
import uk.org.openbanking.datamodel.payment.OBExternalConsentStatus1Code;

import static org.assertj.core.api.Assertions.assertThat;

public class ConsentStatusCodeTest {

    @Test
    public void expectCorrectMapping_toOBExternalConsentStatus1Code() {

        assertThat(ConsentStatusCode.ACCEPTEDSETTLEMENTCOMPLETED.toOBExternalConsentStatus1Code()).isEqualByComparingTo(OBExternalConsentStatus1Code.CONSUMED);
        assertThat(ConsentStatusCode.ACCEPTEDSETTLEMENTINPROCESS.toOBExternalConsentStatus1Code()).isEqualByComparingTo(OBExternalConsentStatus1Code.CONSUMED);
        assertThat(ConsentStatusCode.CONSUMED.toOBExternalConsentStatus1Code()).isEqualByComparingTo(OBExternalConsentStatus1Code.CONSUMED);

        assertThat(ConsentStatusCode.ACCEPTEDCUSTOMERPROFILE.toOBExternalConsentStatus1Code()).isEqualByComparingTo(OBExternalConsentStatus1Code.AUTHORISED);
        assertThat(ConsentStatusCode.ACCEPTEDTECHNICALVALIDATION.toOBExternalConsentStatus1Code()).isEqualByComparingTo(OBExternalConsentStatus1Code.AUTHORISED);
        assertThat(ConsentStatusCode.AUTHORISED.toOBExternalConsentStatus1Code()).isEqualByComparingTo(OBExternalConsentStatus1Code.AUTHORISED);

        assertThat(ConsentStatusCode.AWAITINGAUTHORISATION.toOBExternalConsentStatus1Code()).isEqualByComparingTo(OBExternalConsentStatus1Code.AWAITINGAUTHORISATION);

        assertThat(ConsentStatusCode.REJECTED.toOBExternalConsentStatus1Code()).isEqualByComparingTo(OBExternalConsentStatus1Code.REJECTED);

    }

}