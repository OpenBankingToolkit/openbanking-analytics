## Run local with CLI
### Prepare
```shell
# working directory => forgerock-openbanking-ui
npm ci
```
```shell
# Build analytics backend server
# working directory => forgerock-openbanking-ui
cd projects/analytics/server
npm ci
```
### Build Analytics backend server and front end
```shell
# Build analytics backend server
# working directory => forgerock-openbanking-ui
npm run build.analytics.server
```
```shell
# build analytics front end
# working directory => forgerock-openbanking-ui
npm run build.analytics.forgerock
```
### Run analytics backend server and front end
```shell
# Run analytics backend server
# working directory => forgerock-openbanking-ui
npm run build.analytics.server
```
```shell
# Run front end 
npm run serve.analytics
```
### Access
| app | url | port |
| --- | --- | --- |
| backend | http://localhost | 5000 |
| front end | http://localhost | 0000 |

> GO TO http://localhost:4206

## Running docker image

<https://hub.docker.com/repository/docker/openbankingtoolkit/openbanking-analytics-ui> is a built version of the Analytics app with only the Forgerock template.

It is convenient to start the app in no time.

- `<PORT>`: **REQUIRED** Port to use on your machine
- `<DOMAIN>`: **REQUIRED** Domain to use. Will replace `DOMAIN` in the frontend [config](./forgerock-openbanking-ui/projects/analytics/docker/deployment-settings.js) e.g: `https://analytics.DOMAIN`
- `<TEMPLATE_NAME>`: Default value: `forgerock`.

```bash
docker run -it -p <PORT>:80 -e TEMPLATE=<TEMPLATE_NAME> -e DOMAIN=<DOMAIN> openbankingtoolkit/openbanking-analytics-ui
```

## Building the app with your theme

Create a new theme: <https://github.com/OpenBankingToolkit/openbanking-toolkit/wiki/Create-a-new-Theme>

Then build the docker image

## Building your own docker image

```bash
# Build Analytics
docker build -t <IMAGE_NAME> -f projects/analytics/docker/Dockerfile .
# Build Analytics Server
docker build -t <IMAGE_NAME> -f projects/analytics/docker/Dockerfile-server .
```
