import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { AnalyticsDatepickerModalComponent } from './date-picker-modal.component';
import _get from 'lodash-es/get';

import { formatDateUI } from 'analytics/src/utils/dates';

@Component({
  // tslint:disable-next-line
  selector: 'analytics-datepicker-menu',
  template: `
    <button mat-button aria-label="Select Period" (click)="openDialog()">
      <span class="dates" fxHide fxShow.gt-sm>{{ dates }}</span>
      <mat-icon>calendar_today</mat-icon>
    </button>
  `,
  styles: [
    `
      :host .dates {
        margin-right: 5px;
      }
    `
  ]
})
export class AnalyticsDatepickerComponent implements OnInit, OnChanges {
  @Input() from: string;
  @Input() to: string;
  @Output() change = new EventEmitter<{ from: Date; to: Date }>();
  dates: string;

  form: FormGroup;
  serializedDate = new FormControl(new Date().toISOString());

  constructor(public dialog: MatDialog) {}

  ngOnInit(): void {}

  ngOnChanges(changes: SimpleChanges): void {
    const from = formatDateUI(new Date(_get(changes, 'from.currentValue', this.from)));
    const to = formatDateUI(new Date(_get(changes, 'to.currentValue', this.to)));
    this.dates = `${from} - ${to}`;
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(AnalyticsDatepickerModalComponent, {
      width: '350px',
      data: { from: this.from, to: this.to }
    });

    dialogRef.afterClosed().subscribe((result: { from: Date; to: Date }) => {
      if (result) {
        this.change.emit(result);
      }
    });
  }
}
