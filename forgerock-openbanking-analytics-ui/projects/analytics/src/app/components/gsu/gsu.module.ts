import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslateModule } from '@ngx-translate/core';

import { ForgerockChartModule } from 'ob-ui-libs/components/forgerock-chart';
import { ForgerockAlertModule } from 'ob-ui-libs/components/forgerock-alert';
import { ForgerockSplitFlapModule } from 'ob-ui-libs/components/forgerock-splitflap';

import { MetricsService } from '../../services/metrics.service';
import { GSUAPICallsPerWeekContainerComponent } from 'analytics/src/app/components/gsu/gsu-api-calls-per-week.container';
import { GSUAPICallsContainerComponent } from 'analytics/src/app/components/gsu/gsu-api-calls.container';
import { GSUNbrCallByStatusContainerComponent } from 'analytics/src/app/components/gsu/gsu-nbr-call-by-status.container';
import { GSUNbrOBRISessionsContainerComponent } from 'analytics/src/app/components/gsu/gsu-nbr-obri-sessions.container';
import { GSUNbrOfPSUsContainerComponent } from 'analytics/src/app/components/gsu/gsu-nbr-of-psus.container';
import { GSUNbrOfTPPsContainerComponent } from 'analytics/src/app/components/gsu/gsu-nbr-of-tpps.container';
import { GSUOBVersionsContainerComponent } from 'analytics/src/app/components/gsu/gsu-ob-versions.container';
import { GSUOBApiTypeContainerComponent } from 'analytics/src/app/components/gsu/gsu-ob-type.container';
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
