import { Action, createSelector } from '@ngrx/store';
import { IState, IWidgetTableState, IWidgetTable } from 'analytics/src/models';
import _get from 'lodash-es/get';
import _set from 'lodash-es/set';
import _uniq from 'lodash-es/uniq';
import _findIndex from 'lodash-es/findIndex';

import { OIDCLogoutTypes } from '@forgerock/openbanking-ngx-common/oidc';
import { ITableReponseUnion } from 'analytics/src/models/metrics';
import { types as datesTypes } from './dates';
import { ITableFilter, ITableSort, ITableSortList, ITableFilterList } from 'analytics/src/models';

export const DEFAULT_SIZE = 10;

export const types = {
  TABLE_GET_REQUEST: 'TABLE_GET_REQUEST',
  TABLE_GET_SUCCESS: 'TABLE_GET_SUCCESS',
  TABLE_GET_ERROR: 'TABLE_GET_ERROR',
  TABLE_GET_PAGE_CHANGE: 'TABLE_GET_PAGE_CHANGE',
  TABLE_GET_SORT_CHANGE: 'TABLE_GET_SORT_CHANGE',
  TABLE_GET_FILTER_CHANGE: 'TABLE_GET_FILTER_CHANGE',
  TABLE_GET_SORTS_CHANGE: 'TABLE_GET_SORTS_CHANGE',
  TABLE_GET_FILTERS_CHANGE: 'TABLE_GET_FILTERS_CHANGE',
  TABLE_GET_FINISHED: 'TABLE_GET_FINISHED'
};

export class GetTableRequestAction implements Action {
  readonly type = types.TABLE_GET_REQUEST;
  constructor(public payload: { id: string }) {}
}

export class GetTableSuccessAction implements Action {
  readonly type = types.TABLE_GET_SUCCESS;
  constructor(public payload: { id: string; response: ITableReponseUnion }) {}
}

export class GetTableErrorAction implements Action {
  readonly type = types.TABLE_GET_ERROR;
  constructor(public payload: { id: string; error: string }) {}
}

export class GetTablePageChangeAction implements Action {
  readonly type = types.TABLE_GET_PAGE_CHANGE;
  constructor(public payload: { id: string; page: number; size: number }) {}
}

export class GetTableSortChangeAction implements Action {
  readonly type = types.TABLE_GET_SORT_CHANGE;
  constructor(public payload: { id: string; sort: ITableSort }) {}
}

export class GetTableFilterChangeAction implements Action {
  readonly type = types.TABLE_GET_FILTER_CHANGE;
  constructor(public payload: { id: string; filter: ITableFilter }) {}
}

export class GetTableSortsChangeAction implements Action {
  readonly type = types.TABLE_GET_SORTS_CHANGE;
  constructor(public payload: { id: string; sorts: ITableSortList }) {}
}

export class GetTableFiltersChangeAction implements Action {
  readonly type = types.TABLE_GET_FILTERS_CHANGE;
  constructor(public payload: { id: string; filters: ITableFilterList }) {}
}

export class GetTableFinishedAction implements Action {
  readonly type = types.TABLE_GET_FINISHED;
  constructor(public payload: { id: string }) {}
}

export type TableActionsUnion =
  | GetTableRequestAction
  | GetTableSuccessAction
  | GetTableErrorAction
  | GetTablePageChangeAction
  | GetTableFilterChangeAction
  | GetTableSortChangeAction
  | GetTableFiltersChangeAction
  | GetTableSortsChangeAction;

export const DEFAULT_STATE: IWidgetTableState = {};
export const DEFAULT_TABLE: IWidgetTable = {
  isFetching: false,
  pages: [],
  size: DEFAULT_SIZE,
  currentPage: 0,
  totalPages: 0,
  totalResults: 0,
  error: '',
  sorts: [],
  filters: []
};

export default function tableReducer(state: IWidgetTableState = DEFAULT_STATE, action: any): IWidgetTableState {
  switch (action.type) {
    case datesTypes.DATES_SET: {
      return Object.keys(state).reduce((acc: IWidgetTableState, current: string) => {
        const table = <IWidgetTable>state[current];
        acc[current] = {
          ...table, // keep sorts, filters and size
          isFetching: false,
          error: '',
          pages: [],
          currentPage: 0,
          totalPages: 0,
          totalResults: 0
        };
        return acc;
      }, {});
    }
    case types.TABLE_GET_REQUEST: {
      const { id } = action.payload;
      return {
        ...state,
        [id]: {
          ...state[id],
          isFetching: true,
          error: ''
        }
      };
    }
    case types.TABLE_GET_SUCCESS: {
      const {
        id,
        response: { totalPages, totalResults, currentPage, data }
      } = action.payload;
      const pointer = state[id];
      const resetData = pointer.totalResults !== totalResults;
      const pages = resetData ? [] : [...state[id].pages];
      pages[currentPage] = data;

      return {
        ...state,
        [id]: {
          ...state[id],
          isFetching: false,
          totalPages,
          totalResults,
          currentPage,
          pages
        }
      };
    }
    case types.TABLE_GET_ERROR: {
      const { id, error } = action.payload;
      return {
        ...state,
        [action.payload.id]: {
          ...state[id],
          isFetching: false,
          error
        }
      };
    }
    case types.TABLE_GET_PAGE_CHANGE: {
      const { id, page, size } = action.payload;
      const currentSize = _get(state, `[${id}].size`);
      if (currentSize && currentSize !== size) {
        return {
          ...state,
          [id]: {
            ...state[id],
            isFetching: false,
            error: '',
            currentPage: 0,
            size,
            pages: []
          }
        };
      }
      return {
        ...state,
        [id]: {
          ...state[id],
          currentPage: page,
          size
        }
      };
    }
    case types.TABLE_GET_SORT_CHANGE: {
      const { id, sort } = <{ id: string; sort: ITableSort }>action.payload;
      const { direction, field } = _get(state, `[${id}].sorts[0]`, []);

      // prevent reloading when exact same sort
      if (sort.direction === direction && sort.field === field) {
        return state;
      }

      return {
        ...state,
        [id]: {
          ...state[id],
          sorts: sort.direction === '' ? [] : [sort],
          pages: []
        }
      };
    }
    case types.TABLE_GET_SORTS_CHANGE: {
      const { id, sorts } = <{ id: string; sorts: ITableSortList }>action.payload;
      return {
        ...state,
        [id]: {
          ...state[id],
          sorts
        }
      };
    }
    case types.TABLE_GET_FILTER_CHANGE: {
      const { id, filter } = <{ id: string; filter: ITableFilter }>action.payload;
      const filters = _get(state, `[${id}].filters`, []);
      const filterIndex = _findIndex(filters, ['field', filter.field]);
      if (filterIndex > -1) {
        if (filter.regex) {
          filters[filterIndex] = filter;
        } else {
          filters.splice(filterIndex, 1);
        }
      } else {
        filters.push(filter);
      }

      return {
        ...state,
        [id]: {
          ...state[id],
          currentPage: 0,
          filters: [...filters],
          pages: []
        }
      };
    }
    case types.TABLE_GET_FILTERS_CHANGE: {
      const { id, filters } = <{ id: string; filters: ITableFilterList }>action.payload;
      return {
        ...state,
        [id]: {
          ...state[id],
          filters
        }
      };
    }
    case types.TABLE_GET_FINISHED: {
      return {
        ...state,
        [action.payload.id]: {
          ...state[action.payload.id],
          isFetching: false
        }
      };
    }
    case OIDCLogoutTypes.LOGOUT_ERROR:
    case OIDCLogoutTypes.LOGOUT_SUCCESS: {
      return DEFAULT_STATE;
    }
    default:
      return state;
  }
}

export const selectTable = (state: IState, id: string) => state.table[id];
export const selectIsTableExists = (state: IState, id: string) => state.table[id] !== undefined;
export const selectTableIsFetching = (state: IState, id: string) => _get(state.table, `[${id}].isFetching`, false);
export const selectTableSize = (state: IState, id: string) => _get(state.table, `[${id}].size`, DEFAULT_SIZE);
export const selectTableCurrentPage = (state: IState, id: string) => _get(state.table, `[${id}].currentPage`, 0);
export const selectTableSorts = (state: IState, id: string) => _get(state.table, `[${id}].sorts`, []);
export const selectTableFilters = (state: IState, id: string) => _get(state.table, `[${id}].filters`, []);
export const selectTableTotalPages = (state: IState, id: string) => _get(state.table, `[${id}].totalPages`);
export const selectTablePages = (state: IState, id: string) => _get(state.table, `[${id}].pages`, []);
export const selectTableLength = (state: IState, id: string) => _get(state.table, `[${id}].totalResults`);
export const selectTableError = (state: IState, id: string) => _get(state.table, `[${id}].error`);

export const selectTablePageData = createSelector(
  selectTablePages,
  selectTableCurrentPage,
  (pages: any[], currentPage: number) => pages[currentPage] || []
);

export const selectTableShouldFetch = createSelector(
  selectTableIsFetching,
  selectTablePageData,
  (isFetching, data) => !isFetching && !data.length
);
