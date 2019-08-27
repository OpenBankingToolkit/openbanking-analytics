/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.services.openbanking.converter;

import org.junit.Test;
import uk.org.openbanking.datamodel.account.OBBranchAndFinancialInstitutionIdentification2;
import uk.org.openbanking.datamodel.account.OBBranchAndFinancialInstitutionIdentification3;
import uk.org.openbanking.datamodel.account.OBBranchAndFinancialInstitutionIdentification6;
import uk.org.openbanking.datamodel.account.OBPostalAddress6;

import static org.assertj.core.api.Assertions.assertThat;

public class OBBranchAndFinancialInstitutionIdentificationConverterTest {

    @Test
    public void toOBBranchAndFinancialInstitutionIdentification6() {
        OBBranchAndFinancialInstitutionIdentification3 source = new OBBranchAndFinancialInstitutionIdentification3()
                .identification("id1")
                .schemeName("sch1")
                .name("n1")
                .postalAddress(obPostalAddress6());

        OBBranchAndFinancialInstitutionIdentification6 result =
                OBBranchAndFinancialInstitutionIdentificationConverter.toOBBranchAndFinancialInstitutionIdentification6(source);

        assertThat(result.getIdentification()).isEqualTo(source.getIdentification());
        assertThat(result.getName()).isEqualTo(source.getName());
        assertThat(result.getSchemeName()).isEqualTo(source.getSchemeName());
        assertThat(result.getPostalAddress()).isEqualTo(source.getPostalAddress());
    }

    @Test
    public void toOBBranchAndFinancialInstitutionIdentification2() {
        OBBranchAndFinancialInstitutionIdentification6 source = new OBBranchAndFinancialInstitutionIdentification6()
                .identification("id1")
                .schemeName("BICFI")
                .name("n1")
                .postalAddress(obPostalAddress6());

        OBBranchAndFinancialInstitutionIdentification2 target =
                OBBranchAndFinancialInstitutionIdentificationConverter.toOBBranchAndFinancialInstitutionIdentification2(source);

        assertThat(target.getIdentification()).isEqualTo(source.getIdentification());
        assertThat(target.getSchemeName().name()).isEqualTo(source.getSchemeName());
    }

    @Test
    public void toOBBranchAndFinancialInstitutionIdentification3() {
        OBBranchAndFinancialInstitutionIdentification6 source = new OBBranchAndFinancialInstitutionIdentification6()
                .identification("id1")
                .schemeName("sch1")
                .name("n1")
                .postalAddress(obPostalAddress6());

        OBBranchAndFinancialInstitutionIdentification3 target =
                OBBranchAndFinancialInstitutionIdentificationConverter.toOBBranchAndFinancialInstitutionIdentification3(source);

        assertThat(target.getIdentification()).isEqualTo(source.getIdentification());
        assertThat(target.getSchemeName()).isEqualTo(source.getSchemeName());
    }

    private OBPostalAddress6 obPostalAddress6() {
        return new OBPostalAddress6()
                .buildingNumber("1")
                .streetName("Queens Sq")
                .townName("Bristol")
                .subDepartment("Glouc")
                .country("UK")
                .countrySubDivision("Eng")
                .postCode("BS1");
    }
}