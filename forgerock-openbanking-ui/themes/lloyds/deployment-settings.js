module.exports = {
  defaultSettings: {
    client: {
      name: 'Lloyds'
    }
  },
  appsSettings: {
    auth: {
      featureFlags: {
        disableProfileForm: true,
        disablePasswordForm: true
      },
      routeDenyList: ['profile/password']
    },
    bank: {
      featureFlags: {
        disableProfileForm: true,
        disablePasswordForm: true
      },
      routeDenyList: ['profile/password']
    },
    'manual-onboarding': {
      navigations: {
        main: [
          {
            id: 'home',
            title: 'home',
            translate: 'NAV.ONBOARDING',
            type: 'item',
            icon: 'dashboard',
            exactMatch: true,
            url: '/'
          },
          {
            id: 'psu',
            title: 'psu',
            translate: 'PSU credentials',
            type: 'item',
            icon: 'vpn_key',
            exactMatch: true,
            url: '/psu-credentials'
          }
        ]
      },
      lloyds: {
        psuPage: {
          content: `
            <h1>PSU Credentials</h1>
            <p>Users registered with Open Banking can test their applications with mock accounts created for the following banks.</p>
            <p>Mock account and payment data matches the our current product range for: </p> 
            <ul>
            <li>Retail</li>
            <li>Business</li>
            <li>Commercial</li>
            </ul>
            <p>The password for all mock accounts is Password123.</p> 
           <table class="psu-table">
              <thead>
                  <tr>
                      <th>Brand</th>
                      <th>Retail</th>
                      <th>Commercial</th>
                      <th>Business</th>
                  </tr>
              </thead>
              <tbody>
                  <tr>
                      <td>Lloyds Bank</td>
                      <td>llr001</td>
                      <td>llc001</td>
                      <td>llb001</td>
                  </tr>
                  <tr>
                      <td>Halifax</td>
                      <td>har001</td>
                      <td>N/A</td>
                      <td>N/A</td>
                  </tr>
                  <tr>
                      <td>Bank of Scotland</td>
                      <td>bar001</td>
                      <td>bac001</td>
                      <td>bab001</td>
                  </tr>
                  <tr>
                      <td>MBNA</td>
                      <td>mbr001</td>
                      <td>N/A</td>
                      <td>N/A</td>
                  </tr>
              </tbody>
            </table>
            <style>
              .psu-table {
                margin: 10px 0;
              }
            </style>
            `
        }
      }
    }
  }
};
