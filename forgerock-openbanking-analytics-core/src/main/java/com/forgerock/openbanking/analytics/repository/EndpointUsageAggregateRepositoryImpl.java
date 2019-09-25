/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.repository;

import com.forgerock.openbanking.analytics.api.endpoint.EndpointUsageKpiAPI;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate;
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
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<String> getSetDefinition(EndpointUsageKpiAPI.EndpointUsageKpiRequest request, DateTime from, DateTime to, String field) {

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

    @Builder
    @Data
    @EqualsAndHashCode
    public static class Keys {
        private String id;

    }

    @Override
    public Page<EndpointUsageAggregate> getEndpointUsageAggregateGroupBy(EndpointUsageKpiAPI.EndpointTableRequest  request, PageRequest pageRequest) {

        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        //Filtering
        aggregationOperations.add(Aggregation.match(
                Criteria.where("date").gt(request.getFrom().minusMinutes(1)
                )));
        aggregationOperations.add(Aggregation.match(
                Criteria.where("date").lt(request.getTo().plusMinutes(1)
                )));
        for(EndpointUsageKpiAPI.EndpointTableFilter filter: request.getFilters()) {
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
        List<AggregationOperation> aggregationOperationsForPageSize = new ArrayList<>();
        aggregationOperationsForPageSize.addAll(aggregationOperations);

        // Page sorting
        List<Sort.Order> orders = pageRequest.getSort().get().collect(Collectors.toList());
        //Need to also sort on the keys as otherwise, the order of elements can differ from one request to the next
        for (String field: request.getFields()) {
            if (!orders.stream().filter(o -> o.getProperty().equals(field)).findAny().isPresent()) {
                orders.add(Sort.Order.by(field));
            }
        }
        aggregationOperations.add(Aggregation.sort(Sort.by(orders)));

        // Page filtering
        aggregationOperations.add(Aggregation.skip((long) (pageRequest.getPageNumber() * pageRequest.getPageSize())));
        aggregationOperations.add(Aggregation.limit(pageRequest.getPageSize()));

        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();
        Aggregation aggregation = newAggregation(aggregationOperations).withOptions(options);

        AggregationResults<EndpointUsageAggregate> groupResults
                = mongoTemplate.aggregate(aggregation, EndpointUsageAggregate.class, EndpointUsageAggregate.class);


        Aggregation aggregationForPageSize = newAggregation(aggregationOperationsForPageSize).withOptions(options);

        AggregationResults<EndpointUsageAggregate> pageRefResults
                = mongoTemplate.aggregate(aggregationForPageSize, EndpointUsageAggregate.class, EndpointUsageAggregate.class);

        int nbPages = (int) Math.round(Math.ceil(pageRefResults.getMappedResults().size() / (pageRequest.getPageSize() * 1.0)));

        return new EndpointUsagePage(nbPages,  pageRefResults, pageRequest, groupResults);
    }
}
