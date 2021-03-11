import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslateModule } from '@ngx-translate/core';

import { ForgerockChartModule } from '@forgerock/openbanking-ngx-common/components/forgerock-chart';
import { ForgerockAlertModule } from '@forgerock/openbanking-ngx-common/components/forgerock-alert';
import { ForgerockSplitFlapModule } from '@forgerock/openbanking-ngx-common/components/forgerock-splitflap';

import { MetricsService } from '../../services/metrics.service';
// Number of request to any endpoints of the sandbox, distributed by weeks days
import { GSUAPICallsPerWeekContainerComponent } from 'analytics/src/app/components/gsu/gsu-api-calls-per-week.container';
// Apis performance of the overall sandbox for this period
import { GSUAPICallsContainerComponent } from 'analytics/src/app/components/gsu/gsu-api-calls.container';
// Number of endpoint calls by response status
import { GSUNbrCallByStatusContainerComponent } from 'analytics/src/app/components/gsu/gsu-nbr-call-by-status.container';
// Number of user sessions
import { GSUNbrOBRISessionsContainerComponent } from 'analytics/src/app/components/gsu/gsu-nbr-obri-sessions.container';
// Number of new PSUs
import { GSUNbrOfPSUsContainerComponent } from 'analytics/src/app/components/gsu/gsu-nbr-of-psus.container';
// Number of new TPPs
import { GSUNbrOfTPPsContainerComponent } from 'analytics/src/app/components/gsu/gsu-nbr-of-tpps.container';
// Number of endpoint calls by Open Banking API versions
import { GSUOBVersionsContainerComponent } from 'analytics/src/app/components/gsu/gsu-ob-versions.container';
// Number of endpoint calls by Open Banking API types
import { GSUOBApiTypeContainerComponent } from 'analytics/src/app/components/gsu/gsu-ob-type.container';
// Response times performance to any endpoints
import { GSUAPIResponseTimesContainerComponent } from 'analytics/src/app/components/gsu/gsu-api-response-time.container';

const declarations = [
  GSUAPICallsPerWeekContainerComponent,
  GSUAPICallsContainerComponent,
  GSUNbrCallByStatusContainerComponent,
  GSUNbrOBRISessionsContainerComponent,
  GSUNbrOfPSUsContainerComponent,
  GSUNbrOfTPPsContainerComponent,
  GSUOBVersionsContainerComponent,
  GSUOBApiTypeContainerComponent,
  GSUAPIResponseTimesContainerComponent
];

@NgModule({
  imports: [
    CommonModule,
    ForgerockChartModule,
    MatProgressBarModule,
    ForgerockAlertModule,
    ForgerockSplitFlapModule,
    TranslateModule
  ],
  declarations,
  exports: declarations,
  providers: [MetricsService],
  entryComponents: declarations
})
export class GSUWidgetsModule {}
