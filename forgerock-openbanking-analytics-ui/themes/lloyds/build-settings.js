module.exports = {
  defaultSettings: {
    html: {
      head: [
        {
          id: 'title',
          tag: '<title>Lloyds App</title>',
          order: 1
        },
        {
          id: 'description',
          tag: '<meta name="description" content="Lloyds description">',
          order: 2
        },
        {
          id: 'og:title',
          tag: '<meta name="og:title" property="og:title" content="Lloyds Bank">',
          order: 3
        }
      ],
      body: []
    }
  },
  appsSettings: {
    auth: {
      html: {
        head: [
          {
            id: 'title',
            tag: '<title>Lloyds Auth App</title>',
            order: 1
          }
        ]
      }
    },
    bank: {
      html: {
        head: [
          {
            id: 'title',
            tag: '<title>Lloyds Bank App</title>',
            order: 1
          }
        ]
      }
    },
    directory: {
      html: {
        head: [
          {
            id: 'title',
            tag: '<title>Lloyds Directory App</title>',
            order: 1
          }
        ]
      }
    },
    'manual-onboarding': {
      html: {
        head: [
          {
            id: 'title',
            tag: '<title>Lloyds Manual Onboarding App</title>',
            order: 1
          }
        ]
      }
    },
    analytics: {
      html: {
        head: [
          {
            id: 'title',
            tag: '<title>Lloyds Analytics</title>',
            order: 1
          }
        ]
      }
    }
  }
};
