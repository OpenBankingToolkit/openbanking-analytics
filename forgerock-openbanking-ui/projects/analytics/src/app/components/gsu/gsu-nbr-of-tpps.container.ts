import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';

import { IChartsEndpointParams, ICounterResponse, IState } from 'analytics/src/models';
import { MetricsService } from '../../services/metrics.service';
import { AbstractWidgetChartComponent } from '../abstracts/widget.chart';
import { TranslateService } from '@ngx-translate/core';

const selector = 'app-gsu-nbr-of-tpps-chart';

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
    <forgerock-splitflap
      *ngIf="config$ | async as config"
      [autoSize]="true"
      deck="0123456789"
      time="0.1"
      [value]="config.value"
    ></forgerock-splitflap>
  `,
  styles: [
    `
      :host {
        display: block;
      }
    `
  ]
})
export class GSUNbrOfTPPsContainerComponent extends AbstractWidgetChartComponent implements OnInit {
  public id = selector;

  constructor(
    protected store: Store<IState>,
    protected metricsService: MetricsService,
    protected translateService: TranslateService
  ) {
    super(store, metricsService, translateService);
  }

  getService(params: IChartsEndpointParams) {
    return this.metricsService.getNbrOfTPPs(params);
  }

  responseTransform(response: ICounterResponse) {
    return {
      value: response.counter
    };
  }
}
