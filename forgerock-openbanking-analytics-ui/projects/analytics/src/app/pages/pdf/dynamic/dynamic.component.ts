import { Component, OnInit, ChangeDetectionStrategy, Input } from '@angular/core';
import { Chart } from 'chart.js';

import { IPdfReportConfig } from 'analytics/src/models/pdf';

@Component({
  selector: 'app-pdf-dynamic',
  templateUrl: './dynamic.component.html',
  styles: [
    `
      :host {
        display: block;
      }
    `
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AnalyticsPdfDynamicComponent implements OnInit {
  @Input() pdf: IPdfReportConfig;
  @Input() from: string;
  @Input() to: string;
  @Input() error: string;

  constructor() {
    Chart.defaults.global.animation.duration = 0;
    Chart.defaults.global.responsiveAnimationDuration = 0;
  }

  ngOnInit() {}
}
