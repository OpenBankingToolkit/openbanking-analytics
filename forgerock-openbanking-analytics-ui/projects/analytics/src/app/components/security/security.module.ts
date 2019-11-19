import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslateModule } from '@ngx-translate/core';

import { ForgerockChartModule } from 'forgerock/src/app/components/forgerock-chart/forgerock-chart.module';
import { ForgerockSplitFlapModule } from 'forgerock/src/app/components/forgerock-splitflap/forgerock-splitflap.module';
import { ForgerockAlertModule } from 'forgerock/src/app/components/forgerock-alert/forgerock-alert.module';

import { MetricsService } from '../../services/metrics.service';
import { SecurityAccessTokenRequestsContainerComponent } from './security-access-token-requests.container';
import { SecurityAuthoriseRequestsContainerComponent } from './security-authorise-requests.container';
import { SecurityAccessTokenContainerComponent } from './security-access-token.container';
import { SecurityIdTokenContainerComponent } from './security-id-token.container';
import { SecurityAccessTokenResponseTimesContainerComponent } from 'analytics/src/app/components/security/security-access-token-response-times.container';
import { SecurityAuthoriseResponseTimesContainerComponent } from 'analytics/src/app/components/security/security-authorise-response-times.container';
import { SecurityTableModule } from 'analytics/src/app/components/security/security-table/security-table.module';

const declarations = [
  SecurityAuthoriseRequestsContainerComponent,
  SecurityAccessTokenRequestsContainerComponent,
  SecurityIdTokenContainerComponent,
  SecurityAccessTokenContainerComponent,
  SecurityAccessTokenResponseTimesContainerComponent,
  SecurityAuthoriseResponseTimesContainerComponent
];

@NgModule({
  imports: [
    CommonModule,
    ForgerockChartModule,
    ForgerockSplitFlapModule,
    MatProgressBarModule,
    ForgerockAlertModule,
    TranslateModule,
    SecurityTableModule
  ],
  declarations,
  entryComponents: declarations,
  exports: declarations,
  providers: [MetricsService, SecurityTableModule]
})
export class SecurityWidgetsModule {}
