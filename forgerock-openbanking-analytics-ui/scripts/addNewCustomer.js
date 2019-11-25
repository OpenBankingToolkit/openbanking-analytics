#!/usr/bin/env node

const fs = require('fs-extra'),
  path = require('path'),
  minimist = require('minimist'),
  options = minimist(process.argv.slice(2)),
  _get = require('lodash/get'),
  _set = require('lodash/set'),
  _kebabCase = require('lodash/kebabCase'),
  prettier = require('prettier');

const PACKAGE_ROOT = path.join(__dirname, '..'),
  ANGULAR_JSON = path.join(PACKAGE_ROOT, 'angular.json');
CUSTOMERS_FOLDER = path.join(PACKAGE_ROOT, 'customers');

async function run() {
  try {
    if (!options.customer) {
      throw new Error('missing --customer option');
    }
    const customer = _kebabCase(options.customer);

    // Add new angular customer's config
    await updateAngularJson(customer);

    // Copy blank assets
    await fs.copy(path.join(CUSTOMERS_FOLDER, 'blank'), path.join(CUSTOMERS_FOLDER, customer));

    if (options.customization) {
      // making sure customization path exists
      await fs.access(options.customization);
      const { theme, imgs } = await fs.readJson(options.customization);
      await handleTheme(customer, theme);
      await handleImages(customer, imgs);
    }
  } catch (error) {
    console.error(error);
  }
}

/**
 * Processes the contents of the theme.json customization file and outputs it as SCSS
 *
 * @param data
 * @param theme {[key: string]: string}
 */
async function handleTheme(customer, theme) {
  const cssVars = parseCssVars(theme);

  const scss = Object.keys(cssVars).reduce((scssString, key) => {
    const contrast = getScssProps(cssVars[key]['contrast'], 4);

    return (
      scssString +
      `
$${key}-palette: (
  ${getScssProps(cssVars[key], 2)},
${contrast &&
  `  contrast: (
    ${contrast}
  )
`});
`
    );
  }, '');

  const variablesPath = path.join(CUSTOMERS_FOLDER, customer, 'scss', '_variables.scss');
  await fs.outputFile(
    variablesPath,
    `
    @import '_default_variables';

    ${scss}
  `
  );
  console.info(`${variablesPath} created`);
}

/**
 * Converts a map of SCSS props into valid SCSS
 *
 * @param map
 * @param indent
 * @return {string}
 */
function getScssProps(map, indent = 0) {
  return Object.keys(map || {})
    .filter(key => !key.match(/(contrast|alpha)/))
    .map(key => `${key}: rgba(${map[key]}, ${map[key + '-alpha'] || 1})`)
    .join(',\n' + Array(indent + 1).join(' '));
}

/**
 * Parses a map of CSS variables into a map of SCSS maps
 *
 * @param cssVars
 */
function parseCssVars(cssVars) {
  const cssMap = {};

  Object.keys(cssVars).forEach(key => {
    key
      .match(/^--palette-([a-zA-Z0-9]*)-(contrast)?-?(.*)/)
      .slice(1)
      .reduce((map, subKey, i, arr) => {
        if (typeof subKey === 'undefined') {
          return map;
        }

        return (map[subKey] = map[subKey] || (i < arr.length - 1 ? {} : cssVars[key]));
      }, cssMap);
  });
  return cssMap;
}

async function handleImages(customer, imgs) {
  const logo = _get(imgs, 'logo.file');
  const icon = _get(imgs, 'icon.file');
  const favicon = _get(imgs, 'favicon.file');

  if (logo) {
    const logoPath = path.join(CUSTOMERS_FOLDER, customer, 'assets', 'logos', 'logo.svg');
    await fs.outputFile(logoPath, logo);
    console.info(`${logoPath} created`);
  }

  if (icon) {
    const iconPath = path.join(CUSTOMERS_FOLDER, customer, 'assets', 'logos', 'icon.svg');
    await fs.outputFile(iconPath, icon);
    console.info(`${iconPath} created`);
  }
}

async function updateAngularJson(customer) {
  const angularConfig = await fs.readJson(ANGULAR_JSON);

  Object.keys(angularConfig.projects)
    .filter(projectName => projectName !== 'forgerock')
    .forEach(projectName => {
      const project = angularConfig.projects[projectName];
      if (project.projectType !== 'application') {
        retunrn;
      }

      const customerConfig = _get(project, `architect.build.configurations.${customer}`);
      // if already exists, we skip
      if (customerConfig) return;

      const newConfig = {
        main: `projects/${projectName}/src/index.build.ts`,
        polyfills: `projects/${projectName}/src/index.build.ts`,
        stylePreprocessorOptions: {
          includePaths: [
            `customers/${customer}/apps/analytics/scss`,
            `customers/${customer}/scss`,
            'utils/scss',
            `projects/${projectName}/src/scss`
          ]
        },
        optimization: true,
        outputHashing: 'none',
        sourceMap: false,
        extractCss: true,
        projectNamedChunks: false,
        aot: false,
        extractLicenses: false,
        vendorChunk: false,
        buildOptimizer: false
      };

      _set(angularConfig, `projects.${projectName}.architect.build.configurations.${customer}`, newConfig);
    });

  const prettierConfig = await prettier.resolveConfig(path.join(PACKAGE_ROOT, '.prettierrc'));
  const formattedConfig = prettier.format(JSON.stringify(angularConfig), { ...prettierConfig, parser: 'json' });
  await fs.writeFile(ANGULAR_JSON, formattedConfig);
  console.info(`${ANGULAR_JSON} updated with new customer ${customer}`);
}

run();
