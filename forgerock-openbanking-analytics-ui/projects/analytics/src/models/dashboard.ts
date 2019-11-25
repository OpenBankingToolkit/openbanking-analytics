import { IPdfWidgetType } from './pdf';

export interface IDashboardWidget {
  type: IPdfWidgetType;
  width: number;
  offset?: number;
}

export interface IDashboardConfig {
  title: string;
  widgets: IDashboardWidget[];
}
