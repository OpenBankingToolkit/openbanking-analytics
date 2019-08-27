/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.services.openbanking.event;

import com.forgerock.openbanking.analytics.exception.OBErrorException;
import com.forgerock.openbanking.analytics.exception.OBErrorResponseException;
import org.junit.Test;
import uk.org.openbanking.datamodel.event.OBEventSubscription1;
import uk.org.openbanking.datamodel.event.OBEventSubscription1Data;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EventValidationServiceTest {

    @Test
    public void verifyValidCallbackUrl() throws Exception{
        // Valid - end in /event-notifications
        EventValidationService.verifyValidCallbackUrl(new OBEventSubscription1()
                .data(new OBEventSubscription1Data()
                        .callbackUrl("http://tpp.com/v3.1/event-notifications")
                ));
        EventValidationService.verifyValidCallbackUrl(new OBEventSubscription1()
                .data(new OBEventSubscription1Data()
                        .callbackUrl("https://www.tpp.com/v3.1.2/event-notifications")
                ));


        // Empty, ignore (callback is not mandatory)
        EventValidationService.verifyValidCallbackUrl(new OBEventSubscription1()
                .data(new OBEventSubscription1Data()
                        .callbackUrl("")
                ));

        // Invalid - wrong resource name
        assertThatThrownBy(() -> EventValidationService.verifyValidCallbackUrl(new OBEventSubscription1()
                .data(new OBEventSubscription1Data()
                        .callbackUrl("http://tpp.com/v3.1/events")
                ))
        ).isInstanceOf(OBErrorException.class);

        // Not a URL
        assertThatThrownBy(() -> EventValidationService.verifyValidCallbackUrl(new OBEventSubscription1()
                .data(new OBEventSubscription1Data()
                        .callbackUrl("tpp.com/v3.1/event-notifications")
                ))
        ).isInstanceOf(OBErrorException.class);

        // No OB version
        assertThatThrownBy(() -> EventValidationService.verifyValidCallbackUrl(new OBEventSubscription1()
                .data(new OBEventSubscription1Data()
                        .callbackUrl("tpp.com/open-banking/1/event-notifications")
                ))
        ).isInstanceOf(OBErrorException.class);

    }

    @Test
    public void checkEqualOrNewerVersion() throws Exception {
        OBEventSubscription1 v1_1 = new OBEventSubscription1().data(new OBEventSubscription1Data().version("1.1"));
        OBEventSubscription1 v2_0 = new OBEventSubscription1().data(new OBEventSubscription1Data().version("2.0"));
        OBEventSubscription1 v3_0 = new OBEventSubscription1().data(new OBEventSubscription1Data().version("3.0"));
        OBEventSubscription1 v3_1 = new OBEventSubscription1().data(new OBEventSubscription1Data().version("3.1"));
        OBEventSubscription1 v3_1_2 = new OBEventSubscription1().data(new OBEventSubscription1Data().version("3.1.2"));

        // All equal or later versions
        EventValidationService.checkEqualOrNewerVersion(v3_1, v3_1);
        EventValidationService.checkEqualOrNewerVersion(v3_0, v3_1);
        EventValidationService.checkEqualOrNewerVersion(v2_0, v3_1);
        EventValidationService.checkEqualOrNewerVersion(v1_1, v3_1);
        EventValidationService.checkEqualOrNewerVersion(v1_1, v3_0);
        EventValidationService.checkEqualOrNewerVersion(v3_1_2, v3_1_2);

        // Invalid - older version
        assertThatThrownBy(() -> EventValidationService.checkEqualOrNewerVersion(v3_1_2, v3_1)).isInstanceOf(OBErrorResponseException.class);
        assertThatThrownBy(() -> EventValidationService.checkEqualOrNewerVersion(v3_1_2, v3_0)).isInstanceOf(OBErrorResponseException.class);
        assertThatThrownBy(() -> EventValidationService.checkEqualOrNewerVersion(v3_1, v3_0)).isInstanceOf(OBErrorResponseException.class);
        assertThatThrownBy(() -> EventValidationService.checkEqualOrNewerVersion(v3_1, v2_0)).isInstanceOf(OBErrorResponseException.class);
        assertThatThrownBy(() -> EventValidationService.checkEqualOrNewerVersion(v3_1_2, v1_1)).isInstanceOf(OBErrorResponseException.class);
    }
}