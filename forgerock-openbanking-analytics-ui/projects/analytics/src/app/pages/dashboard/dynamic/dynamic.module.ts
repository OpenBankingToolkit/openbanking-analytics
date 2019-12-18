import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslateModule } from '@ngx-translate/core';

import { ForgerockConfigModule } from '@forgerock/openbanking-ngx-common/services/forgerock-config';
import { ForgerockAlertModule } from '@forgerock/openbanking-ngx-common/components/forgerock-alert';

import { AnalyticsDashboardDynamicComponent } from './dynamic.component';
import { AnalyticsWidgetCardModule } from 'analytics/src/app/components/widget-card/widget-card.module';
import { AnalyticsDashboardToolbarModule } from 'analytics/src/app/components/dashboard-toolbar/dashboard-toolbar.module';

@NgModule({
  declarations: [AnalyticsDashboardDynamicComponent],
  imports: [
    CommonModule,
    FlexLayoutModule,
    TranslateModule,
    MatCardModule,
    ForgerockConfigModule,
    ForgerockAlertModule,
    AnalyticsWidgetCardModule,
    AnalyticsDashboardToolbarModule
  ]
})
export class AnalyticsDashboardDynamicModule {}
