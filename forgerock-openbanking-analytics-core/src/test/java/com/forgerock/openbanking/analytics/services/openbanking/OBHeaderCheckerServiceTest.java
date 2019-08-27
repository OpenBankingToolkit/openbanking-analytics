/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.services.openbanking;

import com.forgerock.openbanking.analytics.configuration.applications.RSConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OBHeaderCheckerServiceTest {

    private RSConfiguration rsConfig;
    private OBHeaderCheckerService obHeaderCheckerService;

    @Before
    public void setUp() {
        rsConfig = new RSConfiguration("", "" , "");
        obHeaderCheckerService = new OBHeaderCheckerService(rsConfig);
    }

    @Test
    public void verifyHeader_match() {
        // Given
        String financialId = UUID.randomUUID().toString();
        rsConfig = new RSConfiguration("", financialId , "");
        obHeaderCheckerService = new OBHeaderCheckerService(rsConfig);

        // When
        boolean valid = obHeaderCheckerService.verifyFinancialIdHeader(financialId);

        // Then
        assertThat(valid).isTrue();
    }

    @Test
    public void verifyHeader_noMatch() {
        // Given
        String financialId = UUID.randomUUID().toString();
        rsConfig = new RSConfiguration("", financialId , "");
        obHeaderCheckerService = new OBHeaderCheckerService(rsConfig);

        // When
        boolean valid = obHeaderCheckerService.verifyFinancialIdHeader("different");

        // Then
        assertThat(valid).isFalse();
    }
}