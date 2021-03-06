/**
 * Copyright 2019 ForgeRock AS.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.forgerock.openbanking.analytics.upgrade;

import com.forgerock.openbanking.analytics.model.entries.*;
import com.forgerock.openbanking.analytics.models.TokenUsageEntry;
import com.forgerock.openbanking.upgrade.exceptions.UpgradeException;
import com.forgerock.openbanking.upgrade.model.UpgradeMeta;
import com.forgerock.openbanking.upgrade.model.UpgradeStep;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import static com.forgerock.openbanking.analytics.upgrade.UpgradeStep_4_1_4.VERSION;

@Service
@UpgradeMeta(version = VERSION)
@Slf4j
public class UpgradeStep_4_1_4 implements UpgradeStep {

    // The release version of openbanking-reference-implementation
    // Need to comply with => (VERSION >= versionFrom && VERSION <= versionTo)
    protected static final String VERSION = "4.1.4";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public boolean upgrade() throws UpgradeException {
        StopWatch elapsedTime = new StopWatch();
        elapsedTime.start();
        log.info("-----------------------------------------------------------------------");
        log.debug("Start {} to upgrading to version {}", this.getClass().getName(), VERSION);
        try {
            upgradeEntries(TppEntry.class);
            upgradeEntries(ConsentStatusEntry.class);
            upgradeEntries(DirectoryCounterEntry.class);
            upgradeEntries(EndpointUsageAggregate.class);
            upgradeEntries(JwtsGenerationEntry.class);
            upgradeEntries(JwtsValidationEntry.class);
            upgradeEntries(PsuCounterEntry.class);
            upgradeEntries(SessionCounterEntry.class);
            upgradeEntries(TokenUsageEntry.class);
            elapsedTime.stop();
            log.info("Upgraded executed in {} seconds.", elapsedTime.getTotalTimeSeconds());
            log.info("-----------------------------------------------------------------------");
        } catch (Exception e) {
            throw new UpgradeException("Could not upgrade to " + VERSION, e);
        } finally {
            SecurityContextHolder.clearContext();
        }
        return true;
    }

    private <T> void upgradeEntries(Class<T> clazz) {
        StopWatch elapsedTime = start(clazz);

        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, clazz);

        Query query = new Query().addCriteria(new Criteria("_class").regex(".metrics."));

        Update update = new Update()
                .set("_class", clazz.getName())
                .set("upgradedVersion", VERSION)
                .set("upgradedDate", DateTime.now());

        bulkOperations.updateOne(query, update);

        int i = bulkOperations.updateMulti(query, update).execute().getModifiedCount();

        stop(clazz,i,elapsedTime);
    }

    private <T> StopWatch start(Class<T> clazz){
        StopWatch elapsedTime = new StopWatch();
        elapsedTime.start();
        log.info("-----------------------------------------------------------------------");
        log.info("Upgrading {}", clazz.getName());
        return elapsedTime;
    }

    private <T> void stop(Class<T> clazz, int entriesModified, StopWatch elapsedTime){
        elapsedTime.stop();
        log.info("Upgraded [{}] {} objects in {} seconds.",entriesModified, clazz.getName(), elapsedTime.getTotalTimeSeconds());
        log.info("-----------------------------------------------------------------------");
    }

}
