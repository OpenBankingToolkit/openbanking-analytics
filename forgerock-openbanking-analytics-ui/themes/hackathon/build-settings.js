module.exports = {
  defaultSettings: {
    html: {
      head: [
        {
          id: 'description',
          tag: '<meta name="description" content="Hackathon description">',
          order: 2
        },
        {
          id: 'og:title',
          tag: '<meta name="og:title" property="og:title" content="Hackathon Bank">',
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
            tag: '<title>Hackathon Auth App</title>',
            order: 1
          }
        ]
      }
    }
  }
};
