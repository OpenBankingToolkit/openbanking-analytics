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

import com.forgerock.openbanking.analytics.model.IntentType;
import com.forgerock.openbanking.analytics.model.entries.ConsentStatusEntry;
import com.forgerock.openbanking.api.annotations.OBGroupName;
import com.forgerock.openbanking.analytics.models.ConsentActivities;
import com.forgerock.openbanking.analytics.models.ConsentTypeCounter;
import org.joda.time.DateTime;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

@Repository
public class ConsentStatusEntryRepositoryImpl implements ConsentStatusEntryRepositoryCustom {

    private MongoTemplate mongoTemplate;

    public ConsentStatusEntryRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<ConsentActivities> countActiveConsentStatusBetweenDate(DateTime from, DateTime to, IntentType consentType) {
        Aggregation aggregation = newAggregation(
                Aggregation.match(
                        Criteria.where("consentType").is(consentType)),
                Aggregation.match(
                        Criteria.where("date").gt(from)),
                Aggregation.match(
                        Criteria.where("date").lt(to)),
                Aggregation.sort(Sort.Direction.DESC, "date"),
                Aggregation.group("consentId")
                        .first("consentStatus").as("consentStatus"),
                Aggregation.group("consentStatus").count().as("total"),
                project("total").and("consentStatus").previousOperation()
        );
        //Convert the aggregation result into a List
        AggregationResults<ConsentActivities> groupResults
                = mongoTemplate.aggregate(aggregation, ConsentStatusEntry.class, ConsentActivities.class);
        return groupResults.getMappedResults();
    }

    @Override
    public List<ConsentTypeCounter> countConsentsCreatedBetweenDate(OBGroupName obGroupName, DateTime from, DateTime to) {

        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        if (obGroupName != null) {
            aggregationOperations.add(Aggregation.match(Criteria.where("consentType").in(IntentType.byOBGroupeName(obGroupName))));
        }
        aggregationOperations.add(Aggregation.sort(Sort.Direction.ASC, "date"));
        aggregationOperations.add(Aggregation.group("consentId")
                                .first("date").as("created")
                                .first("consentType").as("consentType"));
        aggregationOperations.add(Aggregation.match(
                                Criteria.where("created").gt(from)));
        aggregationOperations.add(Aggregation.match(
                                Criteria.where("created").lt(to)));
        aggregationOperations.add(Aggregation.group("consentType").count().as("total"));
        aggregationOperations.add(project("total").and("consentType").previousOperation());

        Aggregation aggregation = newAggregation(aggregationOperations);

        //Convert the aggregation result into a List
        AggregationResults<ConsentTypeCounter> groupResults
                = mongoTemplate.aggregate(aggregation, ConsentStatusEntry.class, ConsentTypeCounter.class);
        return groupResults.getMappedResults();
    }

}
