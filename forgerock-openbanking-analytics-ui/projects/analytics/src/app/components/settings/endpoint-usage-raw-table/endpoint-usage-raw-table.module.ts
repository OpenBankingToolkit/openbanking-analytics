import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatIconModule, MatMenuModule } from '@angular/material';
import { TranslateModule } from '@ngx-translate/core';

import { MetricsService } from '../../../services/metrics.service';
import { AnaltyticsWidgetTableModule } from '../../widget-table/widget-table.module';
import { EndpointUsageRawTableComponent } from 'analytics/src/app/components/settings/endpoint-usage-raw-table/endpoint-usage-raw-table.component';
import { ForgerockConfirmDialogModule } from 'forgerock/src/app/components/forgerock-confirm-dialog/forgerock-confirm-dialog.module';

const declarations = [EndpointUsageRawTableComponent];

@NgModule({
  imports: [
    CommonModule,
    MatProgressBarModule,
    MatPaginatorModule,
    TranslateModule,
    AnaltyticsWidgetTableModule,
    MatMenuModule,
    MatIconModule,
    ForgerockConfirmDialogModule
  ],
  declarations,
  entryComponents: declarations,
  exports: [...declarations],
  providers: [MetricsService]
})
export class EndpointUsageRawTableModule {}
