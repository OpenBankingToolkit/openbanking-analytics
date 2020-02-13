import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatSortModule } from '@angular/material/sort';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { TranslateModule } from '@ngx-translate/core';

import { AnaltyticsWidgetTableComponent } from './widget-table.component';
import { AnalyticsTablePipesModule } from '../../pipes/table.pipes.module';
import { ForgerockAlertModule } from '@forgerock/openbanking-ngx-common/components/forgerock-alert';

const declarations = [AnaltyticsWidgetTableComponent];

@NgModule({
  imports: [
    CommonModule,
    MatTableModule,
    MatSortModule,
    MatProgressBarModule,
    FormsModule,
    TranslateModule,
    AnalyticsTablePipesModule,
    ForgerockAlertModule
  ],
  declarations,
  entryComponents: declarations,
  exports: declarations
})
export class AnaltyticsWidgetTableModule {}
