import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { TranslateModule } from '@ngx-translate/core';

import { MetricsService } from '../../../services/metrics.service';
import { AccountsTableComponent } from './accounts-table.component';
import { AnaltyticsWidgetTableModule } from '../../widget-table/widget-table.module';

const declarations = [AccountsTableComponent];

@NgModule({
  imports: [
    CommonModule,
    MatProgressBarModule,
    MatTableModule,
    MatPaginatorModule,
    TranslateModule,
    AnaltyticsWidgetTableModule
  ],
  declarations,
  entryComponents: declarations,
  exports: declarations,
  providers: [MetricsService]
})
export class AccountsTableModule {}
