import { Component, OnInit, Input } from '@angular/core';
import { Store, select } from '@ngrx/store';
import { Observable } from 'rxjs';

import { IState } from 'analytics/src/models';
import { selectPdfIsFetching, GetPdfRequestAction } from 'analytics/src/store/reducers/pdf';
import { Platform } from '@angular/cdk/platform';
import { Router } from '@angular/router';
import { AnalyticsPDFService } from '../../services/pdf.service';
import { ForgerockMessagesService } from '@forgerock/openbanking-ngx-common/services/forgerock-messages';

const selector = 'app-dashboard-toolbar-container';

@Component({
  selector,
  template: `
    <app-dashboard-toolbar
      [title]="title"
      [pdfPath]="pdfPath"
      [isPdfLoading]="isPdfLoading$ | async"
      (pdfClick)="onPdfClick($event)"
    >
    </app-dashboard-toolbar>
  `,
  styles: [
    `
      :host {
        display: block;
      }
    `
  ]
})
export class AnalyticsDashboardToolbarContainer implements OnInit {
  @Input() title: '';
  @Input() pdfPath: '';
  isMobile: boolean = this._platform.ANDROID || this._platform.IOS;

  public isPdfLoading$: Observable<boolean> = this.store.pipe(
    select(state => selectPdfIsFetching(state, this.pdfPath))
  );

  constructor(
    protected store: Store<IState>,
    private _platform: Platform,
    private router: Router,
    private pdfService: AnalyticsPDFService,
    private messages: ForgerockMessagesService
  ) {}

  public onPdfClick(e: Event) {
    if (this.isMobile) {
      this.store.dispatch(new GetPdfRequestAction({ id: this.pdfPath }));
    } else {
      try {
        this.router.navigate(['pdf-creator'], {
          queryParams: {
            pdf: JSON.stringify(this.pdfService.getPdfConfig(this.pdfPath))
          }
        });
      } catch (error) {
        this.messages.error(error.message);
      }
    }
  }

  ngOnInit() {}
}
