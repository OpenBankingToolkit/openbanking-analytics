import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-dashboard-toolbar',
  template: `
    <mat-toolbar>
      <span *ngIf="title">{{ title }}</span> <span class="separator"></span>
      <button *ngIf="pdfPath" mat-icon-button (click)="exportPDF($event)" [disabled]="isPdfLoading">
        <mat-progress-spinner
          *ngIf="isPdfLoading"
          diameter="30"
          color="accent"
          mode="indeterminate"
        ></mat-progress-spinner>
        <mat-icon *ngIf="!isPdfLoading" aria-label="Download PDF">picture_as_pdf</mat-icon>
      </button>
    </mat-toolbar>
  `,
  styles: [
    `
      :host {
        display: block;
        margin-bottom: 20px;
      }
      :host .separator {
        flex: 1;
      }
    `
  ]
})
export class AnalyticsDashboardToolbarComponent implements OnInit {
  @Input() title: '';
  @Input() pdfPath: '';
  @Input() isPdfLoading = false;
  @Output() pdfClick = new EventEmitter<Event>();

  constructor() {}

  ngOnInit() {}

  exportPDF(e: Event) {
    this.pdfClick.emit(e);
  }
}
