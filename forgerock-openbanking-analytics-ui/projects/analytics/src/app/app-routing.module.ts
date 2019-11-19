import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ForgerockMainLayoutComponent } from 'forgerock/src/app/layouts/main-layout/main-layout.component';
import { SimpleLayoutComponent } from 'forgerock/src/app/layouts/simple/simple.component';
import { ForgerockSimpleLayoutModule } from 'forgerock/src/app/layouts/simple/simple.module';
import { ForgerockMainLayoutModule } from 'forgerock/src/app/layouts/main-layout/main-layout.module';
import {
  IForgerockMainLayoutConfig,
  IForgerockMainLayoutNavigations
} from 'forgerock/src/app/layouts/main-layout/models';
import { ForgerockPDFLayoutModule } from 'forgerock/src/app/layouts/pdf/pdf.module';
import { PDFLayoutComponent } from 'forgerock/src/app/layouts/pdf/pdf.component';
import { AnalyticsToolbarMenuComponentModule } from './components/toolbar-menu/toolbar-menu.module';
import { AnalyticsToolbarMenuContainer } from './components/toolbar-menu/toolbar-menu.container';
import { ForegerockGDPRConsentGuard } from 'forgerock/src/app/modules/gdpr/gdpr.guard.service';
import { ForgerockGDPRService } from 'forgerock/src/app/modules/gdpr/gdpr.service';
import { ForgerockCustomerCanAccessGuard } from 'forgerock/src/app/guards/can-customer-load';
import { ForgerockAuthRedirectOIDCComponent } from 'forgerock/src/app/modules/oidc/oidc.component';
import { IsConnectedGuard } from 'forgerock/src/app/modules/oidc/oidc.guard.service';

export const routes: Routes = [
  {
    path: 'pdf',
    component: PDFLayoutComponent,
    canLoad: [ForgerockCustomerCanAccessGuard],
    loadChildren: 'analytics/src/app/pages/pdf/pdf.module#AnalyticsPdfModule'
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
        loadChildren: () =>
          import('forgerock/src/app/components/forgerock-not-found/forgerock-not-found.module').then(
            m => m.ForgerockNotFoundModule
          )
      },
      {
        path: 'dev/info',
        pathMatch: 'full',
        loadChildren: () =>
          import('forgerock/src/app/components/forgerock-dev-info/forgerock-dev-info.module').then(
            m => m.ForgerockDevInfoModule
          )
      },
      {
        path: ForgerockGDPRService.gdprDeniedPage,
        loadChildren: () =>
          import('forgerock/src/app/modules/gdpr/page/gdpr.page.module').then(m => m.ForgerockGDPRPageModule)
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
        loadChildren: 'analytics/src/app/pages/dashboard/dashboard.module#AnalyticsDashboardModule',
        canActivate: [IsConnectedGuard]
      },
      {
        path: 'pdf-creator',
        loadChildren: 'analytics/src/app/pages/pdf-edition/pdf-edition.module#AnalyticsPdfEditionModule',
        canLoad: [ForgerockCustomerCanAccessGuard],
        canActivate: [IsConnectedGuard]
      },
      {
        path: 'session-lost',
        loadChildren: 'analytics/src/app/pages/session-lost/session-lost.module#SessionLostModule'
      },
      {
        path: 'settings',
        loadChildren: 'analytics/src/app/pages/settings/settings.module#SettingsModule',
        canLoad: [ForgerockCustomerCanAccessGuard],
        pathMatch: 'full',
        canActivate: [IsConnectedGuard]
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
