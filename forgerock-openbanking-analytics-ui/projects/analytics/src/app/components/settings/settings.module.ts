import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslateModule } from '@ngx-translate/core';

import { ForgerockChartModule } from 'ob-ui-libs/components/forgerock-chart';
import { ForgerockSplitFlapModule } from 'ob-ui-libs/components/forgerock-splitflap';
import { ForgerockAlertModule } from 'ob-ui-libs/components/forgerock-alert';

import { MetricsService } from '../../services/metrics.service';
import { EndpointUsageRawTableModule } from 'analytics/src/app/components/settings/endpoint-usage-raw-table/endpoint-usage-raw-table.module';

const declarations = [];

@NgModule({
  imports: [
    CommonModule,
    ForgerockChartModule,
    ForgerockSplitFlapModule,
    MatProgressBarModule,
    ForgerockAlertModule,
    TranslateModule,
    EndpointUsageRawTableModule
  ],
  declarations,
  entryComponents: declarations,
  exports: declarations,
  providers: [MetricsService, EndpointUsageRawTableModule]
})
export class SettingsWidgetsModule {}
