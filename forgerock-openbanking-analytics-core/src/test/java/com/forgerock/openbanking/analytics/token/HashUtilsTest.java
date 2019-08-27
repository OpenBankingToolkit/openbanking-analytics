/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.token;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HashUtilsTest {

    @Test
    public void computeHash() {
        // Given
        String strToEncode = "test12345test-abcde";

        // When
        String encoded = HashUtils.computeHash(strToEncode);

        // Then
        assertThat(encoded).isEqualTo("rbaBpk2vZ0vxOGMEVM7ppA==");

    }

    @Test
    public void computeSHA256FullHash() {
        // Given
        String strToEncode = "test12345test-abcde";

        // When
        String encoded = HashUtils.computeSHA256FullHash(strToEncode);

        // Then (based on https://cryptii.com/pipes/hash-function)
        assertThat(encoded).isEqualTo("rbaBpk2vZ0vxOGMEVM7ppJAFwy+MSnaKllpcLdfCreA=");

    }
}