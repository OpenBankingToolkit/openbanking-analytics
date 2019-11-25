import { BrowserModule } from '@angular/platform-browser';
import { HttpClient } from '@angular/common/http';
import { NgModule, InjectionToken, APP_INITIALIZER } from '@angular/core';
import { StoreModule, ActionReducerMap } from '@ngrx/store';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';

import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { EffectsModule } from '@ngrx/effects';
import { CookieModule } from 'ngx-cookie';
import { ForgerockSharedModule } from 'ob-ui-libs/shared';
import { ForgerockConfigService } from 'ob-ui-libs/services/forgerock-config';
import { ForgerockConfigModule } from 'ob-ui-libs/services/forgerock-config';
import { AppComponent } from 'analytics/src/app/app.component';
import { TranslateSharedModule } from 'analytics/src/app/translate-shared.module';
import { AppRoutingModule } from 'analytics/src/app/app-routing.module';
import { environment } from 'analytics/src/environments/environment';
import rootReducer from 'analytics/src/store';
import { RootEffects } from 'analytics/src/store/effects';
import { ForgerockInterceptorProviders } from 'ob-ui-libs/interceptors';
import { ForgerockOIDCModule } from 'ob-ui-libs/oidc';

export const REDUCER_TOKEN = new InjectionToken<ActionReducerMap<{}>>('Registered Reducers');

export function getReducers() {
  return rootReducer;
}

export function createTranslateLoader(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

export function init_app(appConfig: ForgerockConfigService) {
  return () => appConfig.fetchAndMerge(environment);
}

export function createForgerockOIDCConfigFactory(config: ForgerockConfigService) {
  return {
    backendURL: config.get('metricsBackend')
  };
}

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    ForgerockSharedModule,
    ForgerockOIDCModule.forRoot(createForgerockOIDCConfigFactory),
    ForgerockConfigModule.forRoot(),
    CookieModule.forRoot(),
    TranslateSharedModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: createTranslateLoader,
        deps: [HttpClient]
      }
    }),
    // Store
    StoreModule.forRoot(REDUCER_TOKEN),
    EffectsModule.forRoot(RootEffects),
    environment.devModules || []
  ],
  providers: [
    {
      provide: REDUCER_TOKEN,
      deps: [],
      useFactory: getReducers
    },
    {
      provide: APP_INITIALIZER,
      useFactory: init_app,
      deps: [ForgerockConfigService],
      multi: true
    },
    ForgerockInterceptorProviders
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
