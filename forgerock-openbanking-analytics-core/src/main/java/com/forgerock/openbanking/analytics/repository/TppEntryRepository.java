/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.repository;

import com.forgerock.openbanking.analytics.model.entries.TppEntry;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface TppEntryRepository extends MongoRepository<TppEntry, String> {

    Optional<TppEntry> findByOidcClientId(String oidcClientId);

    List<TppEntry> findByCreatedBetween(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate);

    Page<TppEntry> findByCreatedBetweenAndNameRegex(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate,
            String regex,
            Pageable pageable
    );

    Stream<TppEntry> findByCreatedBetweenAndNameRegex(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDate,
            String regex
    );
}
