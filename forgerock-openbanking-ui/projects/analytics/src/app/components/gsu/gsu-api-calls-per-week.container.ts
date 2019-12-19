import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';

import { IState } from 'analytics/src/models';
import { MetricsService } from '../../services/metrics.service';
import { AbstractWidgetChartComponent } from '../abstracts/widget.chart';
import { IChartsEndpointParams, ILineChartResponse } from 'analytics/src/models/metrics';
import colors from 'analytics/src/utils/colors';
import { TranslateService } from '@ngx-translate/core';

const selector = 'app-gsu-api-calls-per-week-chart';

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
export class GSUAPICallsPerWeekContainerComponent extends AbstractWidgetChartComponent implements OnInit {
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
      aggregations: ['BY_WEEK_DAY'],
      from,
      to
    });
  }

  responseTransform(response: ILineChartResponse) {
    return {
      type: 'line',
      data: {
        labels: response.definition.BY_WEEK_DAY,

        datasets: response.lines.map((line, index) => ({
          label: this.translateLegends(line.name),
          borderColor: colors[index],
          data: line.dataset,
          fill: false
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
                labelString: 'Week days'
              }
            }
          ],
          yAxes: [
            {
              display: true,
              scaleLabel: {
                display: true,
                labelString: 'Number of calls'
              }
            }
          ]
        }
      }
    };
  }
}
