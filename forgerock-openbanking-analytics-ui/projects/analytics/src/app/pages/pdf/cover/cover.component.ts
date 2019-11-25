import { Component, OnInit, ChangeDetectionStrategy, Input } from '@angular/core';
import * as format from 'date-fns/format';
import { formatDateUI } from 'analytics/src/utils/dates';

@Component({
  // tslint:disable-next-line
  selector: 'forgerock-pdf-cover',
  template: `
    <forgerock-pdf-page [orientation]="orientation">
      <div class="header"><forgerock-customer-logo></forgerock-customer-logo></div>
      <div class="content">
        <h1 *ngIf="title">{{ title }}</h1>
        <h3 *ngIf="period">{{ period }}</h3>
      </div>
      <div class="footer">{{ date }}</div>
    </forgerock-pdf-page>
  `,
  styleUrls: ['./cover.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PdfCoverComponent implements OnInit {
  @Input() orientation: 'landscape' | 'portrait' = 'portrait';
  @Input() title: '';
  @Input() from: '';
  @Input() to: '';
  period = '';
  date: string = format(Date.now(), 'YYYY-MM-DDT HH:mm:ss');

  constructor() {}

  ngOnInit() {
    this.period = `${formatDateUI(new Date(this.from))} - ${formatDateUI(new Date(this.to))}`;
  }
}
