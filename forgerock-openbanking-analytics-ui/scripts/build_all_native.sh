#!/usr/bin/env bash
set -e

while getopts "c:a:" opt; do
    case $opt in
        a) app=("$OPTARG");;
        c) customers+=("$OPTARG");;
    esac
done
shift $((OPTIND -1))

# BUILD FORGEROCK THEME (DEFAULT THEME NEEDED TO BE RAN AT THE BEGINNING)
echo "Building $app for customer forgerock"
ng build --project=$app --prod --configuration=forgerock --output-path dist/forgerock --statsJson --extra-webpack-config webpack.extra.js --main projects/$app/src/main-native.ts
node ./scripts/postBuild --app "$app" --customer "forgerock"

# BUILD CUSTOMER CSS, CONFIG, INDEX.HTML & ASSETS (NO JS)
for customer in "${customers[@]}"; do
    echo "Building $app for customer $customer"
    ng build --project=$app --prod --configuration=$customer --output-path "dist/$customer" --extra-webpack-config webpack.extra.js
    node ./scripts/postBuild --app "$app" --customer "$customer"
done

# Cleanup stats that exposes details of our dev stack but necessary to build correct packages
find ./dist -type f -name "stats.json" -exec rm -f {} \;

