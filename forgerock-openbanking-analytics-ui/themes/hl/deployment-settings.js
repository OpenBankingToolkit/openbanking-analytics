module.exports = {
  defaultSettings: {
    client: {
      name: 'Hargreaves Lansdown'
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
