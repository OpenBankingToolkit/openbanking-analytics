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

import com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate;
import com.forgerock.openbanking.analytics.model.kpi.EndpointTableFilter;
import com.forgerock.openbanking.analytics.model.kpi.EndpointTableRequest;
import com.forgerock.openbanking.analytics.model.kpi.EndpointUsageAggregateRepositoryCustom;
import com.forgerock.openbanking.analytics.model.kpi.EndpointUsageKpiRequest;
import com.forgerock.openbanking.analytics.models.EndpointUsagePage;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.util.CloseableIterator;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

@Repository
@Slf4j
public class EndpointUsageAggregateRepositoryImpl implements EndpointUsageAggregateRepositoryCustom {

    private MongoTemplate mongoTemplate;

    public EndpointUsageAggregateRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<String> getSetDefinition(EndpointUsageKpiRequest request, DateTime from, DateTime to, String field) {

        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        aggregationOperations.add( Aggregation.match(
                Criteria.where("date").gt(from.minusMinutes(1))));
        aggregationOperations.add( Aggregation.match(
                Criteria.where("date").lt(to.plusMinutes(1))));

        if (request.getEndpoint() != null) {
            aggregationOperations.add(Aggregation.match(
                    Criteria.where("endpoint").is(request.getEndpoint())));
        }
        if (request.getFiltering() != null) {
            if (request.getFiltering().getObGroupNames() != null) {
                aggregationOperations.add(Aggregation.match(
                        Criteria.where("endpointType").in(request.getFiltering().getObGroupNames())));
            }
            if (request.getFiltering().getStatus() != null) {
                aggregationOperations.add(Aggregation.match(
                        Criteria.where("responseStatus").in(request.getFiltering().getStatus())));
            }
            if (request.getFiltering().getTpps() != null) {
                aggregationOperations.add(Aggregation.match(
                        Criteria.where("identityId").in(request.getFiltering().getTpps())));
            }
        }
        aggregationOperations.add(Aggregation.group(field).first(field).as("id"));
        aggregationOperations.add(project(field));

        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();
        Aggregation aggregation = newAggregation(aggregationOperations).withOptions(options);
        //Convert the aggregation result into a List
        AggregationResults<Keys> groupResults
                = mongoTemplate.aggregate(aggregation, EndpointUsageAggregate.class, Keys.class);
        return groupResults.getMappedResults().stream().map(Keys::getId).collect(Collectors.toList());
    }

    @Override
    public String getMax(EndpointUsageKpiRequest request, DateTime from, DateTime to, String field) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        aggregationOperations.add( Aggregation.match(
                Criteria.where("date").gt(from.minusMinutes(1))));
        aggregationOperations.add( Aggregation.match(
                Criteria.where("date").lt(to.plusMinutes(1))));

        if (request.getEndpoint() != null) {
            aggregationOperations.add(Aggregation.match(
                    Criteria.where("endpoint").is(request.getEndpoint())));
        }
        if (request.getFiltering() != null) {
            if (request.getFiltering().getObGroupNames() != null) {
                aggregationOperations.add(Aggregation.match(
                        Criteria.where("endpointType").in(request.getFiltering().getObGroupNames())));
            }
            if (request.getFiltering().getStatus() != null) {
                aggregationOperations.add(Aggregation.match(
                        Criteria.where("responseStatus").in(request.getFiltering().getStatus())));
            }
            if (request.getFiltering().getTpps() != null) {
                aggregationOperations.add(Aggregation.match(
                        Criteria.where("identityId").in(request.getFiltering().getTpps())));
            }
        }
        aggregationOperations.add(Aggregation.unwind(field));
        GroupOperation computeMaxGlobaly = Aggregation.group()
                .max(field).as("max");
        aggregationOperations.add(computeMaxGlobaly);

        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();
        Aggregation aggregation = newAggregation(aggregationOperations).withOptions(options);
        //Convert the aggregation result into a List
        AggregationResults<MaxResult> groupResults
                = mongoTemplate.aggregate(aggregation, EndpointUsageAggregate.class, MaxResult.class);
        if (groupResults.getMappedResults().size() == 0) {
            return "0";
        }
        return groupResults.getMappedResults().get(0).getMax();
    }

    @Builder
    @Data
    @EqualsAndHashCode
    public static class Keys {
        private String id;

    }

    @Builder
    @Data
    @EqualsAndHashCode
    public static class MaxResult {
        private String max;

    }

    @Override
    public Page<EndpointUsageAggregate> getEndpointUsageAggregateGroupBy(EndpointTableRequest request, PageRequest pageRequest) {

        try {
            CompletableFuture<CloseableIterator<EndpointUsageAggregate>> queryTable = CompletableFuture.supplyAsync(() -> queryAggregateTable(request, pageRequest));
            CompletableFuture<Long> countItems = CompletableFuture.supplyAsync(() -> totalCount(request));
            CloseableIterator<EndpointUsageAggregate> groupResults = queryTable.get();
            Long nbItems = countItems.get();
            int nbPages = (int) Math.round(Math.ceil(nbItems / (pageRequest.getPageSize() * 1.0)));
            Stream<EndpointUsageAggregate> groupResultsAsStream = StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(groupResults, Spliterator.ORDERED),
                    false);
            return new EndpointUsagePage(nbPages, nbItems, pageRequest, groupResultsAsStream);
        }  catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private CloseableIterator<EndpointUsageAggregate> queryAggregateTable(EndpointTableRequest request, PageRequest pageRequest) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        //Filtering
        aggregationOperations.add(Aggregation.match(
                Criteria.where("date").gt(request.getFrom().minusMinutes(1)
                ).lt(request.getTo().plusMinutes(1)
                )));
        for(EndpointTableFilter filter: request.getFilters()) {
            aggregationOperations.add(Aggregation.match(
                    Criteria.where(filter.getField()).regex(filter.getRegex())));
        }

        //Grouping
        GroupOperation groupOperation = Aggregation.group(request.getFields().toArray(new String[0]))
                .first("tppEntry").as("tppEntry")
                .sum("count").as("count")
                .sum("responseTimesSum").as("responseTimesSum")
                .push("responseTimesHistory").as("responseTimesHistorySerialised")
                .sum("responseTimesByKbSum").as("responseTimesByKbSum")
                .push("responseTimesByKbHistory").as("responseTimesHistoryByKbSerialised")
                ;

        for(String field: request.getFields()) {
            groupOperation = groupOperation.first(field).as(field);
        }

        aggregationOperations.add(groupOperation);


        //Projecting
        ProjectionOperation pr = project(request.getFields().toArray(new String[0]))
                .and("tppEntry").as("tppEntry")
                .and("count").as("count")
                .and("responseTimesSum").as("responseTimesSum")
                .and("responseTimesHistorySerialised").as("responseTimesHistorySerialised")
                .and("responseTimesByKbSum").as("responseTimesByKbSum")
                .and("responseTimesHistoryByKbSerialised").as("responseTimesHistoryByKbSerialised")
                ;

        for(String field: request.getFields()) {
            pr = pr.and(field).as(field);
        }

        aggregationOperations.add(pr);

        // Page sorting
        List<Sort.Order> orders = pageRequest.getSort().get().collect(Collectors.toList());
        //Need to also sort on the keys as otherwise, the order of elements can differ from one request to the next
        for (String field: request.getFields()) {
            if (!orders.stream().filter(o -> o.getProperty().equals(field)).findAny().isPresent()) {
                orders.add(Sort.Order.by(field));
            }
        }
        aggregationOperations.add( Aggregation.sort(Sort.by(orders)));

        // Page filtering
        aggregationOperations.add(Aggregation.skip((long) (pageRequest.getPageNumber() * pageRequest.getPageSize())));
        aggregationOperations.add(Aggregation.limit(pageRequest.getPageSize()));

        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();
        Aggregation aggregation = newAggregation(aggregationOperations).withOptions(options);

        log.debug("Aggregation request with pagination : Starting");
        CloseableIterator<EndpointUsageAggregate> groupResults
                = mongoTemplate.aggregateStream(aggregation, EndpointUsageAggregate.class, EndpointUsageAggregate.class);
        log.debug("Aggregation request with pagination : Done");
        return groupResults;
    }


    private Long totalCount(EndpointTableRequest request) {
        log.debug("Count the number of items in total");
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        //Filtering
        aggregationOperations.add(Aggregation.match(
                Criteria.where("date").gt(request.getFrom().minusMinutes(1)
                ).lt(request.getTo().plusMinutes(1)
                )));
        for(EndpointTableFilter filter: request.getFilters()) {
            aggregationOperations.add(Aggregation.match(
                    Criteria.where(filter.getField()).regex(filter.getRegex())));
        }

        //Grouping
        GroupOperation groupOperation = Aggregation.group(request.getFields().toArray(new String[0]))
                .first("tppEntry").as("tppEntry");
        for(String field: request.getFields()) {
            groupOperation = groupOperation.first(field).as(field);
        }

        aggregationOperations.add(groupOperation);
        aggregationOperations.add(Aggregation.count().as("count"));

        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();
        Aggregation aggregationForPageSize = newAggregation(aggregationOperations).withOptions(options);

        log.debug("Run request");
        AggregationResults<NbEntries> pageRefResults
                = mongoTemplate.aggregate(aggregationForPageSize, EndpointUsageAggregate.class, NbEntries.class);
        log.debug("Request done");

        long totalCount = pageRefResults.getMappedResults().size() > 0 ?  pageRefResults.getMappedResults().get(0).getCount() : 0;
        log.debug("nb Items: {}", totalCount);
        return totalCount;
    }

    @Builder
    @Data
    @EqualsAndHashCode
    public static class NbEntries {
        long count;
    }
}
