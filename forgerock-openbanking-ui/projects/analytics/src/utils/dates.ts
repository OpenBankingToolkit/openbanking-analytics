import format from 'date-fns/format';
import subDays from 'date-fns/subDays';
import startOfDay from 'date-fns/startOfDay';
import endOfDay from 'date-fns/endOfDay';
import subWeeks from 'date-fns/subWeeks';
import startOfWeek from 'date-fns/startOfWeek';
import endOfWeek from 'date-fns/endOfWeek';
import startOfMonth from 'date-fns/startOfMonth';
import endOfMonth from 'date-fns/endOfMonth';
import subMonths from 'date-fns/subMonths';
import startOfQuarter from 'date-fns/startOfQuarter';
import endOfQuarter from 'date-fns/endOfQuarter';
import subQuarters from 'date-fns/subQuarters';
import startOfYear from 'date-fns/startOfYear';
import endOfYear from 'date-fns/endOfYear';
import subYears from 'date-fns/subYears';

import { IDatesPeriod } from '../models';

export const dateMetricsServerFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
export const dateUIFormat = 'MMM d, yyyy';

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
