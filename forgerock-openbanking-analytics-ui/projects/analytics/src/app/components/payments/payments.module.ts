import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslateModule } from '@ngx-translate/core';

import { ForgerockChartModule } from 'ob-ui-libs/components/forgerock-chart';
import { ForgerockAlertModule } from 'ob-ui-libs/components/forgerock-alert';

import { MetricsService } from '../../services/metrics.service';
import { PaymentsTableModule } from './payments-table/payments-table.module';
import { PaymentsSingleConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-single-consent-activities.container';
import { PaymentsDomesticConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-domestic-consent-activities.container';
import { PaymentsDomesticStandingOrdersConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-domestic-standing-orders-consent-activities.container';
import { PaymentsDomesticScheduledConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-domestic-scheduled-consent-activities.container';
import { PaymentsInternationalConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-international-consent-activities.container';
import { PaymentsInternationalStandingOrdersConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-international-standing-orders-consent-activities.container';
import { PaymentsInternationalScheduledConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-international-scheduled-consent-activities.container';
import { PaymentsFileConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-file-consent-activities.container';
import { PaymentsConfirmationOfFundsConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-confirmation-of-funds.container';
import { PaymentsOBVersionsContainerComponent } from 'analytics/src/app/components/payments/payments-ob-versions.container';
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
