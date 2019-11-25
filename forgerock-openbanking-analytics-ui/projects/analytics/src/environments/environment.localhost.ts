import { environment as devDefaultEnv } from './environment.dev.default';

export const environment = {
  ...devDefaultEnv,
  production: false,
  nodeBackend: 'http://localhost:5000/api',
  cookieDomain: '.localhost',
  metricsBackend: 'http://localhost:9443'
  // routeDenyList: ['dashboard/tpp', 'dashboard/payments', 'settings']
};
