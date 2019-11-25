import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';

import { SessionLostComponent } from './session-lost.component';
import { SessionLostRoutingModule } from './session-lost-routing.module';
import { ForgerockSharedModule } from 'ob-ui-libs/shared';

@NgModule({
  imports: [CommonModule, SessionLostRoutingModule, ForgerockSharedModule, MatButtonModule],
  declarations: [SessionLostComponent]
})
export class SessionLostModule {}
