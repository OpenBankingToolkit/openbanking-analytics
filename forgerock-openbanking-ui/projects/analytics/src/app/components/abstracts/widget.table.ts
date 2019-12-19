import { Input, OnDestroy, OnInit } from '@angular/core';
import { select, Store } from '@ngrx/store';
import { Observable, of, Subject } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';
import { catchError, finalize, first, switchMap, takeUntil, combineLatest } from 'rxjs/operators';
import { PageEvent } from '@angular/material/paginator';
import _get from 'lodash-es/get';
import debug from 'debug';

import {
  GetTableErrorAction,
  GetTableFinishedAction,
  GetTableRequestAction,
  GetTableSuccessAction,
  selectTableCurrentPage,
  selectTableError,
  selectTableIsFetching,
  selectTableLength,
  selectTablePageData,
  selectTableShouldFetch,
  selectTableSize,
  GetTablePageChangeAction,
  GetTableFilterChangeAction,
  GetTableSortChangeAction,
  selectTableFilters,
  selectTableSorts,
  GetTableFiltersChangeAction,
  GetTableSortsChangeAction,
  selectIsTableExists
} from 'analytics/src/store/reducers/table';
import { IDatesState, IState, ITableServiceParams, ITableReponseUnion } from 'analytics/src/models';
import { MetricsService } from '../../services/metrics.service';
import { selectDateFrom, selectDates, selectDateTo } from 'analytics/src/store/reducers/dates';
import { ITableFilter, ITableSort, ITableFilterList, ITableSortList } from 'analytics/src/models';

const log = debug('AbstractWidgetTableComponent');

export abstract class AbstractWidgetTableComponent implements OnInit, OnDestroy {
  @Input() page = 0;
  @Input() perPage = 10;
  initialSorts: ITableSortList = [];
  initialFilters: ITableFilterList = [];
  serviceParams: ITableServiceParams;
  public exists$: Observable<boolean> = this.store.pipe(select(state => selectIsTableExists(state, this.id)));
  public isLoading$: Observable<string> = this.store.pipe(select(state => selectTableIsFetching(state, this.id)));
  public pageData$: Observable<any[]> = this.store.pipe(select(state => selectTablePageData(state, this.id)));
  public currentPage$: Observable<number> = this.store.pipe(select(state => selectTableCurrentPage(state, this.id)));
  public length$: Observable<number> = this.store.pipe(select(state => selectTableLength(state, this.id)));
  public error$: Observable<string> = this.store.pipe(select(state => selectTableError(state, this.id)));
  public size$: Observable<number> = this.store.pipe(select(state => selectTableSize(state, this.id)));
  public sorts$: Observable<ITableSortList> = this.store.pipe(select(state => selectTableSorts(state, this.id)));
  public filters$: Observable<ITableFilterList> = this.store.pipe(select(state => selectTableFilters(state, this.id)));
  public dates$: Observable<IDatesState> = this.store.pipe(select(state => selectDates(state)));
  public from$: Observable<string> = this.store.pipe(select(state => selectDateFrom(state)));
  public to$: Observable<string> = this.store.pipe(select(state => selectDateTo(state)));

  public abstract id: string;
  private _unsubscribeAll: Subject<any> = new Subject();

  constructor(
    protected store: Store<IState>,
    protected metricsService: MetricsService,
    protected translateService: TranslateService
  ) {}

  abstract getService(params: ITableServiceParams): Observable<any>;

  ngOnInit(): void {
    let exists;
    this.exists$.pipe(first()).subscribe(val => (exists = val));
    // setup inital data
    if (!exists) {
      this.store.dispatch(new GetTablePageChangeAction({ id: this.id, page: this.page || 0, size: this.perPage }));
      this.store.dispatch(new GetTableSortsChangeAction({ id: this.id, sorts: this.initialSorts }));
      this.store.dispatch(new GetTableFiltersChangeAction({ id: this.id, filters: this.initialFilters }));
    }

    // listen to params changes and reload the page when it happens
    this.dates$
      .pipe(
        combineLatest(this.currentPage$, this.size$, this.sorts$, this.filters$),
        takeUntil(this._unsubscribeAll)
      )
      .subscribe(([{ from, to }, page, size, sorts, filters]) => {
        this.serviceParams = {
          from,
          to,
          page,
          size,
          sorts,
          filters
        };
        log('this.serviceParams', this.serviceParams);
        this.refresh();
      });
  }

  ngOnDestroy(): void {
    this._unsubscribeAll.next();
    this._unsubscribeAll.complete();
  }

  pageChange = (e: PageEvent) =>
    this.store.dispatch(new GetTablePageChangeAction({ id: this.id, page: e.pageIndex, size: e.pageSize }));
  filterChange = (filter: ITableFilter) => this.store.dispatch(new GetTableFilterChangeAction({ id: this.id, filter }));
  sortChange = (sort: ITableSort) => this.store.dispatch(new GetTableSortChangeAction({ id: this.id, sort }));

  refresh() {
    if (!this.shouldFetch()) return;
    this.forceRefresh();
  }

  public getDates() {
    let fromDate, toDate;

    this.dates$.pipe(first()).subscribe(dates => {
      fromDate = dates.from;
      toDate = dates.to;
    });

    return {
      fromDate: fromDate,
      toDate: toDate
    };
  }

  forceRefresh() {
    this.store.dispatch(new GetTableRequestAction({ id: this.id }));
    return this.getService(this.serviceParams)
      .pipe(
        takeUntil(this._unsubscribeAll),
        switchMap((response: ITableReponseUnion) => {
          this.store.dispatch(
            new GetTableSuccessAction({
              id: this.id,
              response
            })
          );
          return of(response);
        }),
        catchError((er: HttpErrorResponse | Error) => {
          const error = _get(er, 'error.Message') || _get(er, 'error.message') || _get(er, 'message') || er;
          this.store.dispatch(new GetTableErrorAction({ id: this.id, error }));
          return of(er);
        }),
        finalize(() => {
          this.store.dispatch(new GetTableFinishedAction({ id: this.id }));
        })
      )
      .subscribe();
  }

  shouldFetch() {
    let shoudFetch = false;
    this.store
      .pipe(
        select(state => selectTableShouldFetch(state, this.id)),
        first()
      )
      .subscribe(sf => (shoudFetch = sf));
    return shoudFetch;
  }
}
