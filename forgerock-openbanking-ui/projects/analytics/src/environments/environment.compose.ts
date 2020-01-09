import { environment as devDefaultEnv } from './environment.dev.default';

export const environment = {
  ...devDefaultEnv,
  production: false,
  nodeBackend: 'http://localhost:4206/api',
  cookieDomain: '.localhost',
  metricsBackend: 'http://localhost:9443',
  metricsBackendHeadlessChrome: 'http://spring:9443'
  // routeDenyList: ['dashboard/tpp', 'dashboard/payments', 'settings']
};
