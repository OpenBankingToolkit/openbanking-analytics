import { environment as devDefaultEnv } from './environment.dev.default';

export const environment = {
  ...devDefaultEnv,
  metricsBackend: 'https://service.metrics.dev-ob.forgerock.financial:8074',
  production: false
};
