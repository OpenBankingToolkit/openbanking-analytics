import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslateModule } from '@ngx-translate/core';

import { ForgerockConfigModule } from 'forgerock/src/app/services/forgerock-config/forgerock-config.module';
import { ForgerockAlertModule } from 'forgerock/src/app/components/forgerock-alert/forgerock-alert.module';

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
