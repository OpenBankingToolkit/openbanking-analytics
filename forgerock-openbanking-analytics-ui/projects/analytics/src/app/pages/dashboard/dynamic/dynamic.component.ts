import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

import { ForgerockConfigService } from '@forgerock/openbanking-ngx-common/services/forgerock-config';
import defaultDashboardsConfig from '../dashboards.config';
import { IDashboardConfig } from 'analytics/src/models';
@Component({
  selector: 'app-pdf-dynamic',
  templateUrl: './dynamic.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AnalyticsDashboardDynamicComponent implements OnInit, OnDestroy {
  route: string;
  dashboard: IDashboardConfig;
  private _unsubscribeAll: Subject<any> = new Subject();

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private config: ForgerockConfigService
  ) {
    this.router.events.pipe(takeUntil(this._unsubscribeAll)).subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.pageChange();
        this.cdr.detectChanges();
      }
    });
  }

  pageChange() {
    const { dashboardId } = this.activatedRoute.snapshot.params;
    this.route = dashboardId;
    this.dashboard = this.config.get(`dashboards.${dashboardId}`) || defaultDashboardsConfig[dashboardId];
  }

  ngOnInit() {
    this.pageChange();
  }

  ngOnDestroy(): void {
    this._unsubscribeAll.next();
    this._unsubscribeAll.complete();
  }
}
