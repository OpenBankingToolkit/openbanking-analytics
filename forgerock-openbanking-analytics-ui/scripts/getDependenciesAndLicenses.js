#!/usr/bin/env node
const fs = require('fs');
const path = require('path');
const checker = require('license-checker');
const packageJson = require('../package.json');

function getDependenciesInfo() {
    return new Promise((resolve, reject) => {
        checker.init({
            start: path.resolve(__dirname, '..')
        }, (err, packages) => {
            if (err) {
                reject(err);
            } else {
                const dependenciesNames = Object.keys(packageJson.dependencies)
                    .map((name) => name.startsWith('@') ? name.substr(1) : name);

                const finalList = [];
                for (key in packages) {
                    const { licenses, repository } = packages[key];
                    const [name, version] = (key.startsWith('@') ? key.substr(1) : key).split('@');
                    if (dependenciesNames.includes(name)) {
                        finalList.push({
                            licenses,
                            repository,
                            name,
                            version,
                        })
                    }
                }
                resolve(finalList);
            }
        });
    });
}

getDependenciesInfo().then((list) => {
    const csv = [];
    list.forEach(({ licenses, repository, name, version }) => {
        csv.push(`${name}, ${version}, ${licenses}, ${repository}`);
    })
    // console.log(csv.join('\r\n'))
    fs.writeFile(path.resolve(__dirname, '..', 'dependencies.csv'), csv.join('\r\n'), (err) => {
        if (err) throw err;
        console.log('Saved!');
    });
})  