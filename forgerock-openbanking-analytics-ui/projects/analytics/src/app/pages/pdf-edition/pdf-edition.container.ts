import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Store, select } from '@ngrx/store';
import { Observable } from 'rxjs';

import { IPdfReportConfig } from 'analytics/src/models/pdf';
import { IState } from 'analytics/src/models';
import { SetDatesAction, selectDates } from 'analytics/src/store/reducers/dates';
import { selectPdfIsFetching, GetPdfRequestAction } from 'analytics/src/store/reducers/pdf';

const pdfId = 'pdf-creator';

@Component({
  selector: 'app-pdf-edition-container',
  template: `
    <app-pdf-edition
      [isPdfLoading]="isPdfLoading$ | async"
      [dates]="dates$ | async"
      (download)="download($event)"
      (setDates)="setDates($event)"
    ></app-pdf-edition>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AnalyticsPdfEditionContainer implements OnInit {
  dates$: Observable<{ from: string; to: string }> = this.store.pipe(select(selectDates));
  public isPdfLoading$: Observable<boolean> = this.store.pipe(select(state => selectPdfIsFetching(state, pdfId)));

  constructor(private store: Store<IState>) {}

  ngOnInit() {}

  download(config: IPdfReportConfig) {
    this.store.dispatch(new GetPdfRequestAction({ id: pdfId, config }));
  }

  setDates({ from, to }) {
    this.store.dispatch(
      new SetDatesAction({
        from,
        to
      })
    );
  }
}
