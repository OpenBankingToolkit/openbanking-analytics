import * as format from 'date-fns/format';
import * as subDays from 'date-fns/sub_days';
import * as startOfDay from 'date-fns/start_of_day';
import * as endOfDay from 'date-fns/end_of_day';
import * as subWeeks from 'date-fns/sub_weeks';
import * as startOfWeek from 'date-fns/start_of_week';
import * as endOfWeek from 'date-fns/end_of_week';
import * as startOfMonth from 'date-fns/start_of_month';
import * as endOfMonth from 'date-fns/end_of_month';
import * as subMonths from 'date-fns/sub_months';
import * as startOfQuarter from 'date-fns/start_of_quarter';
import * as endOfQuarter from 'date-fns/end_of_quarter';
import * as subQuarters from 'date-fns/sub_quarters';
import * as startOfYear from 'date-fns/start_of_year';
import * as endOfYear from 'date-fns/end_of_year';
import * as subYears from 'date-fns/sub_years';

import { IDatesPeriod } from '../models';

export const dateMetricsServerFormat = 'YYYY-MM-DDTHH:mm:ss.SSSZ';
export const dateUIFormat = 'MMM D, YYYY';

export function formatDateForMetricsServer(date: Date): string {
  return format(date, dateMetricsServerFormat);
}

export function formatDateUI(date: Date): string {
  return format(date, dateUIFormat);
}

export function getFormattedUIDatesFromPeriod(period: IDatesPeriod): string[] {
  const [from, to] = getDatesFromPeriod(period);
  return [formatDateUI(from), formatDateUI(to)];
}

export function getFormattedDatesFromPeriod(period: IDatesPeriod): string[] {
  const [from, to] = getDatesFromPeriod(period);
  return [formatDateForMetricsServer(from), formatDateForMetricsServer(to)];
}

export function getDatesFromPeriod(period: IDatesPeriod): Date[] {
  switch (period) {
    case IDatesPeriod.TODAY:
      return [startOfDay(new Date()), endOfDay(new Date())];
    case IDatesPeriod.YESTERDAY:
      return [startOfDay(subDays(new Date(), 1)), endOfDay(subDays(new Date(), 1))];
    case IDatesPeriod.LAST_3_DAYS:
      return [startOfDay(subDays(new Date(), 3)), endOfDay(new Date())];
    case IDatesPeriod.CURRENT_WEEK:
      return [startOfWeek(new Date()), endOfWeek(new Date())];
    case IDatesPeriod.LAST_WEEK:
      return [startOfWeek(subWeeks(new Date(), 1)), endOfWeek(subWeeks(new Date(), 1))];
    case IDatesPeriod.LAST_3_WEEKS:
      return [subWeeks(new Date(), 3), endOfWeek(new Date())];
    case IDatesPeriod.LAST_6_WEEKS:
      return [subWeeks(new Date(), 6), endOfWeek(new Date())];
    case IDatesPeriod.LAST_MONTH:
      return [startOfMonth(subMonths(new Date(), 1)), endOfMonth(subMonths(new Date(), 1))];
    case IDatesPeriod.LAST_3_MONTHS:
      return [startOfMonth(subMonths(new Date(), 3)), startOfMonth(new Date())];
    case IDatesPeriod.LAST_6_MONTHS:
      return [startOfMonth(subMonths(new Date(), 6)), startOfMonth(new Date())];
    case IDatesPeriod.CURRENT_QUARTER:
      return [startOfQuarter(new Date()), endOfQuarter(new Date())];
    case IDatesPeriod.QUARTER_MINUS_1:
      return [startOfQuarter(subQuarters(new Date(), 1)), endOfQuarter(subQuarters(new Date(), 1))];
    case IDatesPeriod.QUARTER_MINUS_2:
      return [startOfQuarter(subQuarters(new Date(), 2)), endOfQuarter(subQuarters(new Date(), 2))];
    case IDatesPeriod.QUARTER_MINUS_3:
      return [startOfQuarter(subQuarters(new Date(), 3)), endOfQuarter(subQuarters(new Date(), 3))];
    case IDatesPeriod.CURRENT_YEAR:
      return [startOfYear(new Date()), startOfMonth(new Date())];
    case IDatesPeriod.LAST_YEAR:
      return [startOfYear(subYears(new Date(), 1)), endOfYear(subYears(new Date(), 1))];

    default:
      throw new Error(`The period "${period}" is not implemented`);
  }
}
