import { Component, Inject } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Platform } from '@angular/cdk/platform';
import { getDatesFromPeriod } from 'analytics/src/utils/dates';
import { DatesService } from 'analytics/src/app/services/dates.service';

export interface DialogData {
  from: string;
  to: string;
}

@Component({
  // tslint:disable-next-line
  selector: 'dialog-overview-example-dialog',
  template: `
    <h1 mat-dialog-title>Select Period</h1>
    <div mat-dialog-content>
      <form [formGroup]="form">
        <mat-form-field>
          <mat-label>Apply Period</mat-label>
          <mat-select formControlName="period">
            <mat-option *ngFor="let period of periods" [value]="period.name">
              {{ period.text }}
            </mat-option>
          </mat-select>
        </mat-form-field>
        <mat-form-field>
          <input matInput [matDatepicker]="picker1" placeholder="From" formControlName="from" />
          <mat-datepicker-toggle matSuffix [for]="picker1">
            <mat-icon matDatepickerToggleIcon>calendar_today</mat-icon>
          </mat-datepicker-toggle>
          <mat-datepicker [touchUi]="isMobile" #picker1></mat-datepicker> </mat-form-field
        ><mat-form-field>
          <input matInput [matDatepicker]="picker2" placeholder="To" formControlName="to" />
          <mat-datepicker-toggle matSuffix [for]="picker2">
            <mat-icon matDatepickerToggleIcon>calendar_today</mat-icon>
          </mat-datepicker-toggle>
          <mat-datepicker [touchUi]="isMobile" #picker2></mat-datepicker>
        </mat-form-field>
      </form>
    </div>
    <div mat-dialog-actions style="justify-content: flex-end;">
      <button mat-button (click)="cancel()">Cancel</button>
      <button mat-raised-button color="accent" (click)="submit()" cdkFocusInitial>Ok</button>
    </div>
  `,
  styles: [
    `
      :host mat-form-field {
        width: 100%;
      }
    `
  ]
})
export class AnalyticsDatepickerModalComponent {
  form: FormGroup;
  isMobile: boolean = this._platform.ANDROID || this._platform.IOS;
  periods: { name: string; text: string }[];

  constructor(
    public dialogRef: MatDialogRef<AnalyticsDatepickerModalComponent>,
    private datesService: DatesService,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    private _platform: Platform
  ) {
    this.form = new FormGroup({
      period: new FormControl(''),
      from: new FormControl(new Date(data.from)),
      to: new FormControl(new Date(data.to))
    });

    this.periods = this.datesService.getPeriodOptions();

    this.form.valueChanges.subscribe(values => {
      if (values.period) {
        const [from, to] = getDatesFromPeriod(values.period);
        this.form.setValue({
          period: '',
          from,
          to
        });
      }
    });
  }

  cancel(): void {
    this.dialogRef.close();
  }

  submit() {
    this.dialogRef.close(this.form.value);
  }
}
