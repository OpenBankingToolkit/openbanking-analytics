import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import { TranslateModule } from '@ngx-translate/core';

import { MetricsService } from '../../../services/metrics.service';
import { TppsTableComponent } from './tpps-table.component';
import { AnaltyticsWidgetTableModule } from '../../widget-table/widget-table.module';

const declarations = [TppsTableComponent];

@NgModule({
  imports: [
    CommonModule,
    MatProgressBarModule,
    MatPaginatorModule,
    TranslateModule,
    AnaltyticsWidgetTableModule,
    MatMenuModule,
    MatIconModule
  ],
  declarations,
  entryComponents: declarations,
  exports: [...declarations],
  providers: [MetricsService]
})
export class TppsTableModule {}
