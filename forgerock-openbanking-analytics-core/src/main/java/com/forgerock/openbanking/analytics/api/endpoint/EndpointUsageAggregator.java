/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.api.endpoint;

import com.forgerock.openbanking.analytics.charts.Table;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageEntry;
import com.forgerock.openbanking.analytics.model.kpi.EndpointStatisticKPI;
import com.forgerock.openbanking.analytics.model.kpi.EndpointTableRequest;
import com.forgerock.openbanking.analytics.repository.EndpointUsageAggregateRepository;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.mongodb.core.query.Criteria.byExample;

@Slf4j
@Component
public class EndpointUsageAggregator {

    private EndpointUsageAggregateRepository endpointUsageAggregateRepository;
    private MongoTemplate mongoTemplate;

    public EndpointUsageAggregator(EndpointUsageAggregateRepository endpointUsageAggregateRepository, MongoTemplate mongoTemplate) {
        this.endpointUsageAggregateRepository = endpointUsageAggregateRepository;
        this.mongoTemplate = mongoTemplate;
    }

    void aggregate(List<EndpointUsageEntry> endpointUsageEntries) {


        // group by collectors
        List<Collector<EndpointUsageAggregate, ?, ?>> collectors = Arrays.asList(
                Collectors.counting(),
                Collectors.summarizingLong(e -> e.getResponseTimesSum() != null ? e.getResponseTimesSum(): 0),
                Collectors.mapping(EndpointUsageAggregate::getResponseTimesSum, Collectors.toList()),
                Collectors.summarizingLong(e -> e.getResponseTimesByKbSum() != null ? e.getResponseTimesByKbSum(): 0),
                Collectors.mapping(EndpointUsageAggregate::getResponseTimesByKbSum, Collectors.toList())

        );

        Map<EndpointUsageAggregate, List<Object>> groupByResult = groupByWithMultiCollector(collectors, endpointUsageEntries);

        log.debug("Adding or creating EndpointUsageAggregate count={}", groupByResult.keySet().size());
        groupByResult.forEach((k, collectorsResult) -> upsertByExample(
                k.example(),
                (Long) collectorsResult.get(0),
                ((LongSummaryStatistics) collectorsResult.get(1)).getSum(),
                (List<Long>) collectorsResult.get(2),
                ((LongSummaryStatistics) collectorsResult.get(3)).getSum(),
                (List<Long>) collectorsResult.get(4)
                ));

        log.debug("EndpointUsageAggregates added to the database");
    }

    /**
     * Group the endpoint usage entry and apply multiple collectors.
     * Each collector will do an operation on the group by entry.
     *
     * Solution for multiple collector from: https://stackoverflow.com/questions/32071726/java-8-stream-groupingby-with-multiple-collectors
     * @param collectors
     * @param endpointUsageEntries
     * @return For each new entry of the group by, return a list of result. The result[i] correspond to the result of the operation collectors[i]
     */
    private Map<EndpointUsageAggregate, List<Object>> groupByWithMultiCollector(List<Collector<EndpointUsageAggregate, ?, ?>> collectors,
                                                                           List<EndpointUsageEntry> endpointUsageEntries) {

        Collector<EndpointUsageAggregate, List<Object>, List<Object>> complexCollector = Collector.of(
                () -> collectors.stream().map(Collector::supplier)
                        .map(Supplier::get).collect(toList()),
                (list, e) -> IntStream.range(0, collectors.size()).forEach(
                        i -> ((BiConsumer<Object, EndpointUsageAggregate>) collectors.get(i).accumulator()).accept(list.get(i), e)),
                (l1, l2) -> {
                    IntStream.range(0, collectors.size()).forEach(
                            i -> l1.set(i, ((BinaryOperator<Object>) collectors.get(i).combiner()).apply(l1.get(i), l2.get(i))));
                    return l1;
                },
                list -> {
                    IntStream.range(0, collectors.size()).forEach(
                            i -> list.set(i, ((Function<Object, Object>)collectors.get(i).finisher()).apply(list.get(i))));
                    return list;
                });

        return endpointUsageEntries.stream()
                .map(EndpointUsageAggregate::newAggregate)
                .collect(Collectors.groupingBy(Function.identity(), complexCollector));
    }

    /**
     * Increase the example count given an example EndpointUsageAggregate.
     * This will essentially build a dynamic where clause e.g.
     * where endpoint="access-consent" and method="GET"...
     *
     * @param example an example {@link EndpointUsageAggregate} to create the dynamic query with
     * @param totalItem the amount to increase the count by
     * @param responseTimeCount the sum of the response time
     * @return true if the upsert happened because the example was found, false if the example is not yet stored and was created
     */
    private boolean upsertByExample(EndpointUsageAggregate example, Long totalItem,
                                    Long responseTimeCount, List<Long> responseTimes,  Long responseTimeByMBCount, List<Long> responseTimesByMB) {
        UpdateResult upsert = mongoTemplate.upsert(new Query(byExample(example)),
                new Update()
                        .inc("count", totalItem)
                        .inc("responseTimesSum", responseTimeCount)
                        .push("responseTimesHistory").each(
                                responseTimes.stream()
                                     .filter(Objects::nonNull)
                                    .collect(toList()))
                        .inc("responseTimesByKbSum", responseTimeByMBCount)
                        .push("responseTimesByKbHistory").each(
                                responseTimesByMB.stream()
                                        .filter(Objects::nonNull)
                                        .collect(toList()))
                , EndpointUsageAggregate.class);
        return upsert.getModifiedCount() > 0;
    }

    public Table<EndpointUsageAggregate> getAggregation(EndpointTableRequest request) {
        log.debug("Get aggregation for request {}", request);
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize(),
                Sort.by(

                request.getSort().stream().map(s -> new Sort.Order(s.getDirection(), s.getField())).collect(toList())));
        log.debug("Get entry from database");
        Page<EndpointUsageAggregate> results = endpointUsageAggregateRepository.getEndpointUsageAggregateGroupBy(request, pageRequest);
        log.debug("Build response");
        return Table.<EndpointUsageAggregate>builder()
                .data(results.getContent().stream().map(
                        e -> {
                            EndpointStatisticKPI endpointStatisticKPI = new EndpointStatisticKPI();
                            endpointStatisticKPI.incrementCounters(e);
                            e.setEndpointStatisticKPI(endpointStatisticKPI);
                            return e.dropHistoricData();
                        }
                ).collect(toList()))
                .totalPages(results.getTotalPages())
                .currentPage(results.getNumber())
                .totalResults(results.getTotalElements())
                .build();
    }
}
