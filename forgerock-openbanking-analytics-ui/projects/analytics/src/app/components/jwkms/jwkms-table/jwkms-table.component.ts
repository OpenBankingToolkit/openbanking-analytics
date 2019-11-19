import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';

import { IState, ITableServiceParams, ITableFilterList, ITableSortList } from 'analytics/src/models';
import { MetricsService } from 'analytics/src/app/services/metrics.service';
import { AbstractWidgetTableComponent } from 'analytics/src/app/components/abstracts/widget.table';
import { TranslateService } from '@ngx-translate/core';

const selector = 'app-jwkms-table';

@Component({
  selector,
  template: `
    <app-widget-table
      [data]="pageData$ | async"
      [displayedColumns]="displayedColumns"
      [filters]="filters$ | async"
      [sorts]="sorts$ | async"
      [isLoading]="isLoading$ | async"
      [error]="error$ | async"
      (filterChange)="filterChange($event)"
      (sortChange)="sortChange($event)"
    ></app-widget-table>
    <mat-paginator
      [length]="length$ | async"
      [pageSize]="size$ | async"
      (page)="pageChange($event)"
      [pageSizeOptions]="[5, 10, 25, 100]"
    >
    </mat-paginator>
  `,
  styleUrls: ['./jwkms-table.component.scss']
})
export class JWKMSTableComponent extends AbstractWidgetTableComponent implements OnInit {
  public id = selector;
  displayedColumns: string[] = [
    'endpoint',
    'count',

    'response-time-percentile-95',
    'response-time-percentile-85',
    'response-time-median',
    'response-time-average'
  ];

  initialSorts: ITableSortList = [
    {
      field: 'count',
      direction: 'DESC'
    }
  ];

  initialFilters: ITableFilterList = [
    {
      field: 'application',
      regex: 'jwkms'
    }
  ];

  constructor(
    protected store: Store<IState>,
    protected metricsService: MetricsService,
    protected translateService: TranslateService
  ) {
    super(store, metricsService, translateService);
  }

  getService({ from, to, page, size, sorts, filters }: ITableServiceParams) {
    return this.metricsService.getTppsUsageAggregation({
      from,
      to,
      page,
      size,
      sort: sorts,
      fields: ['endpoint', 'method'],
      filters
    });
  }
}
