import { IPdfWidgetType } from 'analytics/src/models/pdf';
import { IDashboardConfig } from 'analytics/src/models';

const tpp: IDashboardConfig = {
  title: 'NAV.TPP',
  widgets: [
    {
      type: IPdfWidgetType.DOUGHNUT_TPPS_ORIGIN,
      width: 50
    },
    {
      type: IPdfWidgetType.BAR_TPPS_ROLES,
      width: 50
    },
    {
      type: IPdfWidgetType.LINE_TPPS_REGISTRATION,
      width: 100
    },
    {
      type: IPdfWidgetType.TABLE_TPPS_ENTRIES,
      width: 100
    },
    {
      type: IPdfWidgetType.TABLE_TPPS_API_USAGE,
      width: 100
    }
  ]
};

const jwkms: IDashboardConfig = {
  title: 'NAV.JWKMS',
  widgets: [
    {
      type: IPdfWidgetType.COUNTER_JWKMS_GENERATED,
      width: 50
    },
    {
      type: IPdfWidgetType.COUNTER_JWKMS_VALIDATION,
      width: 50
    },
    {
      type: IPdfWidgetType.TABLE_JWKMS_USAGE,
      width: 100
    }
  ]
};

const directory: IDashboardConfig = {
  title: 'NAV.DIRECTORY',
  widgets: [
    {
      type: IPdfWidgetType.COUNTER_DIRECTORY_ORGANISATION_REGISTERED,
      width: 50
    },
    {
      type: IPdfWidgetType.COUNTER_DIRECTORY_SOFTWARE_STATEMENTS_REGISTERED,
      width: 50
    },
    {
      type: IPdfWidgetType.COUNTER_DIRECTORY_SSA_GENERATED,
      width: 50
    },
    {
      type: IPdfWidgetType.COUNTER_DIRECTORY_DOWLOADING_KEYS,
      width: 50
    }
  ]
};

const security: IDashboardConfig = {
  title: 'NAV.SECURITY',
  widgets: [
    {
      type: IPdfWidgetType.COUNTER_SECURITY_ACCESS_TOKEN_GENERATED,
      width: 50
    },
    {
      type: IPdfWidgetType.COUNTER_SECURITY_ID_TOKEN_GENERATED,
      width: 50
    },
    {
      type: IPdfWidgetType.LINE_SECURITY_AUTHORISE_REQUESTS,
      width: 100
    },
    {
      type: IPdfWidgetType.LINE_SECURITY_AUTHORISE_RESPONSE_TIMES,
      width: 100
    },
    {
      type: IPdfWidgetType.LINE_SECURITY_ACCESS_TOKEN_REQUESTS,
      width: 100
    },
    {
      type: IPdfWidgetType.LINE_SECURITY_ACCESS_TOKEN_RESPONSE_TIMES,
      width: 100
    },
    {
      type: IPdfWidgetType.TABLE_SECURITY_USAGE,
      width: 100
    }
  ]
};

const accounts: IDashboardConfig = {
  title: 'NAV.ACCOUNTS',
  widgets: [
    {
      type: IPdfWidgetType.DOUGHNUT_ACCOUNTS_OB_VERSIONS,
      width: 50,
      offset: 25
    },
    {
      type: IPdfWidgetType.DOUGHNUT_ACCOUNTS_CONSENT_TYPE,
      width: 50
    },
    {
      type: IPdfWidgetType.DOUGHNUT_ACCOUNTS_FLOW_COMPLETED,
      width: 50
    },
    {
      type: IPdfWidgetType.TABLE_ACCOUNTS_USAGE,
      width: 100
    }
  ]
};

const payments: IDashboardConfig = {
  title: 'NAV.PAYMENTS',
  widgets: [
    {
      type: IPdfWidgetType.DOUGHNUT_PAYMENTS_OB_VERSIONS,
      width: 50
    },
    {
      type: IPdfWidgetType.DOUGHNUT_PAYMENTS_CONSENT_TYPE,
      width: 50
    },
    {
      type: IPdfWidgetType.DOUGHNUT_PAYMENTS_COF,
      width: 50,
      offset: 25
    },
    {
      type: IPdfWidgetType.DOUGHNUT_PAYMENTS_DOMESTIC_CONSENTS_ACTIVITIES,
      width: 33
    },
    {
      type: IPdfWidgetType.DOUGHNUT_PAYMENTS_DOMESTIC_SCHEDULED_CONSENTS_ACTIVITIES,
      width: 33
    },
    {
      type: IPdfWidgetType.DOUGHNUT_PAYMENTS_DOMESTIC_STANDING_ORDERS_CONSENTS_ACTIVITIES,
      width: 33
    },
    {
      type: IPdfWidgetType.DOUGHNUT_PAYMENTS_INTERNATIONAL_CONSENTS_ACTIVITIES,
      width: 33
    },
    {
      type: IPdfWidgetType.DOUGHNUT_PAYMENTS_INTERNATIONAL_SCHEDULED_CONSENTS_ACTIVITIES,
      width: 33
    },
    {
      type: IPdfWidgetType.DOUGHNUT_PAYMENTS_INTERNATIONAL_STANDING_ORDER_CONSENTS_ACTIVITIES,
      width: 33
    },
    {
      type: IPdfWidgetType.DOUGHNUT_PAYMENTS_FILE_CONSENTS_ACTIVITIES,
      width: 33
    },
    {
      type: IPdfWidgetType.DOUGHNUT_PAYMENTS_SINGLE_CONSENTS_ACTIVITIES,
      width: 33
    },
    {
      type: IPdfWidgetType.TABLE_PAYMENTS_USAGE,
      width: 100
    }
  ]
};

const funds: IDashboardConfig = {
  title: 'NAV.FUNDS_CONFIRMATION',
  widgets: [
    {
      type: IPdfWidgetType.DOUGHNUT_CONFIRMATION_OF_FUNDS_OB_VERSIONS,
      width: 50,
      offset: 25
    },
    {
      type: IPdfWidgetType.DOUGHNUT_CONFIRMATION_OF_FUNDS_CONSENT_TYPE,
      width: 50
    },
    {
      type: IPdfWidgetType.DOUGHNUT_CONFIRMATION_OF_FUNDS_FLOW_COMPLETED,
      width: 50
    },
    {
      type: IPdfWidgetType.TABLE_CONFIRMATION_OF_FUNDS_USAGE,
      width: 100
    }
  ]
};

const events: IDashboardConfig = {
  title: 'NAV.EVENTS_NOTIFICATIONS',
  widgets: [
    {
      type: IPdfWidgetType.DOUGHNUT_EVENTS_NOTIF_OB_VERSIONS,
      width: 50,
      offset: 25
    },
    {
      type: IPdfWidgetType.COUNTER_EVENTS_NOTIF_FLOW_COMPLETED,
      width: 50
    },
    {
      type: IPdfWidgetType.DOUGHNUT_EVENTS_NOTIF_CALLBACK_SENT,
      width: 50
    },
    {
      type: IPdfWidgetType.TABLE_EVENTS_NOTIF_USAGE,
      width: 100
    }
  ]
};

const gsu: IDashboardConfig = {
  title: 'NAV.GSU',
  widgets: [
    {
      type: IPdfWidgetType.COUNTER_GSU_PSUS,
      width: 50
    },
    {
      type: IPdfWidgetType.COUNTER_GSU_TPPS,
      width: 50
    },
    {
      type: IPdfWidgetType.DOUGHNUT_GSU_OBRI_SESSIONS,
      width: 50
    },
    {
      type: IPdfWidgetType.DOUGHNUT_GSU_CALL_BY_STATUS,
      width: 50
    },
    {
      type: IPdfWidgetType.DOUGHNUT_GSU_OB_VERSIONS,
      width: 50
    },
    {
      type: IPdfWidgetType.DOUGHNUT_GSU_OB_API_TYPE,
      width: 50
    },
    {
      type: IPdfWidgetType.LINE_GSU_API_CALLS_PER_WEEK,
      width: 100
    },
    {
      type: IPdfWidgetType.LINE_GSU_API_RESPONSE_TIME,
      width: 100
    },
    {
      type: IPdfWidgetType.LINE_GSU_API_CALLS,
      width: 100
    }
  ]
};

export default {
  tpp,
  jwkms,
  directory,
  security,
  accounts,
  payments,
  funds,
  events,
  gsu
};
