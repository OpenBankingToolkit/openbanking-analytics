/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.models;

import com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EndpointUsagePage implements Page<EndpointUsageAggregate> {

    private int nbPages;
    private long nbElements;
    private PageRequest pageRequest;
    private Stream<EndpointUsageAggregate> groupResults;

    public EndpointUsagePage(int nbPages, long nbElements, PageRequest pageRequest, Stream<EndpointUsageAggregate> groupResults) {
        this.nbPages = nbPages;
        this.nbElements = nbElements;
        this.pageRequest = pageRequest;
        this.groupResults = groupResults;
    }

    @Override
    public Iterator<EndpointUsageAggregate> iterator() {
        return null;
    }

    @Override
    public int getTotalPages() {
        return nbPages;
    }

    @Override
    public long getTotalElements() {
        return  nbElements;
    }

    @Override
    public int getNumber() {
        return pageRequest.getPageNumber();
    }

    @Override
    public int getSize() {
        return  Math.toIntExact(groupResults.count());
    }

    @Override
    public int getNumberOfElements() {
        return Math.toIntExact(groupResults.count());
    }

    @Override
    public List<EndpointUsageAggregate> getContent() {
        return groupResults
                .map(e -> {
                    if (e.getResponseTimesHistorySerialised() != null && !e.getResponseTimesHistorySerialised().isEmpty()) {
                        e.setResponseTimesHistory(
                                Stream.of(e.getResponseTimesHistorySerialised().split(",")).map(s -> Long.valueOf(s)).collect(Collectors.toList())
                        );
                    }
                    if (e.getResponseTimesHistoryByKbSerialised() != null && !e.getResponseTimesHistoryByKbSerialised().isEmpty()) {
                        e.setResponseTimesByKbHistory(
                                Stream.of(e.getResponseTimesHistoryByKbSerialised().split(",")).map(s -> Long.valueOf(s)).collect(Collectors.toList())
                        );
                    }
                    return e;
                })

                .collect(Collectors.toList());

    }

    @Override
    public boolean hasContent() {
        return true;
    }

    @Override
    public Sort getSort() {
        return pageRequest.getSort();
    }

    @Override
    public boolean isFirst() {
        return false;
    }

    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }

    @Override
    public Pageable nextPageable() {
        return null;
    }

    @Override
    public Pageable previousPageable() {
        return null;
    }

    @Override
    public <U> Page<U> map(Function<? super EndpointUsageAggregate, ? extends U> function) {
        return null;
    }
}
