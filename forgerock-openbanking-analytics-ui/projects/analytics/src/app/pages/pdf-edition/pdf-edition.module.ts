import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslateModule } from '@ngx-translate/core';
import {
  MatSelectModule,
  MatButtonModule,
  MatIconModule,
  MatExpansionModule,
  MatInputModule,
  MatCheckboxModule,
  MatProgressSpinnerModule,
  MatToolbarModule
} from '@angular/material';
import { DragDropModule } from '@angular/cdk/drag-drop';

import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { ForgerockConfigModule } from 'ob-ui-libs/services/forgerock-config';
import { ForgerockAlertModule } from 'ob-ui-libs/components/forgerock-alert';

import { AnalyticsPdfEditionRoutingModule } from './pdf-edition.routing.module';
import { AnalyticsPdfEditionComponent } from './pdf-edition.component';
import { AnalyticsWidgetCardModule } from 'analytics/src/app/components/widget-card/widget-card.module';

import { PdfCoverModule } from '../pdf/cover/cover.module';
import { PdfPageModule } from '../pdf/page/page.module';
import { AnalyticsPdfDynamicModule } from '../pdf/dynamic/dynamic.module';
import { ScrollDispatchModule } from '@angular/cdk/scrolling';
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
    ScrollDispatchModule,
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
