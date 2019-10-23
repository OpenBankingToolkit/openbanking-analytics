/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.services;

import com.forgerock.openbanking.auth.services.UserProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Primary
@Service
public class AnalyticsUserProvider implements UserProvider {

    @Override
    public Object getUser(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        return new AnalyticsUser(principal.getUsername(), principal.getAuthorities().stream().map(Objects::toString).collect(Collectors.toList()));
    }

    @Data
    @AllArgsConstructor
    private static class AnalyticsUser {
        private String username;
        private List<String> authorities;
    }
}
