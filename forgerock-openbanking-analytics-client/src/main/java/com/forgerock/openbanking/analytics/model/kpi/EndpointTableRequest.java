/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.kpi;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.forgerock.openbanking.serialiser.IsoDateTimeDeserializer;
import com.forgerock.openbanking.serialiser.IsoDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public
class EndpointTableRequest {

    Integer DEFAULT_SIZE = 10;
    List<SortOrder> DEFAULT_SORT = Arrays.asList(new SortOrder("count", Sort.Direction.DESC));
    Integer DEFAULT_PAGE = 0;

    public String endpoint;
    @JsonDeserialize(using = IsoDateTimeDeserializer.class)
    @JsonSerialize(using = IsoDateTimeSerializer.class)
    public DateTime from;
    @JsonDeserialize(using = IsoDateTimeDeserializer.class)
    @JsonSerialize(using = IsoDateTimeSerializer.class)
    public DateTime to;
    public Integer page = DEFAULT_PAGE;
    public Integer size = DEFAULT_SIZE;
    public List<SortOrder> sort = DEFAULT_SORT;
    public List<String> fields;
    public List<EndpointTableFilter> filters = new ArrayList<>();
}
