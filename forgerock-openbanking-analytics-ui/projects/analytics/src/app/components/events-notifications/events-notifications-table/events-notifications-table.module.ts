import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslateModule } from '@ngx-translate/core';

import { MetricsService } from '../../../services/metrics.service';
import { EventsNotificationsTableComponent } from './events-notifications-table.component';
import { AnaltyticsWidgetTableModule } from '../../widget-table/widget-table.module';

const declarations = [EventsNotificationsTableComponent];

@NgModule({
  imports: [CommonModule, MatProgressBarModule, MatPaginatorModule, TranslateModule, AnaltyticsWidgetTableModule],
  declarations,
  entryComponents: declarations,
  exports: [...declarations],
  providers: [MetricsService]
})
export class EventsNotificationsTableModule {}
