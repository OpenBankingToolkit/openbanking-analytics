import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

import { AnalyticsWidgetCardComponent } from './widget-card.component';
import { PdfWidgetModule } from '../widget/widget.module';
import { MatDividerModule, MatExpansionModule, MatIconModule } from '@angular/material';

@NgModule({
  imports: [
    CommonModule,
    MatCardModule,
    MatDividerModule,
    MatExpansionModule,
    MatIconModule,
    PdfWidgetModule,
    TranslateModule
  ],
  declarations: [AnalyticsWidgetCardComponent],
  exports: [AnalyticsWidgetCardComponent]
})
export class AnalyticsWidgetCardModule {}
