// tslint:disable-next-line
import { Chart } from 'chart.js';
import { ITableFilterList, ITableSortList } from './metrics';
import { IOIDCState } from 'ob-ui-libs/oidc';

export interface IChart {
  isFetching: boolean;
  data: Chart.ChartData;
  error?: string;
}

export interface IChartsState {
  [id: string]: IChart;
}

export interface IWidgetTable {
  isFetching: boolean;
  pages: any[];
  size: number;
  currentPage: number;
  totalPages: number;
  totalResults: number;
  error?: string;
  sorts: ITableSortList;
  filters: ITableFilterList;
}

export interface IWidgetTableState {
  [id: string]: IWidgetTable;
}

export interface IPDFItem {
  isFetching: boolean;
}

export interface IPDFState {
  [id: string]: IPDFItem;
}

export interface IDatesState {
  from: string;
  to: string;
}

export interface IState {
  oidc: IOIDCState;
  charts: IChartsState;
  table: IWidgetTableState;
  pdf: IWidgetTableState;
  dates: IDatesState;
}
