import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslateModule } from '@ngx-translate/core';

import { ForgerockChartModule } from '@forgerock/openbanking-ngx-common/components/forgerock-chart';
import { ForgerockAlertModule } from '@forgerock/openbanking-ngx-common/components/forgerock-alert';

import { MetricsService } from '../../services/metrics.service';
import { FundsConfirmationConsentActivitiesContainerComponent } from './funds-confirmation-flow.container';
import { FundsConfirmationTableModule } from './funds-confirmation-table/funds-confirmation-table.module';
import { FundsOBVersionsContainerComponent } from 'analytics/src/app/components/funds-confirmation/funds-ob-versions.container';
import { FundsConsentTypeContainerComponent } from 'analytics/src/app/components/funds-confirmation/funds-consent-type.container';

const declarations = [
  FundsConfirmationConsentActivitiesContainerComponent,
  FundsOBVersionsContainerComponent,
  FundsConsentTypeContainerComponent
];

@NgModule({
  imports: [
    CommonModule,
    ForgerockChartModule,
    MatProgressBarModule,
    FundsConfirmationTableModule,
    ForgerockAlertModule,
    TranslateModule
  ],
  declarations,
  entryComponents: declarations,
  exports: [...declarations, FundsConfirmationTableModule],
  providers: [MetricsService]
})
export class FundsConfirmationWidgetsModule {}
