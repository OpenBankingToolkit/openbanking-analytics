import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslateModule } from '@ngx-translate/core';

import { ForgerockChartModule } from '@forgerock/openbanking-ngx-common/components/forgerock-chart';
import { ForgerockSplitFlapModule } from '@forgerock/openbanking-ngx-common/components/forgerock-splitflap';
import { ForgerockAlertModule } from '@forgerock/openbanking-ngx-common/components/forgerock-alert';

import { MetricsService } from '../../services/metrics.service';
import { DirectoryOrganisationContainerComponent } from './directory-organisation.container';
import { DirectoryDownloadingKeyContainerComponent } from './directory-downloading-keys.container';
import { DirectorySoftwareStatementContainerComponent } from './directory-software-statement.container';
import { DirectorySSAContainerComponent } from './directory-ssa.container';

const declarations = [
  DirectoryOrganisationContainerComponent,
  DirectorySSAContainerComponent,
  DirectorySoftwareStatementContainerComponent,
  DirectoryDownloadingKeyContainerComponent
];

@NgModule({
  imports: [
    CommonModule,
    ForgerockChartModule,
    MatProgressBarModule,
    ForgerockSplitFlapModule,
    ForgerockAlertModule,
    TranslateModule
  ],
  declarations,
  entryComponents: declarations,
  exports: declarations,
  providers: [MetricsService]
})
export class DirectoryWidgetsModule {}
