import { Component, OnInit, ChangeDetectionStrategy, Input, HostBinding } from '@angular/core';

@Component({
  // tslint:disable-next-line
  selector: 'forgerock-pdf-page',
  template: `
    <ng-content> </ng-content>
  `,
  styles: [
    `
      :host {
        background-color: white;
        position: relative;
        padding: 0.6in;
        display: block;
        overflow: hidden;
      }
      :host:last-child {
        page-break-after: avoid;
      }
      :host.LANDSCAPE {
        width: 11.685in;
        height: 8.25in;
      }
      :host.PORTRAIT {
        width: 8.25in;
        height: 11.685in;
        page-break-after: always;
      }
    `
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PdfPageComponent implements OnInit {
  @HostBinding('class') @Input() orientation: 'LANDSCAPE' | 'PORTRAIT' = 'PORTRAIT';

  constructor() {}

  ngOnInit() {}
}
