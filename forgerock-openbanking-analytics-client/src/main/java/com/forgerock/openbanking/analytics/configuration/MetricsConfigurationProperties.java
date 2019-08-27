/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@ConfigurationProperties(prefix = "metrics")
public class MetricsConfigurationProperties {
    private List<Endpoint> endpoints;

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    public static class Endpoint {
        public String regex;
        private Pattern pattern;

        public Pattern getPattern() {
            if (pattern == null) {
                pattern = Pattern.compile(regex);
            }
            return pattern;
        }

        public void setRegex(String regex) {
            this.regex = regex;
        }

        public Matcher getMatcher(String stringToMatch) {
            return getPattern().matcher(stringToMatch);
        }


    }
}
