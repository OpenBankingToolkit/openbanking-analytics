module.exports = {
  defaultSettings: {
    html: {
      head: [
        {
          id: 'title',
          tag: '<title>Citibank</title>',
          order: 1
        },
        {
          id: 'description',
          tag:
            '<meta name="description" content="Open a bank account, apply for a personal loan or home equity line of credit, or start investing in your financial future with Citi. Learn more about our range of services.">',
          order: 2
        },
        {
          id: 'og:title',
          tag: '<meta name="og:title" property="og:title" content="Citibank">',
          order: 3
        }
      ],
      body: []
    }
  }
};
