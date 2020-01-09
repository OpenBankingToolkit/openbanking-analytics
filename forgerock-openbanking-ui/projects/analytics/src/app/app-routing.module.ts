import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import {
  ForgerockMainLayoutComponent,
  ForgerockMainLayoutModule,
  IForgerockMainLayoutConfig,
  IForgerockMainLayoutNavigations
} from '@forgerock/openbanking-ngx-common/layouts/main-layout';
import { ForgerockPDFLayoutModule, PDFLayoutComponent } from '@forgerock/openbanking-ngx-common/layouts/pdf';
import { AnalyticsToolbarMenuComponentModule } from './components/toolbar-menu/toolbar-menu.module';
import { AnalyticsToolbarMenuContainer } from './components/toolbar-menu/toolbar-menu.container';
import { ForgerockCustomerCanAccessGuard } from '@forgerock/openbanking-ngx-common/guards';
import { ForgerockAuthRedirectOIDCComponent, IsOIDCConnectedGuard } from '@forgerock/openbanking-ngx-common/oidc';
import { ForgerockGDPRService, ForegerockGDPRConsentGuard } from '@forgerock/openbanking-ngx-common/gdpr';
import { ForgerockSimpleLayoutModule, SimpleLayoutComponent } from '@forgerock/openbanking-ngx-common/layouts/simple';

export const routes: Routes = [
  {
    path: 'pdf',
    component: PDFLayoutComponent,
    canLoad: [ForgerockCustomerCanAccessGuard],
    loadChildren: () => import('analytics/src/app/pages/pdf/pdf.module').then(m => m.AnalyticsPdfModule)
  },
  {
    path: 'redirectOpenId',
    component: ForgerockAuthRedirectOIDCComponent
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard/gsu'
  },
  {
    path: '',
    component: SimpleLayoutComponent,
    children: [
      {
        path: '404',
        pathMatch: 'full',
        loadChildren: () =>
          import('forgerock/src/ob-ui-libs-lazy/not-found.module').then(m => m.OBUILibsLazyNotFoundPage)
      },
      {
        path: 'dev/info',
        pathMatch: 'full',
        loadChildren: () => import('forgerock/src/ob-ui-libs-lazy/dev-info.module').then(m => m.OBUILibsLazyDevInfoPage)
      },
      {
        path: ForgerockGDPRService.gdprDeniedPage,
        loadChildren: () => import('forgerock/src/ob-ui-libs-lazy/gdpr.module').then(m => m.OBUILibsLazyGDPRPage)
      }
    ]
  },
  {
    path: '',
    component: ForgerockMainLayoutComponent,
    canActivate: [ForegerockGDPRConsentGuard],
    children: [
      {
        path: 'dashboard',
        loadChildren: () =>
          import('analytics/src/app/pages/dashboard/dashboard.module').then(m => m.AnalyticsDashboardModule),
        canActivate: [IsOIDCConnectedGuard]
      },
      {
        path: 'pdf-creator',
        loadChildren: () =>
          import('analytics/src/app/pages/pdf-edition/pdf-edition.module').then(m => m.AnalyticsPdfEditionModule),
        canLoad: [ForgerockCustomerCanAccessGuard],
        canActivate: [IsOIDCConnectedGuard]
      },
      {
        path: 'session-lost',
        loadChildren: () =>
          import('analytics/src/app/pages/session-lost/session-lost.module').then(m => m.SessionLostModule)
      },
      {
        path: 'settings',
        loadChildren: () => import('analytics/src/app/pages/settings/settings.module').then(m => m.SettingsModule),
        canLoad: [ForgerockCustomerCanAccessGuard],
        pathMatch: 'full',
        canActivate: [IsOIDCConnectedGuard]
      }
    ]
  },
  {
    path: '**',
    pathMatch: 'full',
    redirectTo: '404'
  }
];

// const mainLayoutConfig: IForgerockMainLayoutConfig = {
//     style: 'vertical-layout-1',
//     navbar: {
//       folded: false,
//       hidden: false,
//       position: 'left'
//     },
//     toolbar: {
//       hidden: false
//     },
//     footer: {
//       hidden: false,
//       position: 'below-static'
//     }
// };

const mainLayoutConfig: IForgerockMainLayoutConfig = {
  style: 'horizontal-layout-1',
  width: 1500,
  navbar: {
    folded: false,
    hidden: false,
    position: 'left'
  },
  toolbar: {
    hidden: false
  },
  footer: {
    hidden: true,
    position: 'above-fixed'
  }
};

export const navigations: IForgerockMainLayoutNavigations = {
  main: [
    {
      id: 'gsu',
      translate: 'NAV.GSU',
      type: 'item',

      url: '/dashboard/gsu'
    },
    {
      id: 'tpp',
      translate: 'NAV.TPP',
      type: 'item',
      url: '/dashboard/tpp'
    },
    {
      id: 'security',
      translate: 'NAV.SECURITY',
      type: 'item',
      url: '/dashboard/security'
    },
    {
      id: 'admin',
      translate: 'NAV.READ_WRITE_API',
      icon: 'keyboard_arrow_down',
      type: 'group',
      children: [
        {
          id: 'accounts',
          translate: 'NAV.ACCOUNTS',
          type: 'item',
          url: '/dashboard/accounts'
        },
        {
          id: 'payments',
          translate: 'NAV.PAYMENTS',
          type: 'item',
          url: '/dashboard/payments'
        },
        {
          id: 'funds',
          translate: 'NAV.FUNDS_CONFIRMATION',
          type: 'item',
          url: '/dashboard/funds'
        },
        {
          id: 'events',
          translate: 'NAV.EVENTS_NOTIFICATIONS',
          type: 'item',
          url: '/dashboard/events'
        }
      ]
    },
    {
      id: 'directory',
      translate: 'NAV.DIRECTORY',
      type: 'item',
      url: '/dashboard/directory'
    },
    {
      id: 'jwkms',
      translate: 'NAV.JWKMS',
      type: 'item',
      url: '/dashboard/jwkms'
    }
  ]
};

@NgModule({
  imports: [
    AnalyticsToolbarMenuComponentModule,
    ForgerockMainLayoutModule.forRoot({
      layout: mainLayoutConfig,
      navigations,
      components: {
        toolbar: AnalyticsToolbarMenuContainer
      }
    }),
    ForgerockSimpleLayoutModule,
    ForgerockPDFLayoutModule,
    RouterModule.forRoot(routes)
  ],
  exports: [ForgerockMainLayoutModule, ForgerockSimpleLayoutModule, ForgerockPDFLayoutModule, RouterModule]
})
export class AppRoutingModule {}
