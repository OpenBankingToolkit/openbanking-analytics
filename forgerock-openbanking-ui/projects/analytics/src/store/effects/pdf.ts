import { Injectable } from '@angular/core';
import { Action } from '@ngrx/store';
import { saveAs } from 'file-saver';
import { Store } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Observable, of } from 'rxjs';
import { catchError, map, mergeMap, withLatestFrom } from 'rxjs/operators';
import _get from 'lodash-es/get';

import { types, GetPdfRequestAction, GetPdfSuccessAction, GetPdfErrorAction } from 'analytics/src/store/reducers/pdf';
import { HttpErrorResponse } from '@angular/common/http';
import { MetricsService } from 'analytics/src/app/services/metrics.service';
import { ForgerockMessagesService } from '@forgerock/openbanking-ngx-common/services/forgerock-messages';
import { selectDates } from '../reducers/dates';
import { IState, IDatesState } from 'analytics/src/models';

@Injectable()
export class PdfEffects {
  constructor(
    private actions$: Actions,
    private metricsService: MetricsService,
    private message: ForgerockMessagesService,
    private store: Store<IState>
  ) {}

  @Effect()
  request$: Observable<Action> = this.actions$.pipe(
    ofType(types.PDF_GET_REQUEST),
    withLatestFrom(this.store.select(selectDates)),
    mergeMap(([action, { from: fromDate, to: toDate }]: [GetPdfRequestAction, IDatesState]) => {
      const { id, config } = action.payload;
      let httpObservable: Observable<any>;
      if (config) {
        httpObservable = this.metricsService.postPdf(config);
      } else {
        httpObservable = this.metricsService.getPdf({
          id,
          fromDate,
          toDate
        });
      }

      return httpObservable.pipe(
        map((blob: any) => {
          saveAs(blob, `${Date.now()}-${id}.pdf`);
          return new GetPdfSuccessAction({
            id
          });
        }),
        catchError((er: HttpErrorResponse) => {
          const error = _get(er, 'error.Message') || _get(er, 'error.message') || _get(er, 'message') || er;
          this.message.error(error);
          console.error(error)
          return of(new GetPdfErrorAction({ id }));
        })
      );
    })
  );
}
