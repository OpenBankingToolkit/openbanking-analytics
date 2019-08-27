/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.services.openbanking.converter;

import org.junit.Test;
import org.modelmapper.ModelMapper;
import uk.org.openbanking.datamodel.payment.*;

public class OBActiveOrHistoricCurrencyAndAmountConverterTest {
    @Test
    public void checkModelMapperMapsAllFieldByDefault() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(uk.org.openbanking.datamodel.account.OBActiveOrHistoricCurrencyAndAmount.class, OBActiveOrHistoricCurrencyAndAmount.class);
        modelMapper.validate();

        modelMapper.createTypeMap(OBActiveOrHistoricCurrencyAndAmount.class, uk.org.openbanking.datamodel.account.OBActiveOrHistoricCurrencyAndAmount.class);
        modelMapper.validate();

        modelMapper.createTypeMap(OBDomesticStandingOrder3FinalPaymentAmount.class, OBActiveOrHistoricCurrencyAndAmount.class);
        modelMapper.validate();

        modelMapper.createTypeMap(OBDomesticStandingOrder3FirstPaymentAmount.class, OBActiveOrHistoricCurrencyAndAmount.class);
        modelMapper.validate();

        modelMapper.createTypeMap(OBDomesticStandingOrder3RecurringPaymentAmount.class, OBActiveOrHistoricCurrencyAndAmount.class);
        modelMapper.validate();

        modelMapper.createTypeMap(OBDomesticStandingOrder3FirstPaymentAmount.class, uk.org.openbanking.datamodel.account.OBActiveOrHistoricCurrencyAndAmount.class);
        modelMapper.validate();

        modelMapper.createTypeMap(OBDomesticStandingOrder3FinalPaymentAmount.class, uk.org.openbanking.datamodel.account.OBActiveOrHistoricCurrencyAndAmount.class);
        modelMapper.validate();

        modelMapper.createTypeMap(OBDomesticStandingOrder3RecurringPaymentAmount.class, uk.org.openbanking.datamodel.account.OBActiveOrHistoricCurrencyAndAmount.class);
        modelMapper.validate();

        modelMapper.createTypeMap(OBDomestic2InstructedAmount.class, OBActiveOrHistoricCurrencyAndAmount.class);
        modelMapper.validate();
    }
}