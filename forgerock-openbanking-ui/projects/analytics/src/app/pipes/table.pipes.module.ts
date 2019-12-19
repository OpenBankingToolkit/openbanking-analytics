import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AnalyticsTableTimeFormatPipe } from './analytics.format-table-time.pipe';
import { AnalyticsTableTimePerKbFormatPipe } from './analytics.format-table-time-per-kb.pipe';

@NgModule({
  imports: [CommonModule],
  declarations: [AnalyticsTableTimeFormatPipe, AnalyticsTableTimePerKbFormatPipe],
  exports: [AnalyticsTableTimeFormatPipe, AnalyticsTableTimePerKbFormatPipe],
  providers: [AnalyticsTableTimeFormatPipe, AnalyticsTableTimePerKbFormatPipe]
})
export class AnalyticsTablePipesModule {}
