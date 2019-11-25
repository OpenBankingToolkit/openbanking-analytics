import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { IDatesPeriod } from 'analytics/src/models';
import * as getQuarter from 'date-fns/get_quarter';
import * as subQuarters from 'date-fns/sub_quarters';
import * as format from 'date-fns/format';
import { getFormattedUIDatesFromPeriod } from 'analytics/src/utils/dates';

@Injectable({
  providedIn: 'root'
})
export class DatesService {
  constructor(private translateService: TranslateService) {}

  getPeriodOptions() {
    const options: { name: string; text: string }[] = [];
    const now = new Date();

    Object.keys(IDatesPeriod).map((period: IDatesPeriod, index) => {
      let text;
      switch (period) {
        case IDatesPeriod.CURRENT_QUARTER:
          text = `Q${getQuarter(now)} ${format(now, 'YYYY')}`;
          break;
        case IDatesPeriod.QUARTER_MINUS_1: {
          const date = subQuarters(now, 1);
          text = `Q${getQuarter(date)} ${format(date, 'YYYY')}`;
          break;
        }
        case IDatesPeriod.QUARTER_MINUS_2: {
          const date = subQuarters(now, 2);
          text = `Q${getQuarter(date)} ${format(date, 'YYYY')}`;
          break;
        }
        case IDatesPeriod.QUARTER_MINUS_3: {
          const date = subQuarters(now, 3);
          text = `Q${getQuarter(date)} ${format(date, 'YYYY')}`;
          break;
        }

        default:
          text = this.translateService.instant(`PERIODS.${period}`);
          break;
      }
      const [from, to] = getFormattedUIDatesFromPeriod(period);
      options.push({
        name: period,
        text: `${text} (${from} - ${to})`
      });
    });
    return options;
  }
}
