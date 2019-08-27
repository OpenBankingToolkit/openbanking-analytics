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
import uk.org.openbanking.datamodel.account.OBCashAccount1;
import uk.org.openbanking.datamodel.account.OBCashAccount3;
import uk.org.openbanking.datamodel.account.OBCashAccount5;
import uk.org.openbanking.datamodel.account.OBCashAccount6;
import uk.org.openbanking.datamodel.payment.OBCashAccountCreditor3;
import uk.org.openbanking.datamodel.payment.OBCashAccountDebtor4;

public class OBCashAccountConverterTest {

    @Test
    public void checkModelMapperMapsAllFieldByDefault() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(OBCashAccount6.class, OBCashAccount3.class);
        modelMapper.validate();

        modelMapper.createTypeMap(OBCashAccount3.class, OBCashAccount6.class);
        modelMapper.validate();


        modelMapper.createTypeMap(OBCashAccount3.class, OBCashAccountCreditor3.class);
        modelMapper.validate();


        modelMapper.createTypeMap(OBCashAccountCreditor3.class, OBCashAccount3.class);
        modelMapper.validate();


        modelMapper.createTypeMap(OBCashAccountDebtor4.class, OBCashAccount3.class);
        modelMapper.validate();


        modelMapper.createTypeMap(OBCashAccount3.class, OBCashAccountDebtor4.class);
        modelMapper.validate();

        modelMapper.createTypeMap(OBCashAccount5.class, OBCashAccount3.class);
        modelMapper.validate();

        modelMapper.createTypeMap(OBCashAccount5.class, OBCashAccount1.class);
        modelMapper.validate();
    }
}