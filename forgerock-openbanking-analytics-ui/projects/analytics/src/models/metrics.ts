export interface IDoughnutResponse {
  data: {
    label: string;
    value: string;
  }[];
  total: number;
}

export interface ICounterResponse {
  counter: number;
}

export interface ILineChartResponse {
  endpointName: string;
  dateGranularity: string;
  lines: {
    yAxisID: string;
    name: string;
    dataset: number[];
  }[];
  definition: {
    BY_DATE: string[];
    BY_WEEK_DAY: string[];
    BY_OB_VERSIONS: string[];
    BY_OB_TYPE: string[];
    BY_TPP: string[];
    BY_RESPONSE_STATUS: string[];
    BY_RESPONSE_TIME: string[];
  };
}

export enum EndpointType {
  ACCOUNTS = 'AISP',
  PAYMENTS = 'PISP',
  CONFIRMATION_OF_FOUNDS = 'CBPII',
  EVENT_NOTIFICATIONS = 'EVENT',
  JWKMS = 'JWKMS'
}

export enum UserType {
  OIDC_CLIENT = 'OIDC_CLIENT',
  SOFTWARE_STATEMENT = 'SOFTWARE_STATEMENT',
  ANONYMOUS = 'ANONYMOUS',
  JWKMS_APPLICATION = 'JWKMS_APPLICATION',
  GATEWAY = 'GATEWAY'
}

export enum ApplicationType {
  JWKMS = 'JWKMS'
}

export interface IChartsEndpointParams {
  fromDate: string;
  toDate: string;
}

export interface IPdfParams {
  fromDate: string;
  toDate: string;
  id: string;
}

export interface ITokenUsageParams {
  fromDate: string;
  toDate: string;
  type: string;
}

export interface IActivityTypeEndpointParams {
  fromDate: string;
  toDate: string;
  consentType: string;
}

export interface IConsentTypeEndpointParams {
  fromDate: string;
  toDate: string;
  obGroupName: string;
}

export interface IDirectoryCountersParams {
  fromDate: string;
  toDate: string;
  type: string;
}

export interface ITableSort {
  field: string;
  direction: 'DESC' | 'ASC' | '';
}

export type ITableSortList = ITableSort[];

export interface ITableFilter {
  field: string;
  regex: string;
}

export type ITableFilterList = ITableFilter[];

export interface ITableServiceParams {
  page: number;
  size: number;
  from: string;
  to: string;
  sorts: ITableSortList;
  filters: ITableFilterList;
}

export interface ITableEndpointUsageRawParams {
  page: number;
  size: number;
  fromDate: string;
  toDate: string;
}

export interface ISSA {
  exp: number;
  iat: number;
  iss: string;
  jti: string;
  ob_registry_tos: string;
  org_contacts: [];
  org_id: string;
  org_jwks_endpoint: string;
  org_jwks_revoked_endpoint: string;
  org_name: string;
  org_status: string;
  software_client_id: string;
  software_client_name: string;
  software_id: string;
  software_jwks_endpoint: string;
  software_jwks_revoked_endpoint: string;
  software_mode: string;
  software_redirect_uris: [];
  software_roles: string[];
  software_logo_uri?: string;
}

export interface ITableReponse<T> {
  data: T[];
  totalPages: number;
  currentPage: number;
  totalResults: number;
}
export interface GeoIP {
  ipAddress: string;

  accuracyRadius: number;
  latitude: number;
  longitude: number;
  country: string;
  countryIsoCode: string;
  continent: string;
  continentCode: string;
  city: string;
  cityCode: number;
  postcode: string;
}

export interface IEndpointUsageItem {
  application: string;
  count: number;
  endpoint: string;
  identityId: string;
  method: string;
  responseStatus: string;
  userType: UserType;
  tppName?: string;
  ssa?: ISSA;
  geoIP?: GeoIP;
}

export interface ITppEntryItem {
  oidcClientId: string;
  name: string;
  logoUri: string;
  types: string[];
  directoryId: String;
  softwareId: String;
  organisationId: String;
  organisationName: string;

  created: string;
  deleted: string;
}

export type ITPPSUsageAggregationResponse = ITableReponse<IEndpointUsageItem>;
export type ITPPSEntriesResponse = ITableReponse<ITppEntryItem>;

export type ITableItemUnion = IEndpointUsageItem;
export type ITableReponseUnion = ITPPSUsageAggregationResponse;
