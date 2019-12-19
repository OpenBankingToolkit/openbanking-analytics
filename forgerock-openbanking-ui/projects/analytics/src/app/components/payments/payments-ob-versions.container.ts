import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';

import { ILineChartResponse, IState } from 'analytics/src/models';
import { MetricsService } from '../../services/metrics.service';
import { AbstractWidgetChartComponent } from '../abstracts/widget.chart';
import { IChartsEndpointParams } from 'analytics/src/models/metrics';
import { convertLineChartReponseToDoughnut } from 'analytics/src/utils/chart';
import { TranslateService } from '@ngx-translate/core';

const selector = 'app-payments-ob-versions-chart';

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
export class PaymentsOBVersionsContainerComponent extends AbstractWidgetChartComponent implements OnInit {
  public id = selector;

  constructor(
    protected store: Store<IState>,
    protected metricsService: MetricsService,
    protected translateService: TranslateService
  ) {
    super(store, metricsService, translateService);
  }

  getService({ fromDate: from, toDate: to }: IChartsEndpointParams) {
    return this.metricsService.readStats({
      aggregations: ['BY_OB_VERSIONS'],
      from,
      to,
      filtering: {
        obGroupNames: ['PISP']
      }
    });
  }

  responseTransform(response: ILineChartResponse) {
    return convertLineChartReponseToDoughnut(response, 'BY_OB_VERSIONS');
  }
}
