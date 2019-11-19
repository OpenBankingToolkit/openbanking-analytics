import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store, select } from '@ngrx/store';

import { IState } from 'analytics/src/models';
import { selectDateTo, selectDateFrom, SetDatesAction } from 'analytics/src/store/reducers/dates';
import { formatDateForMetricsServer } from 'analytics/src/utils/dates';
import { ForgerockOIDCLogoutRequestAction } from 'forgerock/src/app/modules/oidc/store/reducers/logout';
import { selectConnected, selectUserId } from 'forgerock/src/app/modules/oidc/store/reducers/user';

@Component({
  // tslint:disable-next-line
  selector: 'analytics-toolbar-menu-container',
  template: `
    <analytics-datepicker-menu
      [from]="from$ | async"
      [to]="to$ | async"
      (change)="changeDates($event)"
    ></analytics-datepicker-menu>
    <analytics-toolbar-menu
      [username]="username$ | async"
      [connected]="connected$ | async"
      (logout)="logout($event)"
    ></analytics-toolbar-menu>
  `,
  styles: [
    `
      :host {
        display: flex;
        flex-direction: row;
        align-items: center;
      }
    `
  ]
})
export class AnalyticsToolbarMenuContainer implements OnInit {
  connected$: Observable<boolean> = this.store.pipe(select(selectConnected));
  username$: Observable<string> = this.store.pipe(select(selectUserId));
  from$: Observable<string> = this.store.pipe(select(selectDateFrom));
  to$: Observable<string> = this.store.pipe(select(selectDateTo));

  constructor(private store: Store<IState>) {}

  ngOnInit(): void {}

  logout(e: Event) {
    this.store.dispatch(new ForgerockOIDCLogoutRequestAction());
  }

  changeDates({ from, to }: { from: Date; to: Date }) {
    this.store.dispatch(
      new SetDatesAction({
        from: formatDateForMetricsServer(from),
        to: formatDateForMetricsServer(to)
      })
    );
  }
}
