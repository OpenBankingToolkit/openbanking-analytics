/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.entries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class GeoIP {

    @Indexed
    private String ipAddress;

    private Integer accuracyRadius;
    private Double latitude;
    private Double longitude;
    private String country;
    private String countryIsoCode;
    private String continent;
    private String continentCode;
    private String city;
    private Integer cityCode;
    private String postcode;

}
