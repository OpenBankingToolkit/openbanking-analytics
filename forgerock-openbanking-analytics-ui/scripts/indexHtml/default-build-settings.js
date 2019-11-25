module.exports = {
  html: {
    head: [
      {
        id: 'charset',
        tag: '<meta charset="utf-8" />',
        order: 1
      },
      {
        id: 'base',
        tag: '<base href="/" />',
        order: 2
      },
      {
        id: 'title',
        tag: '<title>Forgerock App</title>',
        order: 3
      },
      {
        id: 'metaRobots',
        tag: '<meta name="robots" content="index, follow" />'
      },
      {
        id: 'metaViewport',
        tag:
          '<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no, viewport-fit=cover" />'
      },
      {
        id: 'metaMsTileColor',
        tag: '<meta name="msapplication-TileColor" content="#2d89ef" />'
      },
      {
        id: 'metaMsConfig',
        tag: '<meta name="msapplication-config" content="assets/favicons/browserconfig.xml" />'
      },
      {
        id: 'metaThemeColor',
        tag: '<meta name="theme-color" content="#ffffff" />'
      },
      {
        id: 'linkSplashscreen',
        tag: '<link href="./assets/splashscreen.css" rel="stylesheet" />'
      },
      {
        id: 'linkIcons',
        tag: '<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet" />'
      },
      {
        id: 'linkFont',
        tag: '<link href="https://fonts.googleapis.com/css?family=Roboto:300,400,500" rel="stylesheet" />'
      },
      {
        id: 'linkAppleTouchIcon',
        tag: '<link rel="apple-touch-icon" sizes="180x180" href="assets/favicons/apple-touch-icon.png" />'
      },
      {
        id: 'linkFavicon32',
        tag:
          '<link class="favicon" rel="icon" type="image/png" sizes="32x32" href="assets/favicons/favicon-32x32.png" />'
      },
      {
        id: 'linkFavicon16',
        tag:
          '<link class="favicon" rel="icon" type="image/png" sizes="16x16" href="assets/favicons/favicon-16x16.png" />'
      },
      {
        id: 'linkFavicon',
        tag: '<link class="favicon" rel="shortcut icon" type="image/x-icon" href="assets/favicons/favicon.ico" />'
      },
      {
        id: 'linkManifest',
        tag: '<link rel="manifest" href="assets/favicons/site.webmanifest" />'
      },
      {
        id: 'linkSafariPinnedTab',
        tag: '<link rel="mask-icon" href="assets/favicons/safari-pinned-tab.svg" color="#5bbad5" />'
      }
    ],
    body: {}
  }
};
