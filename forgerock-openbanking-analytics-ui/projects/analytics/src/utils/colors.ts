import * as d3Colors from 'd3-scale-chromatic';

export const colors = d3Colors.schemePaired;

export const colorsByConsentStatus = {
  AUTHORISED: d3Colors.schemePaired[0],
  AWAITINGAUTHORISATION: d3Colors.schemePaired[1],
  ACCEPTEDSETTLEMENTCOMPLETED: d3Colors.schemePaired[2],
  CONSUMED: d3Colors.schemePaired[3],
  ACCEPTEDCUSTOMERPROFILE: d3Colors.schemePaired[4],
  REJECTED: d3Colors.schemePaired[5],
  ACCEPTEDSETTLEMENTINPROCESS: d3Colors.schemePaired[6],
  ACCEPTEDTECHNICALVALIDATION: d3Colors.schemePaired[7],
  PENDING: d3Colors.schemePaired[8],
  REVOKED: d3Colors.schemePaired[9],
  AWAITINGUPLOAD: d3Colors.schemePaired[10]
};

export default colors;
