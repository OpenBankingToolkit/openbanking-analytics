import { Component, Inject } from '@angular/core';
import { Platform } from '@angular/cdk/platform';
import { DOCUMENT } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';

import { ForgerockConfigService } from 'forgerock/src/app/services/forgerock-config/forgerock-config.service';
import { ForgerockSplashscreenService } from 'forgerock/src/app/services/forgerock-splashscreen/forgerock-splashscreen.service';
import { ForgerockGDPRService } from 'forgerock/src/app/modules/gdpr/gdpr.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  enableCustomization: string = this.configService.get('enableCustomization');

  constructor(
    @Inject(DOCUMENT) private document: any,
    private splashscreenService: ForgerockSplashscreenService,
    private configService: ForgerockConfigService,
    private platform: Platform,
    private translateService: TranslateService,
    private gdprService: ForgerockGDPRService
  ) {
    this.splashscreenService.init();
    this.gdprService.init();

    this.translateService.addLangs(['en', 'fr']);
    this.translateService.setDefaultLang('en');
    this.translateService.use(this.translateService.getBrowserLang() || 'en');

    // Add is-mobile class to the body if the platform is mobile
    if (this.platform.ANDROID || this.platform.IOS) {
      this.document.body.classList.add('is-mobile');
    }
  }
}
