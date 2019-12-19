import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';

import { IChartsEndpointParams, IState } from 'analytics/src/models';
import { MetricsService } from '../../services/metrics.service';
import { AbstractWidgetChartComponent } from '../abstracts/widget.chart';
import colors from 'analytics/src/utils/colors';
import { TranslateService } from '@ngx-translate/core';

const selector = 'app-tpps-number-chart';

@Component({
  selector,
  template: `
    <mat-progress-bar *ngIf="isLoading$ | async" mode="indeterminate"></mat-progress-bar>
    <forgerock-alert
      *ngIf="error$ | async as error"
      color="warn"
      [translate]="'ERROR'"
      [translateParams]="{ message: error }"
    ></forgerock-alert>
    <forgerock-chart *ngIf="config$ | async as config" [config]="config"></forgerock-chart>
  `,
  styles: [
    `
      :host {
        display: block;
      }
    `
  ]
})
export class TppsNumberContainerComponent extends AbstractWidgetChartComponent implements OnInit {
  public id = selector;

  constructor(
    protected store: Store<IState>,
    protected metricsService: MetricsService,
    protected translateService: TranslateService
  ) {
    super(store, metricsService, translateService);
  }

  getService(params: IChartsEndpointParams) {
    return this.metricsService.getTppsActivities(params);
  }

  responseTransform(data) {
    return {
      type: 'doughnut',
      data: {
        labels: ['Active', 'Inactive'],
        datasets: [
          {
            label: 'TPPs activities',
            data: [data.dataset.ACTIVE, data.dataset.INACTIVE],
            backgroundColor: colors,
            borderWidth: 1
          }
        ]
      }
    };
  }
}
