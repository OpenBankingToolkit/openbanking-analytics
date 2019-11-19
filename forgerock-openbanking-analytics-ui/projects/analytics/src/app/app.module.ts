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
import { ForgerockSharedModule } from 'forgerock/src/app/forgerock-shared.module';
import {
  ForgerockCustomizationModule,
  ForgerockCustomization
} from 'forgerock/src/app/modules/customization/customization.module';
import { ForgerockConfigService } from 'forgerock/src/app/services/forgerock-config/forgerock-config.service';
import { ForgerockConfigModule } from 'forgerock/src/app/services/forgerock-config/forgerock-config.module';
import { AppComponent } from 'analytics/src/app/app.component';
import { TranslateSharedModule } from 'analytics/src/app/translate-shared.module';
// @ts-ignore
import cssVars from 'analytics/src/scss/cssvars.scss';
import { AppRoutingModule } from 'analytics/src/app/app-routing.module';
import { environment } from 'analytics/src/environments/environment';
import rootReducer from 'analytics/src/store';
import { RootEffects } from 'analytics/src/store/effects';
import { ForgerockInterceptorProviders } from 'forgerock/src/app/interceptors';
import { ForgerockOIDCModule } from 'forgerock/src/app/modules/oidc/oidc.module';

export const REDUCER_TOKEN = new InjectionToken<ActionReducerMap<{}>>('Registered Reducers');

export function getReducers() {
  return rootReducer;
}

export function ForgerockCustomizationFactory(): ForgerockCustomization {
  // /\*([^*]|[\r\n]|(\*+([^*/]|[\r\n])))*\*+/g
  // this removes the comments in the Sass file (only in dev, in prod there are no comments)
  // can't find a good wait to remove sourcemap in dev (since we can't eject Webpack config)
  // this removes the comments and select the CSS vars (way faster than just matching the CSS vars)
  return {
    cssVars: cssVars.replace(/\*([^*]|[\r\n]|(\*+([^*/]|[\r\n])))*\*+/g, '').match(/[^{\}]+(?=})/g)[0]
  };
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
    ForgerockCustomizationModule.forRoot(ForgerockCustomizationFactory),
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
