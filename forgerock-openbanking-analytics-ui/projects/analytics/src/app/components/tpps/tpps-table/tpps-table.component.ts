import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';

import { IState, ITableServiceParams, ITableSortList, ITableFilterList } from 'analytics/src/models';
import { MetricsService } from 'analytics/src/app/services/metrics.service';
import { AbstractWidgetTableComponent } from 'analytics/src/app/components/abstracts/widget.table';
import { TranslateService } from '@ngx-translate/core';

const selector = 'app-tpps-table';

@Component({
  selector,
  template: `
    <div class="table-options">
      <button mat-icon-button [matMenuTriggerFor]="menu" aria-label="endpoint-usage-table-options">
        <mat-icon>more_vert</mat-icon>
      </button>
      <mat-menu #menu="matMenu">
        <button mat-menu-item>
          <mat-icon>cloud_download</mat-icon>
          <a class="downloadCSV" [href]="downloadCSV()" target="_blank">Download as CSV</a>
        </button>
      </mat-menu>
    </div>

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
      [pageIndex]="currentPage$ | async"
      (page)="pageChange($event)"
      [pageSizeOptions]="[5, 10, 25, 100]"
    >
    </mat-paginator>
  `,
  styleUrls: ['./tpps-table.component.scss']
})
export class TppsTableComponent extends AbstractWidgetTableComponent implements OnInit {
  public id = selector;
  displayedColumns: string[] = ['organisationName', 'name', 'oidcClientId', 'directoryId'];

  initialSorts: ITableSortList = [
    {
      field: 'count',
      direction: 'DESC'
    }
  ];

  initialFilters: ITableFilterList = [
    {
      field: 'userType',
      regex: 'OIDC_CLIENT'
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
    return this.metricsService.getTppsEntries({
      fromDate: from,
      toDate: to,
      page: page,
      size: size
    });
  }

  downloadCSV() {
    return this.metricsService.getTppEntriesCSVUrl(this.getDates());
  }
}
