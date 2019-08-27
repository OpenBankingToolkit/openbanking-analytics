/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.configuration;

import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MetricsConfiguration implements ApplicationConfiguration {

    @Value("${metrics.endpoints.root}")
    public String rootEndpoint;

    @Value("${metrics.endpoints.endpoint-usage.add-entries}")
    public String endpointUsageAddEntries;

    @Value("${metrics.endpoints.jwts-generation.add-entries}")
    public String jwtsGenerationAddEntries;

    @Value("${metrics.endpoints.jwts-validation.add-entries}")
    public String jwtsValidationAddEntries;

    @Override
    public String getIssuerID() {
        return "metrics";
    }

    @Override
    public JWKSet getJwkSet() {
        return null;
    }
}
