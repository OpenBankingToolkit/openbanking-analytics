import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslateModule } from '@ngx-translate/core';

import { ForgerockChartModule } from '@forgerock/openbanking-ngx-common/components/forgerock-chart';
import { ForgerockAlertModule } from '@forgerock/openbanking-ngx-common/components/forgerock-alert';

import { MetricsService } from '../../services/metrics.service';
// Payments APIs performance
import { PaymentsTableModule } from './payments-table/payments-table.module';
// Single payments consents activities by status
import { PaymentsSingleConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-single-consent-activities.container';
// Domestic payments consents activities by status
import { PaymentsDomesticConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-domestic-consent-activities.container';
// Domestic Standing Orders payments consent activities by status
import { PaymentsDomesticStandingOrdersConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-domestic-standing-orders-consent-activities.container';
// Domestic Scheduled payments consent activities by status
import { PaymentsDomesticScheduledConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-domestic-scheduled-consent-activities.container';
// International payments consent activities by status
import { PaymentsInternationalConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-international-consent-activities.container';
// International Standing Orders payments consent activities by status
import { PaymentsInternationalStandingOrdersConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-international-standing-orders-consent-activities.container';
// International Scheduled payments consent activities by status
import { PaymentsInternationalScheduledConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-international-scheduled-consent-activities.container';
// Payments endpoints called by OIDC clients
import { PaymentsFileConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-file-consent-activities.container';
// Number of payments confirmation of funds by API
import { PaymentsConfirmationOfFundsConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-confirmation-of-funds.container';
// Number of endpoint calls by Open Banking API versions
import { PaymentsOBVersionsContainerComponent } from 'analytics/src/app/components/payments/payments-ob-versions.container';
// New payments consents by consent type
import { PaymentsConsentTypeContainerComponent } from 'analytics/src/app/components/payments/payments-consent-type.container';

const declarations = [
  PaymentsSingleConsentActivitiesContainerComponent,
  PaymentsDomesticConsentActivitiesContainerComponent,
  PaymentsDomesticStandingOrdersConsentActivitiesContainerComponent,
  PaymentsDomesticScheduledConsentActivitiesContainerComponent,
  PaymentsInternationalConsentActivitiesContainerComponent,
  PaymentsInternationalStandingOrdersConsentActivitiesContainerComponent,
  PaymentsInternationalScheduledConsentActivitiesContainerComponent,
  PaymentsFileConsentActivitiesContainerComponent,
  PaymentsConfirmationOfFundsConsentActivitiesContainerComponent,
  PaymentsOBVersionsContainerComponent,
  PaymentsConsentTypeContainerComponent
];

@NgModule({
  imports: [
    CommonModule,
    ForgerockChartModule,
    MatProgressBarModule,
    PaymentsTableModule,
    ForgerockAlertModule,
    TranslateModule
  ],
  declarations,
  entryComponents: declarations,
  exports: [...declarations, PaymentsTableModule],
  providers: [MetricsService]
})
export class PaymentsWidgetsModule {}
