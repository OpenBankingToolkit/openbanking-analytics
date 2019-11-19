import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';

import { IState } from 'analytics/src/models';
import { MetricsService } from '../../services/metrics.service';
import { AbstractWidgetChartComponent } from '../abstracts/widget.chart';
import { IChartsEndpointParams, IDoughnutResponse } from 'analytics/src/models/metrics';
import colors from 'analytics/src/utils/colors';
import { TranslateService } from '@ngx-translate/core';

const selector = 'app-tpps-role-chart';

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
export class TppsRoleContainerComponent extends AbstractWidgetChartComponent implements OnInit {
  public id = selector;

  constructor(
    protected store: Store<IState>,
    protected metricsService: MetricsService,
    protected translateService: TranslateService
  ) {
    super(store, metricsService, translateService);
  }

  getService(params: IChartsEndpointParams) {
    return this.metricsService.getTppsTypes(params);
  }

  responseTransform(response: IDoughnutResponse) {
    const labels = response.data.map(ob => this.translateLegends(ob.label));
    const data = response.data.map(ob => ob.value);
    const backgroundColors = colors.map(ob => ob + '7d');

    return {
      type: 'polarArea',
      data: {
        labels,
        datasets: [
          {
            label: 'TPPs by type',
            data,
            backgroundColor: backgroundColors,
            borderWidth: 1
          }
        ]
      },
      options: {
        scale: {
          ticks: {
            beginAtZero: true
          },
          reverse: false
        }
      }
    };
  }
}
