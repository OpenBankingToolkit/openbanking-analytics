import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PdfWidgetComponent } from './widget.component';

import { TppsWidgetsModule } from 'analytics/src/app/components/tpps/tpps.module';
import { AccountsWidgetsModule } from 'analytics/src/app/components/accounts/accounts.module';
import { DirectoryWidgetsModule } from 'analytics/src/app/components/directory/directory.module';
import { EventsNotificationsWidgetsModule } from 'analytics/src/app/components/events-notifications/events-notifications.module';
import { FundsConfirmationWidgetsModule } from 'analytics/src/app/components/funds-confirmation/funds-confirmation.module';
import { JWKMSWidgetsModule } from 'analytics/src/app/components/jwkms/jwkms.module';
import { PaymentsWidgetsModule } from 'analytics/src/app/components/payments/payments.module';
import { SecurityWidgetsModule } from 'analytics/src/app/components/security/security.module';
import { GSUWidgetsModule } from 'analytics/src/app/components/gsu/gsu.module';
import { SettingsWidgetsModule } from 'analytics/src/app/components/settings/settings.module';

@NgModule({
  declarations: [PdfWidgetComponent],
  exports: [PdfWidgetComponent],
  imports: [
    CommonModule,
    TppsWidgetsModule,
    AccountsWidgetsModule,
    DirectoryWidgetsModule,
    EventsNotificationsWidgetsModule,
    FundsConfirmationWidgetsModule,
    JWKMSWidgetsModule,
    PaymentsWidgetsModule,
    SecurityWidgetsModule,
    GSUWidgetsModule,
    SettingsWidgetsModule
  ]
})
export class PdfWidgetModule {}
