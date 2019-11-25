#!/usr/bin/node

const minimist = require('minimist');
const path = require('path');
const fs = require('fs-extra');
const _merge = require('lodash.merge');

const PACKAGE_ROOT = path.join(__dirname, '..');

async function mergeConfig(files) {
  if (Array.isArray(files)) {
    const contentToMerge = [];
    await asyncForEach(files, async file => {
      const content = await getJSONContentFromJsOrJSON(path.join(PACKAGE_ROOT, file));
      contentToMerge.push(content);
    });
    return _merge(...contentToMerge);
  } else {
    return getJSONContentFromJsOrJSON(path.join(PACKAGE_ROOT, files));
  }
}

async function getJSONContentFromJsOrJSON(srcPath) {
  await fs.pathExists(srcPath);
  const ext = path.extname(srcPath);
  if (ext === '.json') {
    return await fs.readJson(srcPath);
  } else if (ext === '.js') {
    return require(srcPath);
  }
  throw new Error('config files should be either .json or .js with module.exports');
}

async function asyncForEach(array, callback) {
  for (let index = 0; index < array.length; index++) {
    await callback(array[index], index, array);
  }
}

module.exports = {
  mergeConfig,
  asyncForEach
};
