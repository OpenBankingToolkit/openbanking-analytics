import { environment as devDefaultEnv } from './environment.dev.default';

export const environment = {
  ...devDefaultEnv,
  production: false,
  nodeBackend: 'https://analytics.dev-ob.forgerock.financial:5000/api',
  cookieDomain: '.dev-ob.forgerock.financial',
  //metricsBackend: 'https://service.metrics.dev-ob.forgerock.financial:9543'
  metricsBackend: 'https://service.metrics.dev-ob.forgerock.financial:8074'
  // routeDenyList: ['dashboard/tpp', 'dashboard/payments', 'settings']
};
