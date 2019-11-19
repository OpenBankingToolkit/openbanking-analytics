import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';

import { IConsentTypeEndpointParams, IState } from 'analytics/src/models';
import { MetricsService } from '../../services/metrics.service';
import { AbstractWidgetChartComponent } from '../abstracts/widget.chart';
import colors from 'analytics/src/utils/colors';
import { IChartsEndpointParams, IDoughnutResponse } from 'analytics/src/models/metrics';
import { TranslateService } from '@ngx-translate/core';

const selector = 'app-accounts-consents-type-chart';

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
export class AccountsConsentTypeContainerComponent extends AbstractWidgetChartComponent implements OnInit {
  public id = selector;

  constructor(
    protected store: Store<IState>,
    protected metricsService: MetricsService,
    protected translateService: TranslateService
  ) {
    super(store, metricsService, translateService);
  }

  getService(params: IChartsEndpointParams) {
    const consentTypeEndpointParams: IConsentTypeEndpointParams = {
      fromDate: params.fromDate,
      toDate: params.toDate,
      obGroupName: 'AISP'
    };
    return this.metricsService.getConsentType(consentTypeEndpointParams);
  }

  responseTransform(response: IDoughnutResponse) {
    const labels = response.data.map(ob => this.translateLegends(ob.label));
    const data = response.data.map(ob => ob.value);
    const total = response.data.reduce((acc, current) => acc + parseFloat(current.value), 0);

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
