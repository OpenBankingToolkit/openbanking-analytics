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
