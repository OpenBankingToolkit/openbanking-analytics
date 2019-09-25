/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.repository;


import com.forgerock.openbanking.analytics.model.IntentType;
import com.forgerock.openbanking.analytics.model.openbanking.OBGroupName;
import com.forgerock.openbanking.analytics.models.ConsentActivities;
import com.forgerock.openbanking.analytics.models.ConsentTypeCounter;
import org.joda.time.DateTime;

import java.util.List;

public interface ConsentStatusEntryRepositoryCustom {

    List<ConsentActivities> countActiveConsentStatusBetweenDate(DateTime from, DateTime to, IntentType consentType);

    List<ConsentTypeCounter> countConsentsCreatedBetweenDate(OBGroupName obGroupName, DateTime from, DateTime to);
}
