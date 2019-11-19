import { Action, createSelector } from '@ngrx/store';
import { IChartsState, IState } from 'analytics/src/models';
// tslint:disable-next-line
import { Chart } from 'chart.js';
import _get from 'lodash-es/get';

import { types as logoutTypes } from 'forgerock/src/app/modules/authentication/store/reducers/logout';
import { types as datesTypes, selectDateFrom, selectDateTo } from './dates';

export const types = {
  CHART_GET_REQUEST: 'CHART_GET_REQUEST',
  CHART_GET_SUCCESS: 'CHART_GET_SUCCESS',
  CHART_GET_ERROR: 'CHART_GET_ERROR',
  CHART_GET_FINISHED: 'CHART_GET_FINISHED'
};

export class GetChartRequestAction implements Action {
  readonly type = types.CHART_GET_REQUEST;
  constructor(public payload: { id: string }) {}
}

export class GetChartSuccessAction implements Action {
  readonly type = types.CHART_GET_SUCCESS;
  constructor(public payload: { id: string; config: Chart.ChartConfiguration }) {}
}

export class GetChartErrorAction implements Action {
  readonly type = types.CHART_GET_ERROR;
  constructor(public payload: { id: string; error: string }) {}
}

export class GetChartFinishedAction implements Action {
  readonly type = types.CHART_GET_FINISHED;
  constructor(public payload: { id: string }) {}
}

export type ActionsUnion = GetChartRequestAction | GetChartSuccessAction | GetChartErrorAction | GetChartFinishedAction;

export const DEFAULT_STATE: IChartsState = {};

export default function chartsReducer(state: IChartsState = DEFAULT_STATE, action: any): IChartsState {
  switch (action.type) {
    case types.CHART_GET_REQUEST: {
      return {
        ...state,
        [action.payload.id]: {
          isFetching: true
        }
      };
    }
    case types.CHART_GET_SUCCESS: {
      return {
        ...state,
        [action.payload.id]: {
          isFetching: false,
          config: action.payload.config
        }
      };
    }
    case types.CHART_GET_ERROR: {
      return {
        ...state,
        [action.payload.id]: {
          isFetching: false,
          error: action.payload.error
        }
      };
    }
    case types.CHART_GET_FINISHED: {
      return {
        ...state,
        [action.payload.id]: {
          ...state[action.payload.id],
          isFetching: false
        }
      };
    }
    case datesTypes.DATES_SET:
    case logoutTypes.LOGOUT_ERROR:
    case logoutTypes.LOGOUT_SUCCESS: {
      return DEFAULT_STATE;
    }
    default:
      return state;
  }
}

export const selectChart = (state: IState, id: string) => state.charts[id];
export const selectChartIsFetching = (state: IState, id: string) => _get(state.charts, `[${id}].isFetching`, false);
export const selectChartConfig = (state: IState, id: string) => _get(state.charts, `[${id}].config`);
export const selectChartError = (state: IState, id: string) => _get(state.charts, `[${id}].error`);

export const selectChartShouldFetch = createSelector(
  selectChartIsFetching,
  selectChartConfig,
  selectDateFrom,
  selectDateTo,
  (isFetching, data) => !isFetching && !data
);
