/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.repository;

import com.forgerock.openbanking.model.Tpp;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TppRepository extends MongoRepository<Tpp, String> {
}
