/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.models;

import com.forgerock.openbanking.analytics.model.entries.TokenUsage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.index.Indexed;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TokenUsageEntry {

    @Indexed
    private TokenUsage tokenUsage;
    private DateTime day;
    private Long count;

    /**
     * Create a new token usage entry for today (rounded). Token usage entries are aggregated counts of tokens created per day
     * @param tokenUsage token usage being created
     * @return token usage with todays date
     */
    public static TokenUsageEntry today(TokenUsage tokenUsage) {
        return new TokenUsageEntry(tokenUsage, DateTime.now()
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0),
                null);
    }

}
