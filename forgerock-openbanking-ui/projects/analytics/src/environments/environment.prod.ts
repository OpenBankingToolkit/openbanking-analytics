import { environment as defaultEnv } from './environment.default';

export const environment = {
  ...defaultEnv,
  production: true,
  nodeBackend: 'https://node.analytics.ob.forgerock.financial',
  cookieDomain: '.ob.forgerock.financial',
  metricsBackend: 'https://service.metrics.ob.forgerock.financial'
};
