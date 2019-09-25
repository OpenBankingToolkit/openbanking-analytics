/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.repository;

import com.forgerock.openbanking.analytics.model.entries.DirectoryCounterEntry;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

public interface DirectoryCounterEntryRepository extends MongoRepository<DirectoryCounterEntry, String> {

    List<DirectoryCounterEntry> findByDayBetween(
            @Param("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @Param("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate);
}