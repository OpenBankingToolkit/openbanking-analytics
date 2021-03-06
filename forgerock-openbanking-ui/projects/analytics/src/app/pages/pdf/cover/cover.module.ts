import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PdfCoverComponent } from './cover.component';
import { PdfPageModule } from '../page/page.module';
import { ForgerockCustomerLogoModule } from '@forgerock/openbanking-ngx-common/components/forgerock-customer-logo';

@NgModule({
  declarations: [PdfCoverComponent],
  exports: [PdfCoverComponent],
  imports: [CommonModule, PdfPageModule, ForgerockCustomerLogoModule]
})
export class PdfCoverModule {}
