import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslateModule } from '@ngx-translate/core';

import { ForgerockChartModule } from '@forgerock/openbanking-ngx-common/components/forgerock-chart';

import { MetricsService } from '../../services/metrics.service';
import { TppsNumberContainerComponent } from './tpps.number.container';
import { TppsRoleContainerComponent } from './tpps.role.container';
import { TppsOriginContainerComponent } from './tpps.origin.container';
import { TppsRegistrationContainerComponent } from './tpps.registration.container';
import { TppsTableModule } from './tpps-table/tpps-table.module';
import { ForgerockAlertModule } from '@forgerock/openbanking-ngx-common/components/forgerock-alert';
import { TppsApiTableModule } from 'analytics/src/app/components/tpps/tpps-apis-table/tpps-api-table.module';

const declarations = [
  TppsNumberContainerComponent,
  TppsOriginContainerComponent,
  TppsRoleContainerComponent,
  TppsRegistrationContainerComponent
];

@NgModule({
  imports: [
    CommonModule,
    ForgerockChartModule,
    MatProgressBarModule,
    TppsTableModule,
    TppsApiTableModule,
    ForgerockAlertModule,
    TranslateModule
  ],
  declarations,
  exports: [...declarations, TppsTableModule, TppsApiTableModule],
  providers: [MetricsService],
  entryComponents: declarations
})
export class TppsWidgetsModule {}
