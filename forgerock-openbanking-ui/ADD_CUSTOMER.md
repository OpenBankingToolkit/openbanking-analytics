## Create a new customer

```shell
./scripts/addNewCustomer.js --customer <customerName>
```

This will create all the necessary angular.json configuration and create a new folder `customers/<customerName>` with all the necessary files to compile.

## Create a new customer and apply a configuration

Open a cluster that have the customization sidenav enabled e.g: https://auth.<clusterName>.forgerock.financial/

Open the sidenav by clicking this button:

![Screen Shot 2019-10-25 at 13 02 03](https://user-images.githubusercontent.com/1388706/67576487-e98ee580-f73e-11e9-9fa0-cc4875a41c65.png)

Upload the icon, logo and favicon with drag and drop (SVG only):

![Screen Shot 2019-10-25 at 13 02 21](https://user-images.githubusercontent.com/1388706/67576485-e98ee580-f73e-11e9-83ce-2b16e7e9bb5a.png)

Select the 4 palettes by using default ones or by selecting colors one by one using the following tool:

![Screen Shot 2019-10-25 at 13 02 42](https://user-images.githubusercontent.com/1388706/67576484-e8f64f00-f73e-11e9-8ada-5031be540c28.png)

Once you are happy with the result, click the `export` button to get the JSON export.

![Screen Shot 2019-10-25 at 13 02 55](https://user-images.githubusercontent.com/1388706/67576481-e8f64f00-f73e-11e9-9a55-d67366773a8d.png)

Run the following command to apply the customization:

```shell
./scripts/addNewCustomer.js --customer <customerName> --customization <pathToCustomizationJson>
```

Automated tasks:

- [x] angular.json config
- [x] sass/\_variables.scss
- [x] assets/logos/icon.svg
- [x] assets/logos/logo.svg
- [] assets/favicons (go to https://realfavicongenerator.net/ and download the result)
- [] assets/slashscreen.css (change background/throbber color manually)
- [] build-settings.js (change `<Name>` and `<description>` manually)
- [] deployment-settings.js (change `<Name>` and `<description>` manually)

## Run the new config locally

If you want to see the result locally before committing the result. Add the following config in `angular.json` and replace `<customerName>`, `<appName>` and `<clusterName>`. See example: `ui-integ-hl`

```json
{
  "<clusterName>-<customerName>": {
    "stylePreprocessorOptions": {
      "includePaths": [
        "customers/<customerName>/apps/<appName>/scss",
        "customers/<customerName>/scss",
        "utils/scss",
        "projects/<appName>/src/scss"
      ]
    },
    "fileReplacements": [
      {
        "replace": "projects/<appName>/src/environments/environment.ts",
        "with": "projects/<appName>/src/environments/environment.<clusterName>.ts"
      },
      {
        "replace": "projects/<appName>/src/assets/logos/icon.svg",
        "with": "customers/<customerName>/assets/logos/icon.svg"
      },
      {
        "replace": "projects/<appName>/src/assets/logos/logo.svg",
        "with": "customers/<customerName>/assets/logos/logo.svg"
      },
      {
        "replace": "projects/<appName>/src/assets/splashscreen.css",
        "with": "customers/<customerName>/assets/splashscreen.css"
      }
    ]
  }
}
```

Then when using `ng serve` use `--browserTarget <appName>:build:<clusterName>-<customerName>`. See `serve.ui-integ.analytics.hl.client` task in `angular.json`

## Build

add the new customer to all `build.<appName>.all` scripts with `-c <customerName>` option
