/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.upgrade;

import com.forgerock.openbanking.analytics.api.endpoint.EndpointUsageKpiAPIController;
import com.forgerock.openbanking.analytics.api.tpp.TppsKpiAPIController;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageEntry;
import com.forgerock.openbanking.analytics.model.entries.TppEntry;
import com.forgerock.openbanking.analytics.model.openbanking.OBGroupName;
import com.forgerock.openbanking.analytics.repository.EndpointUsageAggregateRepository;
import com.forgerock.openbanking.analytics.repository.EndpointUsageEntryRepository;
import com.forgerock.openbanking.analytics.repository.TppEntryRepository;
import com.forgerock.openbanking.analytics.repository.TppRepository;
import com.forgerock.openbanking.constants.OpenBankingConstants;
import com.forgerock.openbanking.model.Tpp;
import com.forgerock.openbanking.model.UserContext;
import com.forgerock.openbanking.model.UserGroup;
import com.forgerock.openbanking.upgrade.exceptions.UpgradeException;
import com.forgerock.openbanking.upgrade.model.UpgradeMeta;
import com.forgerock.openbanking.upgrade.model.UpgradeStep;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@UpgradeMeta(version = "FAT-BOY")
@Slf4j
public class UpgradeStep_FAT_BOY implements UpgradeStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpgradeStep.class);

    @Autowired
    private EndpointUsageAggregateRepository endpointUsageAggregateRepository;
    @Autowired
    private EndpointUsageEntryRepository endpointUsageEntryRepository;
    @Autowired
    private EndpointUsageKpiAPIController endpointUsageKpiAPI;
    @Autowired
    private TppRepository tppRepository;
    @Autowired
    private TppEntryRepository tppEntryRepository;
    @Autowired
    private TppsKpiAPIController tppsKpiAPIController;

    @Override
    public boolean upgrade() throws UpgradeException {
        LOGGER.debug("Start upgrading to version FAT-BOY");
        try {
            fakeAuthContext();

            upgradeOldTppMetric();

            upgradeOldEndpointUsage();

        } catch (Exception e) {
            throw new UpgradeException("Could not upgrade to FAT-BOY", e);
        } finally {
            SecurityContextHolder.clearContext();
        }
        return true;
    }

    private void upgradeOldTppMetric() {
        tppEntryRepository.deleteAll();
        List<Tpp> tpps = tppRepository.findAll();
        tpps.stream().map(tpp -> {
            String softwareId = "";
            String organisationId = "";
            String organisationName = "";
            try {
                softwareId = tpp.getSsaClaim().getStringClaim(OpenBankingConstants.SSAClaims.SOFTWARE_ID);
                organisationId = tpp.getSsaClaim().getStringClaim(OpenBankingConstants.SSAClaims.ORG_ID);
                organisationName = tpp.getSsaClaim().getStringClaim(OpenBankingConstants.SSAClaims.ORG_NAME);
            } catch (ParseException e) {
                log.error("Can't read SSA claims", e);
            }
            return TppEntry.builder()
                    .oidcClientId(tpp.getClientId())
                    .name(tpp.getName())
                    .logoUri(tpp.getLogo())
                    .created(new DateTime(tpp.getCreated()))
                    .types(tpp.getTypes())
                    .directoryId(tpp.getDirectoryId())
                    .softwareId(softwareId)
                    .organisationId(organisationId)
                    .organisationName(organisationName)
                    .build();
        }).forEach(e -> tppsKpiAPIController.postTPPEntries(Collections.singletonList(e)));
        log.info("Number of TPP entries {}", tppEntryRepository.count());
    }

    private void upgradeOldEndpointUsage() {
        DateTime limitDate = DateTime.now().withMonthOfYear(1).withDayOfMonth(1).minusDays(1);

        DateTime to = DateTime.now();
        int nbDaysByPeriod = 1;
        DateTime from = to.minusDays(nbDaysByPeriod);

        endpointUsageAggregateRepository.deleteByDateBetween(limitDate, to);

        final int numberOfPeriod = (int) Math.round(to.getDayOfYear() / (nbDaysByPeriod * 1.0));
        long total = endpointUsageEntryRepository.countByDateBetween(limitDate, to);

        log.info("Number of period {}", numberOfPeriod);
        log.info("Number of entries {}", total);
        long nbEntriesComputed = 0;

        while (limitDate.isBefore(from)) {
            log.info("For the period from={} to={}", from, to);
            long totalPeriod = endpointUsageEntryRepository.countByDateBetween(from, to);
            log.info("Number of endpoint usage entries to compute for this period: {}", totalPeriod);
            if (totalPeriod != 0) {
                final DateTime startUpgrade = DateTime.now();
                Stream<EndpointUsageEntry> byDateBetween = endpointUsageEntryRepository.findByDateBetween(from, to);
                List<EndpointUsageEntry> collect = byDateBetween.parallel().map(e -> convertEntryUsage(e)).collect(Collectors.toList());
                endpointUsageKpiAPI.addEntries(collect);
                nbEntriesComputed += totalPeriod;

                double milliByEntry = ((DateTime.now().getMillis() - startUpgrade.getMillis())) / (totalPeriod * 1.0);
                long progress = (nbEntriesComputed * 100)/ total;
                log.info("Progress: {}% - {} / {} -- Estimated end time :{}  -- speed : {} ms/entry",
                        progress,
                        nbEntriesComputed,
                        total,
                        DateTime.now().plusMillis((int) Math.round(milliByEntry * (total - nbEntriesComputed))),
                        milliByEntry
                );
            }
            to = from;
            from = from.minusDays(nbDaysByPeriod);
        }
    }

    public void fakeAuthContext() {
        if (SecurityContextHolder.getContext() == null
                || SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContext ctx = SecurityContextHolder.createEmptyContext();
            SecurityContextHolder.setContext(ctx);
            UserContext upgrade = UserContext.create("Upgrade", Collections.singletonList(UserGroup.GROUP_FORGEROCK), UserContext.UserType.UPGRADE, new X509Certificate[0]);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(upgrade, "", upgrade.getAuthorities());
            ctx.setAuthentication(authenticationToken);
        }
    }

    private EndpointUsageEntry convertEntryUsage(EndpointUsageEntry endpointUsageEntry) {
        HttpMethod httpMethod;
        String endpoint;
        endpointUsageEntry.setResponseTime(0l);
        endpointUsageEntry.setResponseTimeByKb(0l);
        if (endpointUsageEntry == null || endpointUsageEntry.getEndpoint() == null) {
            return endpointUsageEntry;
        }
        String[] oldEndpointFormat = endpointUsageEntry.getEndpoint().split(" ");
        if (oldEndpointFormat.length > 1) {
            httpMethod = HttpMethod.valueOf(oldEndpointFormat[0]);
            endpoint = oldEndpointFormat[1];
        } else {
            httpMethod = HttpMethod.GET;
            endpoint = oldEndpointFormat[0];
        }
        endpointUsageEntry.setMethod(httpMethod.name());
        endpointUsageEntry.setEndpoint(endpoint);

        if (endpoint.contains("open-banking")) {
            endpointUsageEntry.setApplication("rs-api");

            if (endpoint.contains("v3.1")) {
                endpointUsageEntry.setObVersion("3.1");
            } else if (endpoint.contains("v3.1.1")) {
                endpointUsageEntry.setObVersion("3.1.1");
            } else if (endpoint.contains("v3.0")) {
                endpointUsageEntry.setObVersion("3.0");
            } else if (endpoint.contains("v2.0")) {
                endpointUsageEntry.setObVersion("2.0");
            } else if (endpoint.contains("v1.1")) {
                endpointUsageEntry.setObVersion("1.1");
            }

            if (endpoint.contains("aisp")) {
                endpointUsageEntry.setEndpointType(OBGroupName.AISP);
            } else if (endpoint.contains("pisp")) {
                endpointUsageEntry.setEndpointType(OBGroupName.PISP);
            } else if (endpoint.contains("cbpii")) {
                endpointUsageEntry.setEndpointType(OBGroupName.CBPII);
            } else if (endpoint.contains("callback-urls")) {
                endpointUsageEntry.setEndpointType(OBGroupName.EVENT);
            }
        }


        if (endpoint.contains("oauth2") || endpoint.contains("open-banking/register")) {
            endpointUsageEntry.setApplication("as-api");
        } else if (endpoint.startsWith("jwkms")) {
            endpointUsageEntry.setApplication("jwkms");
        } else if (endpoint.startsWith("as-api")) {
            endpointUsageEntry.setApplication("as-api");
        } else if (endpoint.startsWith("directory-services")) {
            endpointUsageEntry.setApplication("directory-services");
        } else if (endpoint.startsWith("rs-api")) {
            endpointUsageEntry.setApplication("rs-api");
        }


        return endpointUsageEntry;
    }
}
