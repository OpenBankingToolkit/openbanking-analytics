import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslateModule } from '@ngx-translate/core';

import { ForgerockConfigModule } from 'forgerock/src/app/services/forgerock-config/forgerock-config.module';
import { ForgerockAlertModule } from 'forgerock/src/app/components/forgerock-alert/forgerock-alert.module';

import { AnalyticsPdfDynamicComponent } from './dynamic.component';
import { PdfPageModule } from '../page/page.module';
import { PdfCoverModule } from '../cover/cover.module';
import { AnalyticsWidgetCardModule } from 'analytics/src/app/components/widget-card/widget-card.module';
import { AnalyticsPdfDynamicContainer } from './dynamic.container';

@NgModule({
  declarations: [AnalyticsPdfDynamicComponent, AnalyticsPdfDynamicContainer],
  exports: [AnalyticsPdfDynamicComponent],
  imports: [
    CommonModule,
    FlexLayoutModule,
    TranslateModule,
    ForgerockConfigModule,
    MatCardModule,
    PdfPageModule,
    PdfCoverModule,
    AnalyticsWidgetCardModule,
    ForgerockAlertModule
  ]
})
export class AnalyticsPdfDynamicModule {}
