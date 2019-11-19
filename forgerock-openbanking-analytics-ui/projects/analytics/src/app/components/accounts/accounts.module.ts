import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslateModule } from '@ngx-translate/core';

import { ForgerockChartModule } from 'forgerock/src/app/components/forgerock-chart/forgerock-chart.module';
import { ForgerockAlertModule } from 'forgerock/src/app/components/forgerock-alert/forgerock-alert.module';

import { MetricsService } from '../../services/metrics.service';
import { AccountsFlowContainerComponent } from './accounts-flow.container';
import { AccountsTableModule } from './accounts-table/accounts-table.module';
import { AccountsOBVersionsContainerComponent } from 'analytics/src/app/components/accounts/accounts-ob-versions.container';
import { AccountsConsentTypeContainerComponent } from 'analytics/src/app/components/accounts/accounts-consent-type.container';

const declarations = [
  AccountsFlowContainerComponent,
  AccountsOBVersionsContainerComponent,
  AccountsConsentTypeContainerComponent
];

@NgModule({
  imports: [
    CommonModule,
    ForgerockChartModule,
    MatProgressBarModule,
    AccountsTableModule,
    ForgerockAlertModule,
    TranslateModule
  ],
  declarations,
  entryComponents: declarations,
  exports: [...declarations, AccountsTableModule],
  providers: [MetricsService]
})
export class AccountsWidgetsModule {}
