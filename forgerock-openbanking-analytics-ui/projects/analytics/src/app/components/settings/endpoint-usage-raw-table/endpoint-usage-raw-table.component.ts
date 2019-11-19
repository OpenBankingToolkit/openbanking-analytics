import { Component, Inject, OnInit, Renderer2 } from '@angular/core';
import { Store } from '@ngrx/store';

import { IState, ITableServiceParams } from 'analytics/src/models';
import { MetricsService } from 'analytics/src/app/services/metrics.service';
import { AbstractWidgetTableComponent } from 'analytics/src/app/components/abstracts/widget.table';
import { TranslateService } from '@ngx-translate/core';
import { DOCUMENT } from '@angular/common';
import { ForgerockConfirmDialogComponent } from 'forgerock/src/app/components/forgerock-confirm-dialog/forgerock-confirm-dialog.component';
import { MatDialog } from '@angular/material';

const selector = 'app-settings-endpoint-usage-raw-table';

@Component({
  selector,
  template: `
    <div class="table-options">
      <button mat-icon-button [matMenuTriggerFor]="menu" aria-label="endpoint-usage-table-options">
        <mat-icon>more_vert</mat-icon>
      </button>
      <mat-menu #menu="matMenu">
        <a mat-menu-item [href]="downloadCSV()" target="_blank">
          <mat-icon>cloud_download</mat-icon>
          Download as CSV
        </a>
        <button mat-menu-item (click)="deleteHistory()">
          <mat-icon>delete</mat-icon>
          <span>Delete history</span>
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
  styleUrls: ['./endpoint-usage-raw-table.component.scss']
})
export class EndpointUsageRawTableComponent extends AbstractWidgetTableComponent implements OnInit {
  public id = selector;
  displayedColumns: string[] = [
    'date',
    'identityId',
    'ip',
    'settings-endpoint',
    'endpointType',
    'obVersion',
    'responseStatus',
    'application',
    'settings-count'
  ];

  constructor(
    protected store: Store<IState>,
    protected metricsService: MetricsService,
    protected translateService: TranslateService,
    private renderer: Renderer2,
    public dialog: MatDialog,
    @Inject(DOCUMENT) private document: any
  ) {
    super(store, metricsService, translateService);
  }

  getService({ from, to, page, size }: ITableServiceParams) {
    return this.metricsService.getEndpointUsageRawHistory({
      fromDate: from,
      toDate: to,
      page,
      size
    });
  }

  downloadCSV() {
    return this.metricsService.getEndpointUsageRawHistoryCSVUrl(this.getDates());
  }

  downloadFile(data, type, fileName) {
    const anchor = this.renderer.createElement('a');
    this.renderer.setStyle(anchor, 'visibility', 'hidden');
    this.renderer.setAttribute(anchor, 'href', 'data:' + type + ';charset=utf-8,' + encodeURIComponent(data));
    this.renderer.setAttribute(anchor, 'target', '_blank');
    this.renderer.setAttribute(anchor, 'download', fileName);
    this.document.body.appendChild(anchor); // Required for Firefox
    anchor.click();
    anchor.remove();
  }

  deleteHistory() {
    const dialogRef = this.dialog.open(ForgerockConfirmDialogComponent, {
      data: {
        text: this.translateService.instant('SETTINGS.DELETE_ENDPOINT_USAGE_CONFIRM', this.getDates())
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.metricsService.deleteEndpointUsageRawHistory(this.getDates()).subscribe(() => {
          this.forceRefresh();
        });
      }
    });
  }
}
