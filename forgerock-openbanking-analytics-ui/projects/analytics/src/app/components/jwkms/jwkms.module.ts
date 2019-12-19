import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslateModule } from '@ngx-translate/core';

import { ForgerockChartModule } from '@forgerock/openbanking-ngx-common/components/forgerock-chart';
import { ForgerockSplitFlapModule } from '@forgerock/openbanking-ngx-common/components/forgerock-splitflap';
import { ForgerockAlertModule } from '@forgerock/openbanking-ngx-common/components/forgerock-alert';

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
