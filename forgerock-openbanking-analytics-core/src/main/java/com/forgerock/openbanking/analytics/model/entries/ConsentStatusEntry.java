/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.entries;

import com.forgerock.openbanking.analytics.model.IntentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Document
public class ConsentStatusEntry {

    @Indexed
    private String consentId;
    @Indexed
    private String consentStatus;
    @Indexed
    private IntentType consentType;
    @Indexed
    private DateTime date;

    public ConsentStatusEntry(String consentId, String consentStatus) {
        this.consentId = consentId;
        this.consentStatus = consentStatus;
        this.consentType = IntentType.identify(consentId);
        this.date = DateTime.now();
    }
}
