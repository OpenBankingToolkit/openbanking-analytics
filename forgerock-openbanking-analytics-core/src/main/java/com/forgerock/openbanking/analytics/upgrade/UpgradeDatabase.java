/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.upgrade;

import com.forgerock.openbanking.analytics.repository.EntitiesVersionRepository;
import com.forgerock.openbanking.upgrade.UpgradeApplication;
import com.forgerock.openbanking.upgrade.model.version.EntitiesVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UpgradeDatabase extends UpgradeApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpgradeDatabase.class);

    @Autowired
    private EntitiesVersionRepository entitiesVersionRepository;

    @Bean
    public CommandLineRunner upgrade() {
        return this;
    }

    @Override
    public EntitiesVersion getPreviousVersion() {
        List<EntitiesVersion> all = entitiesVersionRepository.findAll();
        EntitiesVersion entitiesVersion;
        if (all.isEmpty()) {
            entitiesVersion = new EntitiesVersion();
            entitiesVersion.setStatus(EntitiesVersion.UpgradeStatus.UPGRADED);
            entitiesVersion.setVersion("0");
        } else if (all.size() > 1) {
            LOGGER.error("More than one version found: " + all);
            throw new RuntimeException("More than one version found: " + all);
        } else {
            entitiesVersion = all.get(0);
        }
        return entitiesVersion;
    }

    @Override
    public EntitiesVersion saveEntitiesVersion(EntitiesVersion entitiesVersion) {
        return entitiesVersionRepository.save(entitiesVersion);
    }
}
