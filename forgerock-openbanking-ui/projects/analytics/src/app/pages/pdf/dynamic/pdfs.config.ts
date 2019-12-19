import { IPdfPageOrientation, IPdfPageType, IPdfReportConfig, IPdfWidgetType } from 'analytics/src/models/pdf';

export const tpp: IPdfReportConfig = {
  v: 1,
  pages: [
    {
      type: IPdfPageType.COVER,
      orientation: IPdfPageOrientation.PORTRAIT,
      title: 'TPPs report'
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_TPPS_ORIGIN
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.BAR_TPPS_ROLES
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.LINE_TPPS_REGISTRATION
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.TABLE_TPPS_ENTRIES,
          attributes: {
            perPage: '15'
          }
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.TABLE_TPPS_API_USAGE,
          attributes: {
            perPage: '15'
          }
        }
      ]
    }
  ]
};

export const jwkms: IPdfReportConfig = {
  v: 1,
  pages: [
    {
      type: IPdfPageType.COVER,
      orientation: IPdfPageOrientation.PORTRAIT,
      title: 'JWKMS report'
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.COUNTER_JWKMS_GENERATED
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.COUNTER_JWKMS_VALIDATION
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.TABLE_JWKMS_USAGE,
          attributes: {
            perPage: '15'
          }
        }
      ]
    }
  ]
};

export const directory: IPdfReportConfig = {
  v: 1,
  pages: [
    {
      type: IPdfPageType.COVER,
      orientation: IPdfPageOrientation.PORTRAIT,
      title: 'Directory report'
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.COUNTER_DIRECTORY_ORGANISATION_REGISTERED
        },
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.COUNTER_DIRECTORY_SOFTWARE_STATEMENTS_REGISTERED
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.COUNTER_DIRECTORY_SSA_GENERATED
        },
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.COUNTER_DIRECTORY_DOWLOADING_KEYS
        }
      ]
    }
  ]
};

export const security: IPdfReportConfig = {
  v: 1,
  pages: [
    {
      type: IPdfPageType.COVER,
      orientation: IPdfPageOrientation.PORTRAIT,
      title: 'Security report'
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.COUNTER_SECURITY_ACCESS_TOKEN_GENERATED
        },
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.COUNTER_SECURITY_ID_TOKEN_GENERATED
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.LINE_SECURITY_AUTHORISE_REQUESTS
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.LINE_SECURITY_ACCESS_TOKEN_REQUESTS
        }
      ]
    }
  ]
};

export const accounts: IPdfReportConfig = {
  v: 1,
  pages: [
    {
      type: IPdfPageType.COVER,
      orientation: IPdfPageOrientation.PORTRAIT,
      title: 'Accounts API report'
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_ACCOUNTS_OB_VERSIONS
        },
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_ACCOUNTS_CONSENT_TYPE
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_ACCOUNTS_FLOW_COMPLETED
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.TABLE_ACCOUNTS_USAGE,
          attributes: {
            perPage: '15'
          }
        }
      ]
    }
  ]
};

export const payments: IPdfReportConfig = {
  v: 1,
  pages: [
    {
      type: IPdfPageType.COVER,
      orientation: IPdfPageOrientation.PORTRAIT,
      title: 'Payments API report'
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_PAYMENTS_OB_VERSIONS
        },
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_PAYMENTS_CONSENT_TYPE
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_PAYMENTS_COF
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_PAYMENTS_DOMESTIC_CONSENTS_ACTIVITIES
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_PAYMENTS_DOMESTIC_SCHEDULED_CONSENTS_ACTIVITIES
        },
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_PAYMENTS_DOMESTIC_STANDING_ORDERS_CONSENTS_ACTIVITIES
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_PAYMENTS_INTERNATIONAL_CONSENTS_ACTIVITIES
        },
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_PAYMENTS_INTERNATIONAL_SCHEDULED_CONSENTS_ACTIVITIES
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_PAYMENTS_INTERNATIONAL_STANDING_ORDER_CONSENTS_ACTIVITIES
        },
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_PAYMENTS_FILE_CONSENTS_ACTIVITIES
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_PAYMENTS_SINGLE_CONSENTS_ACTIVITIES
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.TABLE_PAYMENTS_USAGE,
          attributes: {
            perPage: '15'
          }
        }
      ]
    }
  ]
};

export const funds: IPdfReportConfig = {
  v: 1,
  pages: [
    {
      type: IPdfPageType.COVER,
      orientation: IPdfPageOrientation.PORTRAIT,
      title: 'Confirmation of Funds APIs report'
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_CONFIRMATION_OF_FUNDS_OB_VERSIONS
        },
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_CONFIRMATION_OF_FUNDS_CONSENT_TYPE
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.TABLE_CONFIRMATION_OF_FUNDS_USAGE,
          attributes: {
            perPage: '15'
          }
        }
      ]
    }
  ]
};

export const events: IPdfReportConfig = {
  v: 1,
  pages: [
    {
      type: IPdfPageType.COVER,
      orientation: IPdfPageOrientation.PORTRAIT,
      title: 'Events notifications APIs report'
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_EVENTS_NOTIF_OB_VERSIONS
        },
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.COUNTER_EVENTS_NOTIF_FLOW_COMPLETED
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.TABLE_EVENTS_NOTIF_USAGE,
          attributes: {
            perPage: '15'
          }
        }
      ]
    }
  ]
};

export const gsu: IPdfReportConfig = {
  v: 1,
  pages: [
    {
      type: IPdfPageType.COVER,
      orientation: IPdfPageOrientation.PORTRAIT,
      title: 'General Service Usage report'
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.COUNTER_GSU_PSUS
        },
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.COUNTER_GSU_TPPS
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_GSU_OBRI_SESSIONS
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_GSU_CALL_BY_STATUS
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.PORTRAIT,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_GSU_OB_VERSIONS
        },
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.DOUGHNUT_GSU_OB_API_TYPE
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.LINE_GSU_API_CALLS_PER_WEEK
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.LINE_GSU_API_CALLS
        }
      ]
    }
  ]
};

export const tables: IPdfReportConfig = {
  v: 1,
  pages: [
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.TABLE_ACCOUNTS_USAGE,
          attributes: {
            perPage: 20,
            page: 1
          }
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.TABLE_CONFIRMATION_OF_FUNDS_USAGE
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.TABLE_EVENTS_NOTIF_USAGE
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.TABLE_JWKMS_USAGE
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.TABLE_PAYMENTS_USAGE
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.TABLE_SECURITY_USAGE
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.TABLE_TPPS_ENTRIES
        }
      ]
    },
    {
      type: IPdfPageType.PAGE,
      orientation: IPdfPageOrientation.LANDSCAPE,
      widgets: [
        {
          width: 100,
          info: true,
          type: IPdfWidgetType.TABLE_TPPS_API_USAGE
        }
      ]
    }
  ]
};

export const all: IPdfReportConfig = {
  v: 1,
  pages: [
    ...tpp.pages,
    ...jwkms.pages,
    ...directory.pages,
    ...security.pages,
    ...accounts.pages,
    ...payments.pages,
    ...funds.pages,
    ...events.pages,
    ...gsu.pages
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
  gsu,
  tables,
  all
};
