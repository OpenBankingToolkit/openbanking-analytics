import { Component, HostBinding, Input, OnInit } from '@angular/core';

import { IPdfWidgetAttributes, IPdfWidgetType } from 'analytics/src/models/pdf';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-widget-card',
  template: `
    <mat-card>
      <mat-card-header *ngIf="title" title="{{ title | translate }}">
        <mat-card-title [translate]="title"></mat-card-title>
        <mat-card-actions align="end">
          <button *ngIf="!pdf" mat-icon-button (click)="showHideInfo()">
            <mat-icon class="info" aria-label="KPI info">info</mat-icon>
          </button>
        </mat-card-actions>
      </mat-card-header>
      <mat-card-content>
        <mat-expansion-panel class="widget-info" [expanded]="showInfo">
          <p [innerHTML]="info"></p>
        </mat-expansion-panel>
        <app-widget-dynamic [type]="type" [attributes]="attributes"></app-widget-dynamic>
      </mat-card-content>
    </mat-card>
  `,
  styleUrls: ['./widget-card.component.scss']
})
export class AnalyticsWidgetCardComponent implements OnInit {
  @Input() type: IPdfWidgetType;
  @Input() attributes: IPdfWidgetAttributes;
  @Input() title = '';
  @Input() info = '';
  @Input() showInfo = false;
  @HostBinding('class.pdf') @Input() pdf = false;

  constructor(private translateService: TranslateService) {}

  showHideInfo() {
    this.showInfo = !this.showInfo;
  }
  ngOnInit() {
    if (!this.title) {
      this.title = `WIDGETS.${this.type}.TITLE`;
    }
    if (!this.info) {
      this.info = this.translateService.instant(`WIDGETS.${this.type}.INFO`).replace(/\n/g, '<br />');
    }
  }
}
