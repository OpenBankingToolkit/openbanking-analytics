import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { AnalyticsDashboardDynamicModule } from './dynamic/dynamic.module';
import { AnalyticsDashboardDynamicComponent } from './dynamic/dynamic.component';
import { ForgerockCustomerCanAccessGuard } from '@forgerock/openbanking-ngx-common/guards';

const routes: Routes = [
  {
    path: ':dashboardId',
    canActivate: [ForgerockCustomerCanAccessGuard],
    component: AnalyticsDashboardDynamicComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes), AnalyticsDashboardDynamicModule],
  exports: [RouterModule]
})
export class AnalyticsDashboardRoutingModule {}
