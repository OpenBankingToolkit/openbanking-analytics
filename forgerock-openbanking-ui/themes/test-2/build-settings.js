module.exports = {
  defaultSettings: {
    html: {
      head: [
        {
          id: 'title',
          tag: '<title><Name></title>',
          order: 1
        },
        {
          id: 'description',
          tag: '<meta name="description" content="<description>">',
          order: 2
        },
        {
          id: 'og:title',
          tag: '<meta name="og:title" property="og:title" content="<Name>">',
          order: 3
        }
      ],
      body: []
    }
  }
};
