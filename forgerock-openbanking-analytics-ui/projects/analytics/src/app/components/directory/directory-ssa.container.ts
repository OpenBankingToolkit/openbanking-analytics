import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';

import { IChartsEndpointParams, ICounterResponse, IDirectoryCountersParams, IState } from 'analytics/src/models';
import { MetricsService } from '../../services/metrics.service';
import { AbstractWidgetChartComponent } from '../abstracts/widget.chart';
import { TranslateService } from '@ngx-translate/core';

const selector = 'app-directory-ssa-chart';

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
export class DirectorySSAContainerComponent extends AbstractWidgetChartComponent implements OnInit {
  public id = selector;

  constructor(
    protected store: Store<IState>,
    protected metricsService: MetricsService,
    protected translateService: TranslateService
  ) {
    super(store, metricsService, translateService);
  }

  getService(params: IChartsEndpointParams) {
    const directoryCountersParams: IDirectoryCountersParams = {
      fromDate: params.fromDate,
      toDate: params.toDate,
      type: 'SSA_GENERATED'
    };

    return this.metricsService.getDirectoriesCounters(directoryCountersParams);
  }

  responseTransform(response: ICounterResponse) {
    return {
      value: response.counter
    };
  }
}
