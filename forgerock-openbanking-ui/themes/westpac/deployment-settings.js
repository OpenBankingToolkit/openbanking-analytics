module.exports = {
  defaultSettings: {
    client: {
      name: 'Westpac NZ'
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
    }
  }
};
