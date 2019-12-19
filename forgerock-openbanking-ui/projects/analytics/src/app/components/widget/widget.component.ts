import {
  ChangeDetectionStrategy,
  Component,
  ComponentFactoryResolver,
  ComponentRef,
  Input,
  OnChanges,
  OnInit,
  ViewChild,
  ViewContainerRef,
  ViewEncapsulation
} from '@angular/core';
import debug from 'debug';
// Tpps
import { TppsRoleContainerComponent } from 'analytics/src/app/components/tpps/tpps.role.container';
import { TppsOriginContainerComponent } from 'analytics/src/app/components/tpps/tpps.origin.container';
import { TppsRegistrationContainerComponent } from 'analytics/src/app/components/tpps/tpps.registration.container';
import { TppsTableComponent } from 'analytics/src/app/components/tpps/tpps-table/tpps-table.component';
// JWKMS
import { JWKMSValidationContainerComponent } from 'analytics/src/app/components/jwkms/jwkms-validation.container';
import { JWKMSJWTContainerComponent } from 'analytics/src/app/components/jwkms/jwkms-jwt.container';
import { JWKMSTableComponent } from 'analytics/src/app/components/jwkms/jwkms-table/jwkms-table.component';
import { JwkMsKeysContainerComponent } from 'analytics/src/app/components/jwkms/jwkms-keys.container';
// Directory
import { DirectoryOrganisationContainerComponent } from 'analytics/src/app/components/directory/directory-organisation.container';
import { DirectoryDownloadingKeyContainerComponent } from 'analytics/src/app/components/directory/directory-downloading-keys.container';
import { DirectorySoftwareStatementContainerComponent } from 'analytics/src/app/components/directory/directory-software-statement.container';
import { DirectorySSAContainerComponent } from 'analytics/src/app/components/directory/directory-ssa.container';
// Security
import { SecurityAccessTokenRequestsContainerComponent } from 'analytics/src/app/components/security/security-access-token-requests.container';
import { SecurityAuthoriseRequestsContainerComponent } from 'analytics/src/app/components/security/security-authorise-requests.container';
import { SecurityAccessTokenContainerComponent } from 'analytics/src/app/components/security/security-access-token.container';
import { SecurityIdTokenContainerComponent } from 'analytics/src/app/components/security/security-id-token.container';
// Account
import { AccountsFlowContainerComponent } from 'analytics/src/app/components/accounts/accounts-flow.container';
import { AccountsTableComponent } from 'analytics/src/app/components/accounts/accounts-table/accounts-table.component';
// Payments
import { PaymentsSingleConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-single-consent-activities.container';
import { PaymentsDomesticScheduledConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-domestic-scheduled-consent-activities.container';
import { PaymentsDomesticStandingOrdersConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-domestic-standing-orders-consent-activities.container';
import { PaymentsInternationalConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-international-consent-activities.container';
import { PaymentsInternationalScheduledConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-international-scheduled-consent-activities.container';
import { PaymentsInternationalStandingOrdersConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-international-standing-orders-consent-activities.container';
import { PaymentsTableComponent } from 'analytics/src/app/components/payments/payments-table/payments-table.component';
import { PaymentsDomesticConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-domestic-consent-activities.container';
import { PaymentsFileConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-file-consent-activities.container';
// Events notifications
import { EventsNotificationsFlowContainerComponent } from 'analytics/src/app/components/events-notifications/events-notifications-flow.container';
import { EventsNotificationsCallbacksContainerComponent } from 'analytics/src/app/components/events-notifications/events-notifications-callbacks.container';
import { EventsNotificationsTableComponent } from 'analytics/src/app/components/events-notifications/events-notifications-table/events-notifications-table.component';
// FundsConfirmation
import { FundsConfirmationConsentActivitiesContainerComponent } from 'analytics/src/app/components/funds-confirmation/funds-confirmation-flow.container';
import { FundsConfirmationTableComponent } from 'analytics/src/app/components/funds-confirmation/funds-confirmation-table/funds-confirmation-table.component';
// GSU
import { GSUAPICallsPerWeekContainerComponent } from 'analytics/src/app/components/gsu/gsu-api-calls-per-week.container';
import { GSUAPICallsContainerComponent } from 'analytics/src/app/components/gsu/gsu-api-calls.container';
import { GSUNbrCallByStatusContainerComponent } from 'analytics/src/app/components/gsu/gsu-nbr-call-by-status.container';
import { GSUNbrOBRISessionsContainerComponent } from 'analytics/src/app/components/gsu/gsu-nbr-obri-sessions.container';
import { GSUNbrOfPSUsContainerComponent } from 'analytics/src/app/components/gsu/gsu-nbr-of-psus.container';
import { GSUNbrOfTPPsContainerComponent } from 'analytics/src/app/components/gsu/gsu-nbr-of-tpps.container';

import { IPdfWidget, IPdfWidgetAttributes, IPdfWidgetType } from 'analytics/src/models/pdf';
import { PaymentsConfirmationOfFundsConsentActivitiesContainerComponent } from 'analytics/src/app/components/payments/payments-confirmation-of-funds.container';
import { PaymentsOBVersionsContainerComponent } from 'analytics/src/app/components/payments/payments-ob-versions.container';
import { FundsOBVersionsContainerComponent } from 'analytics/src/app/components/funds-confirmation/funds-ob-versions.container';
import { EventsOBVersionsContainerComponent } from 'analytics/src/app/components/events-notifications/events-ob-versions.container';
import { AccountsOBVersionsContainerComponent } from 'analytics/src/app/components/accounts/accounts-ob-versions.container';
import { GSUOBVersionsContainerComponent } from 'analytics/src/app/components/gsu/gsu-ob-versions.container';
import { GSUOBApiTypeContainerComponent } from 'analytics/src/app/components/gsu/gsu-ob-type.container';
import { AccountsConsentTypeContainerComponent } from 'analytics/src/app/components/accounts/accounts-consent-type.container';
import { FundsConsentTypeContainerComponent } from 'analytics/src/app/components/funds-confirmation/funds-consent-type.container';
import { PaymentsConsentTypeContainerComponent } from 'analytics/src/app/components/payments/payments-consent-type.container';
import { GSUAPIResponseTimesContainerComponent } from 'analytics/src/app/components/gsu/gsu-api-response-time.container';
import { SecurityAccessTokenResponseTimesContainerComponent } from 'analytics/src/app/components/security/security-access-token-response-times.container';
import { SecurityAuthoriseResponseTimesContainerComponent } from 'analytics/src/app/components/security/security-authorise-response-times.container';
import { SecurityTableComponent } from 'analytics/src/app/components/security/security-table/security-table.component';
import { TppsApiTableComponent } from 'analytics/src/app/components/tpps/tpps-apis-table/tpps-api-table.component';
import { EndpointUsageRawTableComponent } from 'analytics/src/app/components/settings/endpoint-usage-raw-table/endpoint-usage-raw-table.component';

const log = debug('PdfWidgetComponent');

@Component({
  selector: 'app-widget-dynamic',
  template: `
    <ng-template #dynamicTarget></ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None
})
export class PdfWidgetComponent implements OnInit, OnChanges {
  constructor(private componentFactoryResolver: ComponentFactoryResolver) {}

  @Input() type: IPdfWidget;
  @Input() attributes: IPdfWidgetAttributes;
  @ViewChild('dynamicTarget', { read: ViewContainerRef, static: true })
  dynamicTarget: ViewContainerRef;
  componentRef: ComponentRef<any>;

  ngOnInit() {}

  ngOnChanges(changes: any) {
    if (!changes.type || !changes.type.currentValue) return;

    this.create(changes.type.currentValue);
  }

  create(type: IPdfWidgetType) {
    log(`create widget: ${type}`, this.attributes);

    try {
      const componentInstance = this.getComponentInstance(type);
      const componentFactory = this.componentFactoryResolver.resolveComponentFactory(componentInstance);
      this.dynamicTarget.clear();
      this.componentRef = this.dynamicTarget.createComponent(componentFactory);
      this.componentRef.instance.type = this.type;
      if (this.attributes) {
        Object.keys(this.attributes).forEach(key => (this.componentRef.instance[key] = this.attributes[key]));
      }
    } catch (error) {
      log(error);
    }
  }

  getComponentInstance(type: IPdfWidgetType): any {
    switch (type) {
      // TPPs
      case IPdfWidgetType.BAR_TPPS_ROLES:
        return TppsRoleContainerComponent;
      case IPdfWidgetType.DOUGHNUT_TPPS_ORIGIN:
        return TppsOriginContainerComponent;
      case IPdfWidgetType.LINE_TPPS_REGISTRATION:
        return TppsRegistrationContainerComponent;
      case IPdfWidgetType.TABLE_TPPS_ENTRIES:
        return TppsTableComponent;
      case IPdfWidgetType.TABLE_TPPS_API_USAGE:
        return TppsApiTableComponent;
      // JWKMS
      case IPdfWidgetType.COUNTER_JWKMS_GENERATED:
        return JWKMSJWTContainerComponent;
      case IPdfWidgetType.COUNTER_JWKMS_VALIDATION:
        return JWKMSValidationContainerComponent;
      case IPdfWidgetType.DOUGHNUT_JWKMS_NUMBER_OF_KEYS:
        return JwkMsKeysContainerComponent;
      case IPdfWidgetType.TABLE_JWKMS_USAGE:
        return JWKMSTableComponent;
      // Directory
      case IPdfWidgetType.COUNTER_DIRECTORY_ORGANISATION_REGISTERED:
        return DirectoryOrganisationContainerComponent;
      case IPdfWidgetType.COUNTER_DIRECTORY_DOWLOADING_KEYS:
        return DirectoryDownloadingKeyContainerComponent;
      case IPdfWidgetType.COUNTER_DIRECTORY_SOFTWARE_STATEMENTS_REGISTERED:
        return DirectorySoftwareStatementContainerComponent;
      case IPdfWidgetType.COUNTER_DIRECTORY_SSA_GENERATED:
        return DirectorySSAContainerComponent;
      // Security
      case IPdfWidgetType.COUNTER_SECURITY_ACCESS_TOKEN_GENERATED:
        return SecurityAccessTokenContainerComponent;
      case IPdfWidgetType.COUNTER_SECURITY_ID_TOKEN_GENERATED:
        return SecurityIdTokenContainerComponent;
      case IPdfWidgetType.LINE_SECURITY_ACCESS_TOKEN_REQUESTS:
        return SecurityAccessTokenRequestsContainerComponent;
      case IPdfWidgetType.LINE_SECURITY_AUTHORISE_REQUESTS:
        return SecurityAuthoriseRequestsContainerComponent;
      case IPdfWidgetType.LINE_SECURITY_ACCESS_TOKEN_RESPONSE_TIMES:
        return SecurityAccessTokenResponseTimesContainerComponent;
      case IPdfWidgetType.LINE_SECURITY_AUTHORISE_RESPONSE_TIMES:
        return SecurityAuthoriseResponseTimesContainerComponent;
      case IPdfWidgetType.TABLE_SECURITY_USAGE:
        return SecurityTableComponent;
      // Account
      case IPdfWidgetType.DOUGHNUT_ACCOUNTS_CONSENT_TYPE:
        return AccountsConsentTypeContainerComponent;
      case IPdfWidgetType.DOUGHNUT_ACCOUNTS_FLOW_COMPLETED:
        return AccountsFlowContainerComponent;
      case IPdfWidgetType.DOUGHNUT_ACCOUNTS_OB_VERSIONS:
        return AccountsOBVersionsContainerComponent;
      case IPdfWidgetType.TABLE_ACCOUNTS_USAGE:
        return AccountsTableComponent;
      // Payments
      case IPdfWidgetType.DOUGHNUT_PAYMENTS_SINGLE_CONSENTS_ACTIVITIES:
        return PaymentsSingleConsentActivitiesContainerComponent;
      case IPdfWidgetType.DOUGHNUT_PAYMENTS_DOMESTIC_CONSENTS_ACTIVITIES:
        return PaymentsDomesticConsentActivitiesContainerComponent;
      case IPdfWidgetType.DOUGHNUT_PAYMENTS_DOMESTIC_SCHEDULED_CONSENTS_ACTIVITIES:
        return PaymentsDomesticScheduledConsentActivitiesContainerComponent;
      case IPdfWidgetType.DOUGHNUT_PAYMENTS_DOMESTIC_STANDING_ORDERS_CONSENTS_ACTIVITIES:
        return PaymentsDomesticStandingOrdersConsentActivitiesContainerComponent;
      case IPdfWidgetType.DOUGHNUT_PAYMENTS_INTERNATIONAL_CONSENTS_ACTIVITIES:
        return PaymentsInternationalConsentActivitiesContainerComponent;
      case IPdfWidgetType.DOUGHNUT_PAYMENTS_INTERNATIONAL_SCHEDULED_CONSENTS_ACTIVITIES:
        return PaymentsInternationalScheduledConsentActivitiesContainerComponent;
      case IPdfWidgetType.DOUGHNUT_PAYMENTS_INTERNATIONAL_STANDING_ORDER_CONSENTS_ACTIVITIES:
        return PaymentsInternationalStandingOrdersConsentActivitiesContainerComponent;
      case IPdfWidgetType.DOUGHNUT_PAYMENTS_FILE_CONSENTS_ACTIVITIES:
        return PaymentsFileConsentActivitiesContainerComponent;
      case IPdfWidgetType.DOUGHNUT_PAYMENTS_COF:
        return PaymentsConfirmationOfFundsConsentActivitiesContainerComponent;
      case IPdfWidgetType.DOUGHNUT_PAYMENTS_OB_VERSIONS:
        return PaymentsOBVersionsContainerComponent;
      case IPdfWidgetType.DOUGHNUT_PAYMENTS_CONSENT_TYPE:
        return PaymentsConsentTypeContainerComponent;

      case IPdfWidgetType.TABLE_PAYMENTS_USAGE:
        return PaymentsTableComponent;
      // FundsConfirmation
      case IPdfWidgetType.DOUGHNUT_CONFIRMATION_OF_FUNDS_FLOW_COMPLETED:
        return FundsConfirmationConsentActivitiesContainerComponent;
      case IPdfWidgetType.DOUGHNUT_CONFIRMATION_OF_FUNDS_CONSENT_TYPE:
        return FundsConsentTypeContainerComponent;
      case IPdfWidgetType.DOUGHNUT_CONFIRMATION_OF_FUNDS_OB_VERSIONS:
        return FundsOBVersionsContainerComponent;
      case IPdfWidgetType.TABLE_CONFIRMATION_OF_FUNDS_USAGE:
        return FundsConfirmationTableComponent;
      // Events notifications
      case IPdfWidgetType.COUNTER_EVENTS_NOTIF_FLOW_COMPLETED:
        return EventsNotificationsFlowContainerComponent;
      case IPdfWidgetType.DOUGHNUT_EVENTS_NOTIF_OB_VERSIONS:
        return EventsOBVersionsContainerComponent;
      case IPdfWidgetType.DOUGHNUT_EVENTS_NOTIF_CALLBACK_SENT:
        return EventsNotificationsCallbacksContainerComponent;

      case IPdfWidgetType.TABLE_EVENTS_NOTIF_USAGE:
        return EventsNotificationsTableComponent;
      //GSU
      case IPdfWidgetType.LINE_GSU_API_CALLS_PER_WEEK:
        return GSUAPICallsPerWeekContainerComponent;
      case IPdfWidgetType.LINE_GSU_API_CALLS:
        return GSUAPICallsContainerComponent;
      case IPdfWidgetType.DOUGHNUT_GSU_CALL_BY_STATUS:
        return GSUNbrCallByStatusContainerComponent;
      case IPdfWidgetType.DOUGHNUT_GSU_OBRI_SESSIONS:
        return GSUNbrOBRISessionsContainerComponent;
      case IPdfWidgetType.DOUGHNUT_GSU_OB_VERSIONS:
        return GSUOBVersionsContainerComponent;
      case IPdfWidgetType.DOUGHNUT_GSU_OB_API_TYPE:
        return GSUOBApiTypeContainerComponent;
      case IPdfWidgetType.COUNTER_GSU_PSUS:
        return GSUNbrOfPSUsContainerComponent;
      case IPdfWidgetType.COUNTER_GSU_TPPS:
        return GSUNbrOfTPPsContainerComponent;
      case IPdfWidgetType.LINE_GSU_API_RESPONSE_TIME:
        return GSUAPIResponseTimesContainerComponent;

      // Settings
      case IPdfWidgetType.TABLE_SETTINGS_ENDPOINT_USAGE_RAW:
        return EndpointUsageRawTableComponent;

      default:
        throw new Error(`"${type}" is not implemented yet`);
    }
  }
}
