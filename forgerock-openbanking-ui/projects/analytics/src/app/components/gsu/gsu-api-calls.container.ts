import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';

import { IState } from 'analytics/src/models';
import { MetricsService } from '../../services/metrics.service';
import { AbstractWidgetChartComponent } from '../abstracts/widget.chart';
import { IChartsEndpointParams, ILineChartResponse } from 'analytics/src/models/metrics';
import colors from 'analytics/src/utils/colors';
import { TranslateService } from '@ngx-translate/core';

const selector = 'app-gsu-api-calls-chart';

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
export class GSUAPICallsContainerComponent extends AbstractWidgetChartComponent implements OnInit {
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
      aggregations: ['BY_DATE', 'BY_AGGREGATION_METHODS'],
      from,
      to
    });
  }

  responseTransform(response: ILineChartResponse) {
    return {
      type: 'line',
      data: {
        labels: response.definition.BY_DATE,

        datasets: response.lines.map((line, index) => ({
          label: this.translateLegends(line.name),
          borderColor: colors[index],
          data: line.dataset,
          fill: false,

          yAxisID: line.yAxisID
        }))
      },

      options: {
        legend: {
          display: true
        },
        tooltips: {
          mode: 'index',
          intersect: false
        },
        hover: {
          mode: 'nearest',
          intersect: true
        },
        scales: {
          xAxes: [
            {
              display: true,
              scaleLabel: {
                display: true,
                labelString: 'Time'
              }
            }
          ],
          yAxes: [
            {
              display: true,
              scaleLabel: {
                display: true,
                labelString: 'Number of requests'
              },
              position: 'left',
              id: 'NB_REQUESTS'
            },
            {
              display: true,
              scaleLabel: {
                display: true,
                labelString: 'Response time in ms'
              },
              position: 'right',
              id: 'RESPONSE_TIME_MS'
            }
          ]
        }
      }
    };
  }
}
