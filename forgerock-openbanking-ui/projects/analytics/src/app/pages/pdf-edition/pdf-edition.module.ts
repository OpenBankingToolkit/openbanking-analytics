import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatExpansionModule } from '@angular/material/expansion';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslateModule } from '@ngx-translate/core';
import { DragDropModule } from '@angular/cdk/drag-drop';

import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { ForgerockConfigModule } from '@forgerock/openbanking-ngx-common/services/forgerock-config';
import { ForgerockAlertModule } from '@forgerock/openbanking-ngx-common/components/forgerock-alert';

import { AnalyticsPdfEditionRoutingModule } from './pdf-edition.routing.module';
import { AnalyticsPdfEditionComponent } from './pdf-edition.component';
import { AnalyticsWidgetCardModule } from 'analytics/src/app/components/widget-card/widget-card.module';

import { PdfCoverModule } from '../pdf/cover/cover.module';
import { PdfPageModule } from '../pdf/page/page.module';
import { AnalyticsPdfDynamicModule } from '../pdf/dynamic/dynamic.module';
import { ScrollingModule } from '@angular/cdk/scrolling';
import { AnalyticsPdfEditionContainer } from './pdf-edition.container';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    FlexLayoutModule,
    TranslateModule,
    ForgerockAlertModule,
    ForgerockConfigModule,
    MatCardModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatExpansionModule,
    MatInputModule,
    MatCheckboxModule,
    MatProgressSpinnerModule,
    MatToolbarModule,
    DragDropModule,
    ScrollingModule,
    PdfPageModule,
    PdfCoverModule,
    AnalyticsPdfEditionRoutingModule,
    AnalyticsWidgetCardModule,
    AnalyticsPdfDynamicModule,
    ForgerockAlertModule
  ],
  declarations: [AnalyticsPdfEditionComponent, AnalyticsPdfEditionContainer]
})
export class AnalyticsPdfEditionModule {}
