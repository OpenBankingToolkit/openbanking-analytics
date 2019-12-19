import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
  OnDestroy,
  OnChanges,
  SimpleChanges
} from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { ITableFilter, ITableSort, ITableFilterList, ITableSortList } from 'analytics/src/models';
import { Subscription, Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';

@Component({
  selector: 'app-widget-table',
  template: `
    <table mat-table [dataSource]="data" matSort [matSortDirection]="sortDirection" [matSortActive]="sortActive">
      <ng-container matColumnDef="endpoint">
        <th mat-header-cell *matHeaderCellDef mat-sort-header>
          {{ 'WIDGETS.TABLE.COLUMNS.endpoint' | translate }}
          <input
            [(ngModel)]="filtersModels.endpoint"
            (click)="$event.stopPropagation()"
            class="filter-input"
            (keyup)="filterInputChange('endpoint', $event)"
            placeholder="{{ 'WIDGETS.TABLE.COLUMNS.FILTER' | translate }}"
          />
        </th>
        <td mat-cell *matCellDef="let element">
          <span class="method {{ element.method }}">{{ element.method }}</span>
          <span class="endpoint">{{ element.endpoint }}</span>
        </td>
      </ng-container>
      <ng-container matColumnDef="settings-endpoint">
        <th mat-header-cell *matHeaderCellDef>
          {{ 'WIDGETS.TABLE.COLUMNS.endpoint' | translate }}
        </th>
        <td mat-cell *matCellDef="let element">
          <span class="method {{ element.method }}">{{ element.method }}</span>
          <span class="endpoint">{{ element.endpoint }}</span>
        </td>
      </ng-container>
      <ng-container matColumnDef="date">
        <th mat-header-cell *matHeaderCellDef>
          {{ 'WIDGETS.TABLE.COLUMNS.date' | translate }}
        </th>
        <td mat-cell *matCellDef="let element">
          {{ element.date }}
        </td>
      </ng-container>
      <ng-container matColumnDef="identityId">
        <th mat-header-cell *matHeaderCellDef>
          {{ 'WIDGETS.TABLE.COLUMNS.identityId' | translate }}
        </th>
        <td mat-cell *matCellDef="let element">
          {{ element.identityId }}
        </td>
      </ng-container>
      <ng-container matColumnDef="endpointType">
        <th mat-header-cell *matHeaderCellDef>
          {{ 'WIDGETS.TABLE.COLUMNS.endpointType' | translate }}
        </th>
        <td mat-cell *matCellDef="let element">
          {{ element.endpointType }}
        </td>
      </ng-container>
      <ng-container matColumnDef="obVersion">
        <th mat-header-cell *matHeaderCellDef>
          {{ 'WIDGETS.TABLE.COLUMNS.obVersion' | translate }}
        </th>
        <td mat-cell *matCellDef="let element">
          {{ element.obVersion }}
        </td>
      </ng-container>
      <ng-container matColumnDef="responseStatus">
        <th mat-header-cell *matHeaderCellDef>
          {{ 'WIDGETS.TABLE.COLUMNS.responseStatus' | translate }}
        </th>
        <td mat-cell *matCellDef="let element">
          {{ element.responseStatus }}
        </td>
      </ng-container>
      <ng-container matColumnDef="application">
        <th mat-header-cell *matHeaderCellDef>
          {{ 'WIDGETS.TABLE.COLUMNS.application' | translate }}
        </th>
        <td mat-cell *matCellDef="let element">
          {{ element.application }}
        </td>
      </ng-container>
      <ng-container matColumnDef="count">
        <th mat-header-cell *matHeaderCellDef mat-sort-header>{{ 'WIDGETS.TABLE.COLUMNS.count' | translate }}</th>
        <td mat-cell *matCellDef="let element">{{ element.count }}</td>
      </ng-container>
      <ng-container matColumnDef="settings-count">
        <th mat-header-cell *matHeaderCellDef>{{ 'WIDGETS.TABLE.COLUMNS.count' | translate }}</th>
        <td mat-cell *matCellDef="let element">{{ element.count }}</td>
      </ng-container>
      <ng-container matColumnDef="tppEntry.organisationName">
        <th mat-header-cell *matHeaderCellDef mat-sort-header>
          {{ 'WIDGETS.TABLE.COLUMNS.organisation' | translate }}
          <input
            [(ngModel)]="filtersModels['tppEntry.organisationName']"
            (click)="$event.stopPropagation()"
            class="filter-input"
            (keyup)="filterInputChange('tppEntry.organisationName', $event)"
            placeholder="{{ 'WIDGETS.TABLE.COLUMNS.FILTER' | translate }}"
          />
        </th>
        <td mat-cell *matCellDef="let element">
          {{ element.tppEntry?.organisationName }}
        </td>
      </ng-container>
      <ng-container matColumnDef="tppEntry.name">
        <th mat-header-cell *matHeaderCellDef mat-sort-header>
          {{ 'WIDGETS.TABLE.COLUMNS.tpp' | translate }}
          <input
            [(ngModel)]="filtersModels['tppEntry.name']"
            (click)="$event.stopPropagation()"
            class="filter-input"
            (keyup)="filterInputChange('tppEntry.name', $event)"
            placeholder="{{ 'WIDGETS.TABLE.COLUMNS.FILTER' | translate }}"
          />
        </th>
        <td mat-cell *matCellDef="let element">
          <ng-container *ngIf="element.tppEntry">
            <img class="tpp-logo-uri" *ngIf="element.tppEntry.logoUri" [src]="element.tppEntry.logoUri" />
            {{ element.tppEntry.name }}
          </ng-container>
        </td>
      </ng-container>

      <ng-container matColumnDef="response-time-percentile-95">
        <th mat-header-cell *matHeaderCellDef>{{ 'WIDGETS.TABLE.COLUMNS.response-time-percentile-95' | translate }}</th>
        <td mat-cell *matCellDef="let element">
          <ng-container *ngIf="element.endpointStatisticKPI">
            <span
              innerHTML="{{
                element.endpointStatisticKPI.valuesByAggregationMethods.BY_RESPONSE_TIME_PERCENTILE_95
                  | analyticsTableTimeFormat
              }}"
            ></span>
          </ng-container>
        </td>
      </ng-container>
      <ng-container matColumnDef="response-time-percentile-85">
        <th mat-header-cell *matHeaderCellDef>{{ 'WIDGETS.TABLE.COLUMNS.response-time-percentile-85' | translate }}</th>
        <td
          mat-cell
          *matCellDef="let element"
          innerHTML="{{
            element.endpointStatisticKPI.valuesByAggregationMethods.BY_RESPONSE_TIME_PERCENTILE_85
              | analyticsTableTimeFormat
          }}"
        ></td>
      </ng-container>
      <ng-container matColumnDef="response-time-median">
        <th mat-header-cell *matHeaderCellDef>{{ 'WIDGETS.TABLE.COLUMNS.response-time-median' | translate }}</th>
        <td
          mat-cell
          *matCellDef="let element"
          innerHTML="{{
            element.endpointStatisticKPI.valuesByAggregationMethods.BY_RESPONSE_TIME_MEDIAN | analyticsTableTimeFormat
          }}"
        ></td>
      </ng-container>
      <ng-container matColumnDef="response-time-average">
        <th mat-header-cell *matHeaderCellDef>{{ 'WIDGETS.TABLE.COLUMNS.response-time-average' | translate }}</th>
        <td
          mat-cell
          *matCellDef="let element"
          innerHTML="{{
            element.endpointStatisticKPI.valuesByAggregationMethods.BY_RESPONSE_TIME_AVERAGE | analyticsTableTimeFormat
          }}"
        ></td>
      </ng-container>

      <ng-container matColumnDef="response-time-by-mb-percentile-95">
        <th mat-header-cell *matHeaderCellDef>
          {{ 'WIDGETS.TABLE.COLUMNS.response-time-by-mb-percentile-95' | translate }}
        </th>
        <td
          mat-cell
          *matCellDef="let element"
          innerHTML="{{
            element.endpointStatisticKPI.valuesByAggregationMethods.BY_RESPONSE_TIME_BY_MB_PERCENTILE_95
              | analyticsTableTimePerKbFormat
          }}"
        ></td>
      </ng-container>
      <ng-container matColumnDef="response-time-by-mb-percentile-85">
        <th mat-header-cell *matHeaderCellDef>
          {{ 'WIDGETS.TABLE.COLUMNS.response-time-by-mb-percentile-85' | translate }}
        </th>
        <td
          mat-cell
          *matCellDef="let element"
          innerHTML=" {{
            element.endpointStatisticKPI.valuesByAggregationMethods.BY_RESPONSE_TIME_BY_MB_PERCENTILE_85
              | analyticsTableTimePerKbFormat
          }}"
        ></td>
      </ng-container>
      <ng-container matColumnDef="response-time-by-mb-median">
        <th mat-header-cell *matHeaderCellDef>{{ 'WIDGETS.TABLE.COLUMNS.response-time-by-mb-median' | translate }}</th>
        <td
          mat-cell
          *matCellDef="let element"
          innerHTML="{{
            element.endpointStatisticKPI.valuesByAggregationMethods.BY_RESPONSE_TIME_BY_MB_MEDIAN
              | analyticsTableTimePerKbFormat
          }}"
        ></td>
      </ng-container>
      <ng-container matColumnDef="response-time-by-mb-average">
        <th mat-header-cell *matHeaderCellDef>{{ 'WIDGETS.TABLE.COLUMNS.response-time-by-mb-average' | translate }}</th>
        <td
          mat-cell
          *matCellDef="let element"
          innerHTML="{{
            element.endpointStatisticKPI.valuesByAggregationMethods.BY_RESPONSE_TIME_BY_MB_AVERAGE
              | analyticsTableTimePerKbFormat
          }}"
        ></td>
      </ng-container>

      <ng-container matColumnDef="name">
        <th mat-header-cell *matHeaderCellDef>
          {{ 'WIDGETS.TABLE.COLUMNS.name' | translate }}
        </th>
        <td mat-cell *matCellDef="let element">
          <img class="tpp-logo-uri" *ngIf="element.logoUri" [src]="element.logoUri" />
          <ng-container *ngIf="element.name">
            {{ element.name }}
          </ng-container>
        </td>
      </ng-container>
      <ng-container matColumnDef="oidcClientId">
        <th mat-header-cell *matHeaderCellDef>
          {{ 'WIDGETS.TABLE.COLUMNS.oidcClientId' | translate }}
        </th>
        <td mat-cell *matCellDef="let element">
          <ng-container *ngIf="element.oidcClientId">
            {{ element.oidcClientId }}
          </ng-container>
        </td>
      </ng-container>
      <ng-container matColumnDef="organisationName">
        <th mat-header-cell *matHeaderCellDef>
          {{ 'WIDGETS.TABLE.COLUMNS.organisationName' | translate }}
        </th>
        <td mat-cell *matCellDef="let element">
          <ng-container *ngIf="element.organisationName">
            {{ element.organisationName }}
          </ng-container>
        </td>
      </ng-container>
      <ng-container matColumnDef="directoryId">
        <th mat-header-cell *matHeaderCellDef>
          {{ 'WIDGETS.TABLE.COLUMNS.directoryId' | translate }}
        </th>
        <td mat-cell *matCellDef="let element">
          <ng-container *ngIf="element.directoryId">
            {{ element.directoryId }}
          </ng-container>
        </td>
      </ng-container>
      <ng-container matColumnDef="ip">
        <th mat-header-cell *matHeaderCellDef>
          {{ 'WIDGETS.TABLE.COLUMNS.ip' | translate }}
        </th>
        <td mat-cell *matCellDef="let element">
          <ng-container *ngIf="element.geoIP">
            {{ element.geoIP.ipAddress }}
          </ng-container>
        </td>
      </ng-container>

      <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
      <tr mat-row *matRowDef="let row; columns: displayedColumns"></tr>
    </table>

    <forgerock-alert
      *ngIf="error"
      color="warn"
      [translate]="'ERROR'"
      [translateParams]="{ message: error }"
    ></forgerock-alert>
    <forgerock-alert *ngIf="(!data || !data.length) && !isLoading && !error" [translate]="'EMPTY'"></forgerock-alert>
    <mat-progress-bar *ngIf="isLoading" mode="indeterminate"></mat-progress-bar>
  `,
  styleUrls: ['./widget-table.component.scss']
})
export class AnaltyticsWidgetTableComponent implements OnInit, OnDestroy, OnChanges {
  @Input() displayedColumns: string[] = [];
  @Input() data: any[] = [];
  @Input() sorts: ITableSortList = [];
  @Input() filters: ITableFilterList = [];
  @Input() isLoading = false;
  @Input() error: string;
  @Output() filterChange = new EventEmitter<ITableFilter>();
  @Output() sortChange = new EventEmitter<ITableSort>();
  public sortDirection: 'asc' | 'desc' | '';
  public sortActive = '';
  public filtersModels: { [key: string]: any } = {};
  private sortSubscription: Subscription;
  private filterSubscription: Subscription;
  private filterSubject: Subject<ITableFilter> = new Subject();
  private latestFilterValue = '';

  @ViewChild(MatSort, { static: true }) matSort: MatSort;

  constructor() {}

  ngOnInit() {
    this.sortSubscription = this.matSort.sortChange.pipe(debounceTime(300)).subscribe(() => {
      this.sortChange.emit({
        field: this.matSort.active,
        // need to change the case to support the backend
        direction: this.matSort.direction === 'asc' ? 'ASC' : this.matSort.direction === 'desc' ? 'DESC' : ''
      });
    });
    this.filterSubscription = this.filterSubject.pipe(debounceTime(1000)).subscribe(filter => {
      this.filterChange.emit(filter);
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.filters) {
      changes.filters.currentValue.forEach((element: ITableFilter) => {
        this.filtersModels[element.field] = element.regex;
      });
    }
    if (changes.sorts) {
      changes.sorts.currentValue.forEach((element: ITableSort) => {
        this.sortActive = element.field;
        this.sortDirection = element.direction === 'ASC' ? 'asc' : element.direction === 'DESC' ? 'desc' : '';
      });
    }
  }

  ngOnDestroy(): void {
    this.sortSubscription.unsubscribe();
    this.filterSubscription.unsubscribe();
  }

  filterInputChange(column = '', e: Event) {
    const uniqueValue = column + (<HTMLInputElement>e.target).value;
    if (this.latestFilterValue === uniqueValue) {
      return;
    }
    this.latestFilterValue = uniqueValue;
    this.filterSubject.next({
      field: column,
      regex: (<HTMLInputElement>e.target).value
    });
  }
}
