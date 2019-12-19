import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDialogModule } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { TranslateModule } from '@ngx-translate/core';
import { FlexLayoutModule } from '@angular/flex-layout';

import { AnalyticsToolbarMenuComponent } from './toolbar-menu.component';
import { AnalyticsToolbarMenuContainer } from './toolbar-menu.container';
import { AnalyticsDatepickerComponent } from './date-picker.component/date-picker.component';
import { AnalyticsDatepickerModalComponent } from './date-picker.component/date-picker-modal.component';

const declarations = [
  AnalyticsToolbarMenuComponent,
  AnalyticsToolbarMenuContainer,
  AnalyticsDatepickerComponent,
  AnalyticsDatepickerModalComponent
];

@NgModule({
  declarations,
  entryComponents: declarations,
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatDialogModule,
    MatSelectModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
    MatMenuModule,
    TranslateModule,
    FlexLayoutModule
  ],
  exports: declarations
})
export class AnalyticsToolbarMenuComponentModule {}
