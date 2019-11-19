import { Injectable } from '@angular/core';

import { ForgerockConfigService } from 'forgerock/src/app/services/forgerock-config/forgerock-config.service';
import defaultPdfsConfig from '../pages/pdf/dynamic/pdfs.config';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsPDFService {
  constructor(private config: ForgerockConfigService) {}

  getPdfConfig(name: string) {
    const config = this.config.get(`pdfs.${name}`) || defaultPdfsConfig[name];
    if (!config) {
      throw new Error('This PDF configuration does not exist');
    }
    return config;
  }
}
