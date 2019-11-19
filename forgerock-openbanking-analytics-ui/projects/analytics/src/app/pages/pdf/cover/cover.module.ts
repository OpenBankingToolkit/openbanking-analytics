import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PdfCoverComponent } from './cover.component';
import { PdfPageModule } from '../page/page.module';
import { ForgerockCustomerLogoModule } from 'forgerock/src/app/components/forgerock-customer-logo/forgerock-customer-logo.module';

@NgModule({
  declarations: [PdfCoverComponent],
  exports: [PdfCoverComponent],
  imports: [CommonModule, PdfPageModule, ForgerockCustomerLogoModule]
})
export class PdfCoverModule {}
