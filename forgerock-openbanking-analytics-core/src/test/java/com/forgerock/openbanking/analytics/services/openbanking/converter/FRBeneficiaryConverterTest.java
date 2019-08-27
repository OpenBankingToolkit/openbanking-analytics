/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.services.openbanking.converter;

import com.forgerock.openbanking.analytics.model.openbanking.v1_1.account.FRBeneficiary1;
import com.forgerock.openbanking.analytics.model.openbanking.v2_0.account.FRBeneficiary2;
import com.forgerock.openbanking.analytics.model.openbanking.v3_1_1.account.FRBeneficiary3;
import org.joda.time.DateTime;
import org.junit.Test;
import uk.org.openbanking.datamodel.account.*;

import static org.assertj.core.api.Assertions.assertThat;

public class FRBeneficiaryConverterTest {

    @Test
    public void toBeneficiary3() {
        FRBeneficiary2 frBeneficiary2 = FRBeneficiary2.builder()
                .accountId("acc1")
                .created(DateTime.now().toDate())
                .updated(DateTime.now().toDate())
                .id("111")
                .beneficiary(new OBBeneficiary2()
                        .beneficiaryId("b1")
                        .creditorAccount(new OBCashAccount3()
                                .identification("id123")
                                .name("user"))
                        .creditorAgent(new OBBranchAndFinancialInstitutionIdentification3()
                                .identification("agent1")
                                .postalAddress(new OBPostalAddress6().postCode("BS1")))
                        .reference("ref")
                )
                .build();

        FRBeneficiary3 frBeneficiary3 = FRBeneficiaryConverter.toBeneficiary3(frBeneficiary2);

        assertThat(frBeneficiary3.getAccountId()).isEqualTo(frBeneficiary2.getAccountId());
        assertThat(frBeneficiary3.getCreated()).isEqualTo(frBeneficiary2.getCreated());
        assertThat(frBeneficiary3.getUpdated()).isEqualTo(frBeneficiary2.getUpdated());
        assertThat(frBeneficiary3.getId()).isEqualTo(frBeneficiary2.getId());
        assertThat(frBeneficiary3.getBeneficiary().getBeneficiaryId()).isEqualTo(frBeneficiary2.getBeneficiary().getBeneficiaryId());
        assertThat(frBeneficiary3.getBeneficiary().getReference()).isEqualTo(frBeneficiary2.getBeneficiary().getReference());
        assertThat(frBeneficiary3.getBeneficiary().getCreditorAccount().getIdentification()).isEqualTo(frBeneficiary2.getBeneficiary().getCreditorAccount().getIdentification());
        assertThat(frBeneficiary3.getBeneficiary().getCreditorAccount().getName()).isEqualTo(frBeneficiary2.getBeneficiary().getCreditorAccount().getName());
        assertThat(frBeneficiary3.getBeneficiary().getCreditorAgent().getPostalAddress().getPostCode()).isEqualTo(frBeneficiary2.getBeneficiary().getCreditorAgent().getPostalAddress().getPostCode());
    }

    @Test
    public void toBeneficiary1() {
        FRBeneficiary3 frBeneficiary3 = FRBeneficiary3.builder()
                .accountId("acc1")
                .created(DateTime.now().toDate())
                .updated(DateTime.now().toDate())
                .id("111")
                .beneficiary(new OBBeneficiary3()
                        .beneficiaryId("b1")
                        .creditorAccount(new OBCashAccount5()
                                .identification("id123")
                                .name("user"))
                        .creditorAgent(new OBBranchAndFinancialInstitutionIdentification6()
                                .identification("agent1")
                                .postalAddress(new OBPostalAddress6().postCode("BS1")))
                        .reference("ref")
                )
                .build();

        FRBeneficiary1 frBeneficiary1 = FRBeneficiaryConverter.toBeneficiary1(frBeneficiary3);

        assertThat(frBeneficiary1.getAccountId()).isEqualTo(frBeneficiary3.getAccountId());
        assertThat(frBeneficiary1.getCreated()).isEqualTo(frBeneficiary3.getCreated());
        assertThat(frBeneficiary1.getUpdated()).isEqualTo(frBeneficiary3.getUpdated());
        assertThat(frBeneficiary1.getId()).isEqualTo(frBeneficiary3.getId());
        assertThat(frBeneficiary1.getBeneficiary().getBeneficiaryId()).isEqualTo(frBeneficiary3.getBeneficiary().getBeneficiaryId());
        assertThat(frBeneficiary1.getBeneficiary().getReference()).isEqualTo(frBeneficiary3.getBeneficiary().getReference());
        assertThat(frBeneficiary1.getBeneficiary().getCreditorAccount().getIdentification()).isEqualTo(frBeneficiary3.getBeneficiary().getCreditorAccount().getIdentification());
        assertThat(frBeneficiary1.getBeneficiary().getCreditorAccount().getName()).isEqualTo(frBeneficiary3.getBeneficiary().getCreditorAccount().getName());
    }
}