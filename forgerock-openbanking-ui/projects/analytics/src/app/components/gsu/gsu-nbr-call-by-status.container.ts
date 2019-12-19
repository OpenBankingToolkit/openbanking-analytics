import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import _get from 'lodash-es/get';

import { IState } from 'analytics/src/models';
import { MetricsService } from '../../services/metrics.service';
import { AbstractWidgetChartComponent } from '../abstracts/widget.chart';
import colors from 'analytics/src/utils/colors';
import { IChartsEndpointParams, IDoughnutResponse } from 'analytics/src/models/metrics';
import { TranslateService } from '@ngx-translate/core';

const selector = 'app-gsu-nbr-call-by-status-chart';

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
export class GSUNbrCallByStatusContainerComponent extends AbstractWidgetChartComponent implements OnInit {
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
      aggregations: ['BY_RESPONSE_STATUS'],
      from,
      to
    });
  }

  responseTransform(response: IDoughnutResponse) {
    const labels = _get(response, 'definition.BY_RESPONSE_STATUS', []);
    const data = _get(response, 'lines[0].dataset', []);
    const total = data.reduce((acc, current) => acc + parseFloat(current), 0);

    return {
      type: 'doughnut',
      data: {
        labels,
        datasets: [
          {
            label: 'TPPs by type',
            data,
            backgroundColor: colors,
            borderWidth: 1
          }
        ]
      },
      options: {
        // String - Template string for single tooltips
        tooltips: {
          /**
           callbacks: {

            label: function(tooltipItem, data) {
              let label = data.datasets[tooltipItem.datasetIndex].label || '';

              if (label) {
                label += ': ';
              }
              console.log(tooltipItem);
              console.log(data.datasets[tooltipItem.datasetIndex].data[tooltipItem.index]);
              const value = data.datasets[tooltipItem.datasetIndex].data[tooltipItem.index];
              const percentage = ((value * 100) / total).toFixed(1);
              label += percentage + ' %';

              label += ' - ' + data.datasets[tooltipItem.datasetIndex].data[tooltipItem.index];
              return label;
            }
          } **/
        },
        elements: {
          center: {
            text: total,
            fontSizeFactor: 0.8,
            // sidePadding: 20, // Defualt is 20 (as a percentage)
            // color: '#000', // Default is #000000
            // fontStyle: 'Arial', // Default is Arial
            yShift: 10
            // xShift: 10
          }
        }
      }
    };
  }
}
