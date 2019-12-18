import { Action } from '@ngrx/store';
import { IPDFState, IState, IPdfReportConfig } from 'analytics/src/models';
import _get from 'lodash-es/get';

import { OIDCLogoutTypes } from '@forgerock/openbanking-ngx-common/oidc';
export const types = {
  PDF_GET_REQUEST: 'PDF_GET_REQUEST',
  PDF_GET_SUCCESS: 'PDF_GET_SUCCESS',
  PDF_GET_ERROR: 'PDF_GET_ERROR'
};

export class GetPdfRequestAction implements Action {
  readonly type = types.PDF_GET_REQUEST;
  constructor(public payload: { id: string; config?: IPdfReportConfig }) {}
}

export class GetPdfSuccessAction implements Action {
  readonly type = types.PDF_GET_SUCCESS;
  constructor(public payload: { id: string }) {}
}

export class GetPdfErrorAction implements Action {
  readonly type = types.PDF_GET_ERROR;
  constructor(public payload: { id: string }) {}
}

export type ActionsUnion = GetPdfRequestAction | GetPdfSuccessAction | GetPdfErrorAction;

export const DEFAULT_STATE: IPDFState = {};

export default function pdfReducer(state: IPDFState = DEFAULT_STATE, action: any): IPDFState {
  switch (action.type) {
    case types.PDF_GET_REQUEST: {
      return {
        ...state,
        [action.payload.id]: {
          isFetching: true
        }
      };
    }
    case types.PDF_GET_SUCCESS:
    case types.PDF_GET_ERROR: {
      return {
        ...state,
        [action.payload.id]: {
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

export const selectPdf = (state: IState, id: string) => state.pdf[id];
export const selectPdfIsFetching = (state: IState, id: string) => _get(state.pdf, `[${id}].isFetching`, false);
