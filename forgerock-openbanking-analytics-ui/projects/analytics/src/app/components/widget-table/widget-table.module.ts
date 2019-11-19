import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatSortModule, MatProgressBarModule } from '@angular/material';
import { MatTableModule } from '@angular/material/table';
import { TranslateModule } from '@ngx-translate/core';

import { AnaltyticsWidgetTableComponent } from './widget-table.component';
import { AnalyticsTablePipesModule } from '../../pipes/table.pipes.module';
import { ForgerockAlertModule } from 'forgerock/src/app/components/forgerock-alert/forgerock-alert.module';

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
