#!/usr/bin/node

const minimist = require('minimist');
const path = require('path');
const fs = require('fs-extra');
const mergedirs = require('merge-dirs').default;
const options = minimist(process.argv.slice(2));
const _merge = require('lodash.merge');

const { generateIndexHtml } = require('./indexHtml/index');
const { mergeConfig } = require('./utils');

const PACKAGE_ROOT = path.join(__dirname, '..');

async function run() {
  try {
    if (!options.app) {
      throw new Error('missing --app option');
    }
    if (!options.customer) {
      throw new Error('missing --customer option');
    }
    const { app, customer } = options;
    const forgerockAppPath = path.join(PACKAGE_ROOT, `dist/forgerock`);
    const forgerockNativeAppPath = path.join(PACKAGE_ROOT, `native/${app}`);
    const forgerockAppStats = path.join(forgerockAppPath, 'stats.json');

    // making sure dist/forgerock exists
    await fs.access(forgerockAppPath);
    await fs.access(forgerockAppStats);
    // get the assets that we need to include in our index.html
    const { assetsByChunkName } = await fs.readJson(forgerockAppStats);

    if (customer === 'forgerock') {
      // extract forgerock conf and write file
      const forgerockConfig = require(path.join(PACKAGE_ROOT, `/projects/${app}/docker/deployment-settings.js`));
      await fs.writeJson(path.join(forgerockAppPath, 'deployment-settings.json'), forgerockConfig);

      // Generate index.html and write file
      const generatedIndexHtml = await generateIndexHtml(app, customer, assetsByChunkName);
      await fs.writeFile(path.join(forgerockAppPath, 'index.html'), generatedIndexHtml);
    } else {
      const customerAppPath = path.join(PACKAGE_ROOT, `dist/${customer}`);
      const customerAppCopyPath = path.join(PACKAGE_ROOT, `dist/${customer}-temp`);

      // making sure both our targets are present in the file system
      await fs.access(customerAppPath);

      // Move the customer app
      await fs.move(customerAppPath, customerAppCopyPath, { overwrite: true });

      // Copy the foregerock app to the customer app target path
      await fs.copy(forgerockAppPath, customerAppPath);

      // Overwrite foregerock's assets with customer's assets
      mergeAssetsFolders(customer, app);

      // Generate index.html and write file
      const generatedIndexHtml = await generateIndexHtml(app, customer, assetsByChunkName);
      await fs.writeFile(path.join(customerAppPath, 'index.html'), generatedIndexHtml);

      const newConfig = mergeDeploymentSettings(customer, app);
      await fs.writeJson(path.join(customerAppPath, 'deployment-settings.json'), newConfig);

      // Replace existing forgerock CSS by customer generated CSS
      await fs.copy(path.join(customerAppCopyPath, 'styles.css'), path.join(customerAppPath, assetsByChunkName.styles));

      // cleanup the temp app
      await fs.remove(customerAppCopyPath);
    }
  } catch (error) {
    console.error(error);
  }
}

run();

function mergeDeploymentSettings(customer, app) {
  const dockerDeploymentSettings = path.join(PACKAGE_ROOT, `/projects/${app}/docker/deployment-settings.js`);
  const customerDeploymentSettings = path.join(PACKAGE_ROOT, `/customers/${customer}/deployment-settings.js`);
  const { defaultSettings = {}, appsSettings = {} } = require(customerDeploymentSettings);
  return _merge({}, require(dockerDeploymentSettings), defaultSettings, appsSettings[app]);
}

function mergeAssetsFolders(customer, app) {
  const destination = path.join(PACKAGE_ROOT, `dist/${customer}/assets`);
  const customerAssetsPath = path.join(PACKAGE_ROOT, `/customers/${customer}/assets`);
  const customerAppAssetsPath = path.join(PACKAGE_ROOT, `/customers/${customer}/apps/${app}/assets`);

  mergedirs(customerAssetsPath, destination, 'overwrite');

  // Allow specific assets per app if needed
  if (fs.existsSync(customerAppAssetsPath)) {
    mergedirs(customerAppAssetsPath, destination, 'overwrite');
  }
}
