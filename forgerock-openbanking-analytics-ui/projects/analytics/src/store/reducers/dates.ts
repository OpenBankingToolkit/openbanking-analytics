import { Action } from '@ngrx/store';
import { IDatesState, IState, IDatesPeriod } from 'analytics/src/models';
import _get from 'lodash-es/get';

import { types as logoutTypes } from 'forgerock/src/app/modules/authentication/store/reducers/logout';
import { getFormattedDatesFromPeriod } from 'analytics/src/utils/dates';

export const types = {
  DATES_SET: 'DATES_SET'
};

export class SetDatesAction implements Action {
  readonly type = types.DATES_SET;
  constructor(public payload: { from: string; to: string }) {}
}

export type ActionsUnion = SetDatesAction;

const [from, to] = getFormattedDatesFromPeriod(IDatesPeriod.LAST_3_WEEKS);

export const DEFAULT_STATE: IDatesState = {
  from,
  to
};

export default function pdfReducer(state: IDatesState = DEFAULT_STATE, action: any): IDatesState {
  switch (action.type) {
    case types.DATES_SET: {
      return { from: action.payload.from, to: action.payload.to };
    }
    case logoutTypes.LOGOUT_ERROR:
    case logoutTypes.LOGOUT_SUCCESS: {
      return DEFAULT_STATE;
    }
    default:
      return state;
  }
}

export const selectDates = (state: IState) => state.dates;
export const selectDateFrom = (state: IState) => state.dates.from;
export const selectDateTo = (state: IState) => state.dates.to;
