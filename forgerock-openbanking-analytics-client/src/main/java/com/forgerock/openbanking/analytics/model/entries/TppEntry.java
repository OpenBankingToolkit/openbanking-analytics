/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.entries;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.forgerock.openbanking.model.SoftwareStatementRole;
import com.forgerock.openbanking.serialiser.IsoDateTimeDeserializer;
import com.forgerock.openbanking.serialiser.IsoDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class TppEntry {
    @Id
    @Indexed
    private String oidcClientId;
    @Indexed
    public String name;
    private String logoUri;
    private Set<SoftwareStatementRole> types = new HashSet<>();

    @Indexed
    public String directoryId;
    public String softwareId;
    @Indexed
    public String organisationId;
    public String organisationName;
    @JsonDeserialize(using = IsoDateTimeDeserializer.class)
    @JsonSerialize(using = IsoDateTimeSerializer.class)
    @Indexed
    private DateTime created;
    @JsonDeserialize(using = IsoDateTimeDeserializer.class)
    @JsonSerialize(using = IsoDateTimeSerializer.class)
    @Indexed
    private DateTime deleted;
}
