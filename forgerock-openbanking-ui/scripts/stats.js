#!/usr/bin/node

const path = require('path');
const fs = require('fs-extra');
const gzipSize = require('gzip-size');
const sortBy = require('lodash/sortBy');
const asyncForEach = require('./utils').asyncForEach;

const PACKAGE_ROOT = path.join(__dirname, '..');

async function run() {
  try {
    const forgerockAppPath = path.join(PACKAGE_ROOT, `dist/forgerock`);
    const forgerockAppStats = path.join(forgerockAppPath, 'stats.json');

    // making sure dist/forgerock exists
    await fs.access(forgerockAppPath);
    await fs.access(forgerockAppStats);
    // get the assets that we need to include in our index.html
    let { assets } = await fs.readJson(forgerockAppStats);
    // console.log(assets);
    assets = assets.filter(asset => asset.name.endsWith('.js'));

    const stats = [];
    await asyncForEach(assets, async asset => {
      const gzippedSize = await gzipSize.file(path.join(forgerockAppPath, asset.name));
      stats.push({
        name: asset.name.split('.')[0],
        size: asset.size,
        gzippedSize
      });
    });

    sortBy(stats, 'gzippedSize')
      .reverse()
      .map(asset => console.log(`${asset.name};${asset.size};${asset.gzippedSize}`));
  } catch (error) {
    console.error(error);
  }
}

run();
