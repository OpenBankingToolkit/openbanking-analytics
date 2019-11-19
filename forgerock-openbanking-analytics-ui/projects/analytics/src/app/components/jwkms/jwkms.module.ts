import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslateModule } from '@ngx-translate/core';

import { ForgerockChartModule } from 'forgerock/src/app/components/forgerock-chart/forgerock-chart.module';
import { ForgerockSplitFlapModule } from 'forgerock/src/app/components/forgerock-splitflap/forgerock-splitflap.module';
import { ForgerockAlertModule } from 'forgerock/src/app/components/forgerock-alert/forgerock-alert.module';

import { MetricsService } from '../../services/metrics.service';
import { JWKMSValidationContainerComponent } from './jwkms-validation.container';
import { JWKMSJWTContainerComponent } from './jwkms-jwt.container';
import { JWKMSTableModule } from './jwkms-table/jwkms-table.module';
import { JwkMsKeysContainerComponent } from 'analytics/src/app/components/jwkms/jwkms-keys.container';

const declarations = [JWKMSValidationContainerComponent, JWKMSJWTContainerComponent, JwkMsKeysContainerComponent];

@NgModule({
  imports: [
    CommonModule,
    ForgerockChartModule,
    MatProgressBarModule,
    JWKMSTableModule,
    ForgerockSplitFlapModule,
    ForgerockAlertModule,
    TranslateModule
  ],
  declarations,
  entryComponents: declarations,
  exports: [...declarations, JWKMSTableModule],
  providers: [MetricsService]
})
export class JWKMSWidgetsModule {}
