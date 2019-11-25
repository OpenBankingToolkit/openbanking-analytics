#!/usr/bin/node

const path = require('path');
const ejs = require('ejs');
const minify = require('html-minifier').minify;
const _get = require('lodash.get');
const _merge = require('lodash.merge');
const _unionBy = require('lodash.unionby');

const PACKAGE_ROOT = path.join(__dirname, '../..');
const orderByDefault = 50;

async function generateIndexHtml(app, customer, assetsByChunkName = {}) {
  try {
    const configs = [];
    configs.push(require(path.join(PACKAGE_ROOT, '/scripts/indexHtml/default-build-settings.js')));
    configs.push(require(path.join(PACKAGE_ROOT, `/projects/${app}/docker/build-settings.js`)));
    if (customer !== 'forgerock') {
      const customerBuildSettings = path.join(PACKAGE_ROOT, `/customers/${customer}/build-settings.js`);
      const { defaultSettings = {}, appsSettings = {} } = require(customerBuildSettings);
      configs.push(_merge({}, defaultSettings, appsSettings[app]));
    }
    configs.push(getIndexHtmlTagsFromStats(assetsByChunkName));

    const head = mergeArrayOfAssets(configs, 'html.head');
    // adding this meta here and not in default-build-setting.js because
    // we can't pass variables to it yet and it would require a refactor
    head.push({
      id: 'metaCustomer',
      tag: `<meta name="customer" content="${customer}" />`
    });
    const body = mergeArrayOfAssets(configs, 'html.body');
    const sortByOrder = (a, b) => (a.order || orderByDefault) - (b.order || orderByDefault);

    const content = await renderTemplate(
      path.join(__dirname, 'index.ejs'),
      { head: head.sort(sortByOrder), body: body.sort(sortByOrder) },
      {}
    );
    return await minify(content, {
      html5: true,
      removeComments: true,
      collapseWhitespace: true
    });
  } catch (error) {
    console.error(error);
  }
}

function mergeArrayOfAssets(configList, path = '', key = 'id') {
  let finalConf = [];
  configList.forEach(config => {
    finalConf = _unionBy(_get(config, path, []), finalConf, key);
  });
  return finalConf;
}

function getIndexHtmlTagsFromStats(stats = {}) {
  const { runtime, 'polyfills-es5': es2015Polyfills, main, polyfills, styles, scripts } = stats;

  return {
    html: {
      head: [
        {
          id: 'styles',
          tag: `<link rel="stylesheet" href="${styles}">`,
          order: 51
        }
      ],
      body: [
        {
          id: 'runtime',
          tag: `<script type="text/javascript" src="${runtime}"></script>`,
          order: 10
        },
        {
          id: 'es2015-polyfills',
          tag: `<script type="text/javascript" src="${es2015Polyfills}" nomodule></script>`,
          order: 11
        },
        {
          id: 'polyfills',
          tag: `<script type="text/javascript" src="${polyfills}"></script>`,
          order: 12
        },
        {
          id: 'scripts',
          tag: `<script type="text/javascript" src="${scripts}"></script>`,
          order: 13
        },
        {
          id: 'main',
          tag: `<script type="text/javascript" src="${main}"></script>`,
          order: 14
        }
      ]
    }
  };
}

async function renderTemplate(path, data = {}, options = {}) {
  return new Promise((resolve, reject) => {
    ejs.renderFile(path, data, options, function(err, str) {
      if (err) {
        reject(err);
      } else {
        resolve(str);
      }
    });
  });
}

module.exports = {
  generateIndexHtml
};
