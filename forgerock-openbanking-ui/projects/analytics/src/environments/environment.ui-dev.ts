import { environment as devDefaultEnv } from './environment.dev.default';

export const environment = {
  ...devDefaultEnv,
  production: false,
  nodeBackend: 'https://dev.analytics.ui-dev.forgerock.financial:4206/api',
  cookieDomain: '.ui-dev.forgerock.financial',
  metricsBackend: 'https://service.metrics.ui-dev.forgerock.financial'
};
