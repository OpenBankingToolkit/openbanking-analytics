import { OnDestroy, OnInit } from '@angular/core';
import { select, Store } from '@ngrx/store';
import { Observable, of, Subject } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { catchError, finalize, first, switchMap, takeUntil } from 'rxjs/operators';
import _get from 'lodash-es/get';

import {
  GetChartErrorAction,
  GetChartFinishedAction,
  GetChartRequestAction,
  GetChartSuccessAction,
  selectChartConfig,
  selectChartError,
  selectChartIsFetching,
  selectChartShouldFetch
} from 'analytics/src/store/reducers/charts';
import { IChartsEndpointParams, IDatesState, IState } from 'analytics/src/models';
import { MetricsService } from '../../services/metrics.service';
import { selectDates } from 'analytics/src/store/reducers/dates';
import { TranslateService } from '@ngx-translate/core';

export abstract class AbstractWidgetChartComponent implements OnInit, OnDestroy {
  public isLoading$: Observable<string> = this.store.pipe(select(state => selectChartIsFetching(state, this.id)));
  public config$: Observable<string> = this.store.pipe(select(state => selectChartConfig(state, this.id)));
  public error$: Observable<string> = this.store.pipe(select(state => selectChartError(state, this.id)));
  public dates$: Observable<IDatesState> = this.store.pipe(select(state => selectDates(state)));
  public abstract id: string;
  private _unsubscribeAll: Subject<any> = new Subject();

  constructor(
    protected store: Store<IState>,
    protected metricsService: MetricsService,
    protected translateService: TranslateService
  ) {}

  abstract getService(params: IChartsEndpointParams): Observable<any>;

  abstract responseTransform(data: any): Chart.ChartConfiguration | any;

  protected onRequest() {
    this.store.dispatch(
      new GetChartRequestAction({
        id: this.id
      })
    );
  }

  translateLegends(ref: String) {
    return this.translateService.instant('WIDGETS.LEGENDS.' + ref) === 'WIDGETS.LEGENDS.' + ref
      ? ref
      : this.translateService.instant('WIDGETS.LEGENDS.' + ref);
  }

  ngOnInit(): void {
    this.dates$.pipe(takeUntil(this._unsubscribeAll)).subscribe(() => this.refresh());
    this.refresh();
  }

  ngOnDestroy(): void {
    this._unsubscribeAll.next();
    this._unsubscribeAll.complete();
  }

  refresh() {
    if (!this.shouldFetch()) return;

    let fromDate, toDate;
    this.dates$.pipe(first()).subscribe(dates => {
      fromDate = dates.from;
      toDate = dates.to;
    });
    this.onRequest();
    return this.getService({ fromDate, toDate })
      .pipe(
        takeUntil(this._unsubscribeAll),
        switchMap((response: any) => {
          const config = this.responseTransform(response);
          this.store.dispatch(
            new GetChartSuccessAction({
              id: this.id,
              config
            })
          );
          return of(response);
        }),
        catchError((er: HttpErrorResponse | Error) => {
          if(_get(er, 'error.status') === 403 && _get(er, 'error.error') === 'Forbidden'){
            console.log("Missing roles to access")
            this.store.dispatch(new GetChartErrorAction({ id: this.id, error: "You do not have permission to view the analytics data. Please contact your administrator to ask for permissions." }));
          }else{
            const error = _get(er, 'error.Message') || _get(er, 'error.message') || _get(er, 'message') || er;
            this.store.dispatch(new GetChartErrorAction({ id: this.id, error }));
          }
          return of(er);
        }),
        finalize(() => {
          this.store.dispatch(new GetChartFinishedAction({ id: this.id }));
        })
      )
      .subscribe();
  }

  shouldFetch() {
    let shoudFetch = false;
    this.store
      .pipe(
        select(state => selectChartShouldFetch(state, this.id)),
        first()
      )
      .subscribe(sf => (shoudFetch = sf));
    return shoudFetch;
  }
}
