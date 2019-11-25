import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PdfPageComponent } from './page.component';

@NgModule({
  declarations: [PdfPageComponent],
  exports: [PdfPageComponent],
  imports: [CommonModule]
})
export class PdfPageModule {}
