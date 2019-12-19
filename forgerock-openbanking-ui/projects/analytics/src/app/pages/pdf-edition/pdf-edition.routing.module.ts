import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { AnalyticsPdfEditionContainer } from './pdf-edition.container';

const routes: Routes = [
  {
    path: '**',
    component: AnalyticsPdfEditionContainer
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AnalyticsPdfEditionRoutingModule {}
