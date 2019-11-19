import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslateModule } from '@ngx-translate/core';

import { SettingsRoutingModule } from './settings-routing.module';
import { SettingsComponent } from './settings.component';
import { AnalyticsDashboardToolbarModule } from 'analytics/src/app/components/dashboard-toolbar/dashboard-toolbar.module';
import { AnalyticsWidgetCardModule } from '../../components/widget-card/widget-card.module';

@NgModule({
  declarations: [SettingsComponent],
  imports: [
    CommonModule,
    SettingsRoutingModule,
    FlexLayoutModule,
    TranslateModule,
    AnalyticsWidgetCardModule,
    AnalyticsDashboardToolbarModule
  ]
})
export class SettingsModule {}
