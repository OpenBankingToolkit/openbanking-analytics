/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.entries;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.forgerock.openbanking.analytics.model.kpi.EndpointStatisticKPI;
import com.forgerock.openbanking.analytics.model.openbanking.OBGroupName;
import com.forgerock.openbanking.analytics.model.openbanking.OBReference;
import com.forgerock.openbanking.auth.model.UserContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class EndpointUsageAggregate {

    @Indexed
    private String identityId;
    @Indexed
    private String endpoint;
    @Indexed
    private DateTime date;
    @Indexed
    private OBGroupName endpointType;
    @Indexed
    private String method;
    @Indexed
    private String application;
    @Indexed
    private String responseStatus;
    @Indexed
    private UserContext.UserType userType;
    @Indexed
    private String obVersion;
    @Indexed
    private OBReference obReference;
    private GeoIP geoIP;

    private TppEntry tppEntry;
    private Long count;
    private Long responseTimesSum;
    private List<Long> responseTimesHistory;
    private Long responseTimesByKbSum;
    private List<Long> responseTimesByKbHistory;
    private EndpointStatisticKPI endpointStatisticKPI;

    @JsonIgnore
    private String responseTimesHistorySerialised;
    @JsonIgnore
    private String responseTimesHistoryByKbSerialised;

    public static EndpointUsageAggregate newAggregate(EndpointUsageEntry entry) {
        return EndpointUsageAggregate.builder()
                .identityId(entry.getIdentityId())
                .date(entry.getDate().withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).withZone(DateTimeZone.UTC))
                .responseTimesSum(entry.getResponseTime())
                .responseTimesByKbSum(entry.getResponseTimeByKb() != null ? entry.getResponseTimeByKb() : 0)
                .responseTimesHistory(entry.getResponseTime() != null ? Collections.singletonList(entry.getResponseTime()) : Collections.emptyList())
                .responseTimesByKbHistory(entry.getResponseTimeByKb() != null ? Collections.singletonList(entry.getResponseTimeByKb()) : Collections.emptyList())
                .endpoint(entry.getEndpoint())
                .endpointType(entry.getEndpointType())
                .method(entry.getMethod())
                .application(entry.getApplication())
                .responseStatus(entry.getResponseStatus())
                .obVersion(entry.getObVersion())
                .obReference(entry.getObReference())
                .userType(entry.getUserType())
                .tppEntry(entry.getTppEntry())
                .geoIP(entry.getGeoIP())
                .count(0L) //done by the database
                .build();
    }

    public EndpointUsageAggregate example() {
        return EndpointUsageAggregate.builder()
                .identityId(this.getIdentityId())
                .date(this.getDate())
                .endpoint(this.getEndpoint())
                .endpointType(this.getEndpointType())
                .method(this.getMethod())
                .application(this.getApplication())
                .responseStatus(this.getResponseStatus())
                .obVersion(this.getObVersion())
                .obReference(this.getObReference())
                .userType(this.getUserType())
                .tppEntry(this.getTppEntry())
                .geoIP(this.getGeoIP())
                .build();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndpointUsageAggregate aggregate = (EndpointUsageAggregate) o;
        return Objects.equals(identityId, aggregate.identityId) &&
                Objects.equals(endpoint, aggregate.endpoint) &&
                aggregate.date != null ? aggregate.date.compareTo(date) == 0 : true &&
                Objects.equals(responseTimesSum, aggregate.responseTimesSum) &&
                Objects.equals(responseTimesHistory, aggregate.responseTimesHistory) &&
                Objects.equals(responseTimesByKbSum, aggregate.responseTimesByKbSum) &&
                Objects.equals(responseTimesByKbHistory, aggregate.responseTimesByKbHistory) &&
                Objects.equals(endpointType, aggregate.endpointType) &&
                Objects.equals(method, aggregate.method) &&
                Objects.equals(application, aggregate.application) &&
                Objects.equals(responseStatus, aggregate.responseStatus) &&
                userType == aggregate.userType &&
                Objects.equals(tppEntry, aggregate.tppEntry) &&
                Objects.equals(obVersion, aggregate.obVersion) &&
                Objects.equals(obReference, aggregate.obReference) &&
                Objects.equals(geoIP, aggregate.geoIP) &&
                Objects.equals(count, aggregate.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identityId, endpoint, responseTimesSum, responseTimesHistory, responseTimesByKbSum,
                responseTimesByKbHistory, geoIP, date, method, application, responseStatus, userType, tppEntry,
                obVersion, obReference, count, endpointType);
    }
}
