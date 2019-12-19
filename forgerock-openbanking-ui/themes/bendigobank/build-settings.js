module.exports = {
  defaultSettings: {
    html: {
      head: [
        {
          id: 'title',
          tag: '<title>Bendigo Bank</title>',
          order: 1
        },
        {
          id: 'description',
          tag:
            '<meta name="description" content="Bendigo Bank is Australia’s better big bank. We offer personal, business, wealth and community banking and much more. Contact a banking specialist today.">',
          order: 2
        },
        {
          id: 'og:title',
          tag: '<meta name="og:title" property="og:title" content="Bendigo Bank">',
          order: 3
        }
      ],
      body: []
    }
  }
};
