import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

import { AnalyticsWidgetCardComponent } from './widget-card.component';
import { PdfWidgetModule } from '../widget/widget.module';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';

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
