import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';

import { MetricsService } from '../../services/metrics.service';
import { AnalyticsDashboardToolbarComponent } from './dashboard-toolbar.component';
import { AnalyticsDashboardToolbarContainer } from './dashboard-toolbar.container';

@NgModule({
  imports: [CommonModule, MatToolbarModule, MatIconModule, MatButtonModule, MatProgressSpinnerModule, FlexLayoutModule],
  declarations: [AnalyticsDashboardToolbarComponent, AnalyticsDashboardToolbarContainer],
  exports: [AnalyticsDashboardToolbarComponent, AnalyticsDashboardToolbarContainer],
  providers: [MetricsService]
})
export class AnalyticsDashboardToolbarModule {}
