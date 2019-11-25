import { environment as devDefaultEnv } from './environment.dev.default';

export const environment = {
  ...devDefaultEnv,
  production: false,
  nodeBackend: 'https://dev.analytics.ui-integ.forgerock.financial:4206/api',
  cookieDomain: '.ui-integ.forgerock.financial',
  metricsBackend: 'https://service.metrics.ui-integ.forgerock.financial'
  // routeDenyList: ['dashboard/tpp', 'dashboard/payments', 'settings']
};
