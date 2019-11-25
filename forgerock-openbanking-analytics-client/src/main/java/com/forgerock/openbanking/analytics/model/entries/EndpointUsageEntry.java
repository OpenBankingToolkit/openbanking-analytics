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
package com.forgerock.openbanking.analytics.model.entries;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.forgerock.openbanking.analytics.model.openbanking.OBGroupName;
import com.forgerock.openbanking.analytics.model.openbanking.OBReference;
import com.forgerock.openbanking.model.UserContext;
import com.forgerock.openbanking.serialiser.IsoDateTimeDeserializer;
import com.forgerock.openbanking.serialiser.IsoDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class EndpointUsageEntry {

    @JsonDeserialize(using = IsoDateTimeDeserializer.class)
    @JsonSerialize(using = IsoDateTimeSerializer.class)

    @Indexed
    private DateTime date;
    private String identityId;
    private String endpoint;
    private OBGroupName endpointType;
    private String obVersion;
    private OBReference obReference;
    private String method;
    private String application;
    private GeoIP geoIP;
    private String responseStatus;
    private Long responseTime;
    private Long responseTimeByKb;

    private UserContext.UserType userType;
    private TppEntry tppEntry;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndpointUsageEntry entry = (EndpointUsageEntry) o;
        return Objects.equals(date, entry.date) &&
                Objects.equals(identityId, entry.identityId) &&
                Objects.equals(endpoint, entry.endpoint) &&
                Objects.equals(method, entry.method) &&
                Objects.equals(application, entry.application) &&
                Objects.equals(responseStatus, entry.responseStatus) &&
                userType == entry.userType &&
                Objects.equals(tppEntry, entry.tppEntry) &&
                Objects.equals(endpointType, entry.endpointType) &&
                Objects.equals(obReference, entry.obReference)&&
                Objects.equals(responseTimeByKb, entry.responseTimeByKb)&&
                Objects.equals(obVersion, entry.obVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, identityId, endpoint, method, application, responseStatus, userType, tppEntry, responseTimeByKb, endpointType);
    }
}
