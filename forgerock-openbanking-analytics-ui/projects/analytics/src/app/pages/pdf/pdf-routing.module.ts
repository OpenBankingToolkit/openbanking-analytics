import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { AnalyticsPdfDynamicModule } from './dynamic/dynamic.module';
import { AnalyticsPdfDynamicContainer } from './dynamic/dynamic.container';

const routes: Routes = [
  {
    path: '**',
    component: AnalyticsPdfDynamicContainer
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes), AnalyticsPdfDynamicModule],
  exports: [RouterModule]
})
export class AnalyticsPdfRoutingModule {}
