import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store, select } from '@ngrx/store';
import { Observable } from 'rxjs';

import { IPdfReportConfig } from 'analytics/src/models/pdf';
import { IState } from 'analytics/src/models';
import { SetDatesAction, selectDateFrom, selectDateTo } from 'analytics/src/store/reducers/dates';
import { AnalyticsPDFService } from 'analytics/src/app/services/pdf.service';

@Component({
  selector: 'app-pdf-dynamic-container',
  template: `
    <app-pdf-dynamic [pdf]="pdf" [error]="error" [from]="from$ | async" [to]="to$ | async"></app-pdf-dynamic>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AnalyticsPdfDynamicContainer implements OnInit {
  pdf: IPdfReportConfig;
  from$: Observable<string> = this.store.pipe(select(selectDateFrom));
  to$: Observable<string> = this.store.pipe(select(selectDateTo));
  error: string;

  constructor(
    private activatedRoute: ActivatedRoute,
    private store: Store<IState>,
    private pdfService: AnalyticsPDFService
  ) {
    const { fromDate: from, toDate: to, config, id } = this.activatedRoute.snapshot.queryParams;
    try {
      if (id) {
        if (from && to) {
          this.store.dispatch(
            new SetDatesAction({
              from,
              to
            })
          );
        }
        this.pdf = this.pdfService.getPdfConfig(id);
      } else if (config) {
        try {
          this.pdf = JSON.parse(config);
          if (this.pdf.from && this.pdf.to) {
            this.store.dispatch(
              new SetDatesAction({
                from: this.pdf.from,
                to: this.pdf.to
              })
            );
          }
        } catch (error) {
          throw new Error('Cannot parse PDF configuration.');
        }
      } else {
        throw new Error('PDF id or configuration missing.');
      }
    } catch (error) {
      this.error = error.message;
    }
  }

  ngOnInit() {}
}
