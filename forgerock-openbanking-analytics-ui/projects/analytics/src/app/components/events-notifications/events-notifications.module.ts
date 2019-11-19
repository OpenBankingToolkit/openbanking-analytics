import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslateModule } from '@ngx-translate/core';

import { ForgerockChartModule } from 'forgerock/src/app/components/forgerock-chart/forgerock-chart.module';
import { ForgerockSplitFlapModule } from 'forgerock/src/app/components/forgerock-splitflap/forgerock-splitflap.module';
import { ForgerockAlertModule } from 'forgerock/src/app/components/forgerock-alert/forgerock-alert.module';

import { MetricsService } from '../../services/metrics.service';
import { EventsNotificationsFlowContainerComponent } from './events-notifications-flow.container';
import { EventsNotificationsTableModule } from './events-notifications-table/events-notifications-table.module';
import { EventsNotificationsCallbacksContainerComponent } from './events-notifications-callbacks.container';
import { EventsOBVersionsContainerComponent } from 'analytics/src/app/components/events-notifications/events-ob-versions.container';

const declarations = [
  EventsNotificationsFlowContainerComponent,
  EventsNotificationsCallbacksContainerComponent,
  EventsOBVersionsContainerComponent
];

@NgModule({
  imports: [
    CommonModule,
    ForgerockChartModule,
    MatProgressBarModule,
    ForgerockSplitFlapModule,
    EventsNotificationsTableModule,
    ForgerockAlertModule,
    TranslateModule
  ],
  declarations,
  entryComponents: declarations,
  exports: [...declarations, EventsNotificationsTableModule],
  providers: [MetricsService]
})
export class EventsNotificationsWidgetsModule {}
