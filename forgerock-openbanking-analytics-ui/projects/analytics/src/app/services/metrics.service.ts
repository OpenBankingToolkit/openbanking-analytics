import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { ForgerockConfigService } from 'forgerock/src/app/services/forgerock-config/forgerock-config.service';
import {
  IActivityTypeEndpointParams,
  IChartsEndpointParams,
  IConsentTypeEndpointParams,
  IDirectoryCountersParams,
  IDoughnutResponse,
  ILineChartResponse,
  IPdfParams,
  ITableEndpointUsageRawParams,
  ITPPSEntriesResponse,
  ITPPSUsageAggregationResponse
} from 'analytics/src/models/metrics';
import { encodeQueryData } from '@utils/url';
import { IPdfReportConfig } from 'analytics/src/models';

@Injectable({
  providedIn: 'root'
})
export class MetricsService {
  constructor(private http: HttpClient, private conf: ForgerockConfigService) {}

  public getPdf(params: IPdfParams) {
    return this.http.get(
      `${this.conf.get('nodeBackend')}/pdf${encodeQueryData(params)}`,
      getHTTPOptions({
        responseType: 'blob'
      })
    );
  }
  public postPdf(body: IPdfReportConfig) {
    return this.http.post(
      `${this.conf.get('nodeBackend')}/pdf`,
      body,
      getHTTPOptions({
        responseType: 'blob'
      })
    );
  }
  public getTppsActivities(params: IChartsEndpointParams) {
    return this.http.get(
      `${this.conf.get('metricsBackend')}/api/kpi/tpps-activities/${encodeQueryData(params)}`,
      getHTTPOptions()
    );
  }
  public getTppsDirectories(params: IChartsEndpointParams) {
    return this.http.get(
      `${this.conf.get('metricsBackend')}/api/kpi/tpps/directories${encodeQueryData(params)}`,
      getHTTPOptions()
    );
  }
  public getConsentActivities(params: IActivityTypeEndpointParams) {
    return this.http.get(
      `${this.conf.get('metricsBackend')}/api/kpi/consent/activities${encodeQueryData(params)}`,
      getHTTPOptions()
    );
  }

  public getConsentType(params: IConsentTypeEndpointParams) {
    return this.http.get(
      `${this.conf.get('metricsBackend')}/api/kpi/consent/type${encodeQueryData(params)}`,
      getHTTPOptions()
    );
  }

  public getPaymentCOF(params: IChartsEndpointParams) {
    return this.http.get(
      `${this.conf.get('metricsBackend')}/api/kpi/payments/confirmation-of-fund${encodeQueryData(params)}`,
      getHTTPOptions()
    );
  }

  public getTokenUsage(params: IChartsEndpointParams) {
    return this.http.get(
      `${this.conf.get('metricsBackend')}/api/kpi/token-usage/${encodeQueryData(params)}`,
      getHTTPOptions()
    );
  }

  public getNbrOfPSUs(params: IChartsEndpointParams) {
    return this.http.get(
      `${this.conf.get('metricsBackend')}/api/kpi/psu/counter${encodeQueryData(params)}`,
      getHTTPOptions()
    );
  }

  public getNbrOfTPPs(params: IChartsEndpointParams) {
    return this.http.get(
      `${this.conf.get('metricsBackend')}/api/kpi/tpps/count${encodeQueryData(params)}`,
      getHTTPOptions()
    );
  }

  public getNbrOfOBRISessions(params: IChartsEndpointParams) {
    return this.http.get(
      `${this.conf.get('metricsBackend')}/api/kpi/session/count${encodeQueryData(params)}`,
      getHTTPOptions()
    );
  }

  public getDirectoriesCounters(params: IDirectoryCountersParams) {
    return this.http.get(
      `${this.conf.get('metricsBackend')}/api/kpi/directory/count${encodeQueryData(params)}`,
      getHTTPOptions()
    );
  }

  public getTppsTypes(params: IChartsEndpointParams) {
    return this.http.get<IDoughnutResponse>(
      `${this.conf.get('metricsBackend')}/api/kpi/tpps/type${encodeQueryData(params)}`,
      getHTTPOptions()
    );
  }

  public readStats(request) {
    return this.http.post<ILineChartResponse>(
      `${this.conf.get('metricsBackend')}/api/kpi/endpoint-usage/`,
      request,
      getHTTPOptions()
    );
  }

  public getTppsUsageAggregation(request) {
    return this.http.post<ITPPSUsageAggregationResponse>(
      `${this.conf.get('metricsBackend')}/api/kpi/endpoint-usage/aggregated`,
      request,
      getHTTPOptions()
    );
  }

  public getTppsEntries(params: ITableEndpointUsageRawParams) {
    return this.http.get<ITPPSEntriesResponse>(
      `${this.conf.get('metricsBackend')}/api/kpi/tpps/entries${encodeQueryData(params)}`,
      getHTTPOptions()
    );
  }

  public getEndpointUsageRawHistory(params: ITableEndpointUsageRawParams) {
    return this.http.get<ITPPSUsageAggregationResponse>(
      `${this.conf.get('metricsBackend')}/api/kpi/endpoint-usage/history${encodeQueryData(params)}`,
      getHTTPOptions()
    );
  }

  public deleteEndpointUsageRawHistory(params: IChartsEndpointParams) {
    return this.http.delete(
      `${this.conf.get('metricsBackend')}/api/kpi/endpoint-usage/history${encodeQueryData(params)}`,
      getHTTPOptions()
    );
  }

  public getEndpointUsageRawHistoryCSVUrl(params: IChartsEndpointParams) {
    return `${this.conf.get('metricsBackend')}/api/kpi/endpoint-usage/history/csv${encodeQueryData(params)}`;
  }

  public getTppEntriesCSVUrl(params: IChartsEndpointParams) {
    return `${this.conf.get('metricsBackend')}/api/kpi/tpps/entries/csv${encodeQueryData(params)}`;
  }

  //Events
  public getCallbacks(params: IChartsEndpointParams) {
    return this.http.get(
      `${this.conf.get('metricsBackend')}/api/kpi/callbacks/byResponseStatus${encodeQueryData(params)}`,
      getHTTPOptions()
    );
  }

  public createdCallbacks(params: IChartsEndpointParams) {
    return this.http.get(
      `${this.conf.get('metricsBackend')}/api/kpi/callbacks/created${encodeQueryData(params)}`,
      getHTTPOptions()
    );
  }

  public getJwtsGenerationKPI(params: IChartsEndpointParams) {
    return this.http.get(
      `${this.conf.get('metricsBackend')}/api/kpi/jwts/jwts-generation/${encodeQueryData(params)}`,
      getHTTPOptions()
    );
  }

  public getJwtsValidationKPI(params: IChartsEndpointParams) {
    return this.http.get(
      `${this.conf.get('metricsBackend')}/api/kpi/jwts/jwts-validation/${encodeQueryData(params)}`,
      getHTTPOptions()
    );
  }
}

export function getHTTPOptions(options?: any) {
  return {
    withCredentials: true,
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    }),
    ...options
  };
}
