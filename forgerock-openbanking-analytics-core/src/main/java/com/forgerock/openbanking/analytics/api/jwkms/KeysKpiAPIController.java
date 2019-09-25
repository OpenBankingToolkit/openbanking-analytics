/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.api.jwkms;

import com.forgerock.openbanking.analytics.charts.Donut;
import com.forgerock.openbanking.analytics.model.kpi.KeysAlgorithmKPI;
import com.forgerock.openbanking.analytics.model.kpi.KeysStatusKPI;
import com.forgerock.openbanking.analytics.model.kpi.KeysTypeKPI;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/kpi/keys")
@PreAuthorize("hasAnyAuthority('GROUP_FORGEROCK', 'GROUP_OB', 'ROLE_FORGEROCK_INTERNAL_APP')")
@Slf4j
public class KeysKpiAPIController {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${jwkms.root}")
    private String jwkmsBaseUrl;

    @RequestMapping(value = "/type", method = RequestMethod.GET)
    public ResponseEntity<Donut> getKeysTypeKPI(
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime
    ) {
        log.debug("Get KPI keys by type from {} to {}", fromDateTime, toDateTime);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(jwkmsBaseUrl + "/actuator/keys/type");
        builder.queryParam("fromDate", fromDateTime.toString(ISODateTimeFormat.dateTime()));
        builder.queryParam("toDate", toDateTime.toString(ISODateTimeFormat.dateTime()));
        URI uri = builder.build().encode().toUri();
        return ResponseEntity.ok(Donut.convertCountersToDonus(restTemplate.getForEntity(uri, KeysTypeKPI.class).getBody().getDataset()));
    }

    @RequestMapping(value = "/algorithm", method = RequestMethod.GET)
    public ResponseEntity<Donut> getKeysAlgorithmKPI(
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime
    ) {
        log.debug("Get KPI keys by algorithms from {} to {}", fromDateTime, toDateTime);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(jwkmsBaseUrl + "/actuator/keys/algorithm");
        builder.queryParam("fromDate", fromDateTime.toString(ISODateTimeFormat.dateTime()));
        builder.queryParam("toDate", toDateTime.toString(ISODateTimeFormat.dateTime()));
        URI uri = builder.build().encode().toUri();
        return ResponseEntity.ok(Donut.convertCountersToDonus(restTemplate.getForEntity(uri, KeysAlgorithmKPI.class).getBody().getDataset()));
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseEntity<Donut> getKeysStatusKPI(
            @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime fromDateTime,
            @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) DateTime toDateTime
    ) {
        log.debug("Get KPI keys by status from {} to {}", fromDateTime, toDateTime);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(jwkmsBaseUrl + "/actuator/keys/status");
        builder.queryParam("fromDate", fromDateTime.toString(ISODateTimeFormat.dateTime()));
        builder.queryParam("toDate", toDateTime.toString(ISODateTimeFormat.dateTime()));
        URI uri = builder.build().encode().toUri();
        return ResponseEntity.ok(Donut.convertCountersToDonus(restTemplate.getForEntity(uri, KeysStatusKPI.class).getBody().getDataset()));
    }
}
