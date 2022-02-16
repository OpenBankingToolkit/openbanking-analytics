## Run local with CLI
> The user to grant access to analytics must have the roles PUSH_KPI and READ_KPI set in the identity field 'MSISDN Number' on AM
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
**To run the analytics client on local against the reference implementation services to play with**
```shell
# Go to reference implementation project
# run the docker compose with profile
docker-compose -f docker-compose-profiles.yml --profile analytics up
# When the services are running stop the service eu.gcr.io/openbanking-214714/obri/analytics-ui:latest
docker ps -a
docker stop {analytics-ui container ID}
```
```shell
# Back to analytics project
# working directory => forgerock-openbanking-ui
npm ci
npm run serve.dev-ob.analytics.client
```
> got to https://analytics.dev-ob.forgerock.financial:4206

**to run the analytics server on local against the reference implementation services to play with**
```shell
# Go to reference implementation project
# run the docker compose with profile
docker-compose -f docker-compose-profiles.yml --profile analytics up
# When the services are running stop the service eu.gcr.io/openbanking-214714/obri/analytics-node:latest
docker ps -a
docker stop {analytics-node container ID}
```
```shell
# Back to analytics project
# working directory => forgerock-openbanking-ui
npm ci
npm run serve.dev-ob.analytics
```
> nodeBackend: 'https://analytics.dev-ob.forgerock.financial:5000/api'

### Access
| app | url | port |
| --- | --- | --- |
| backend | http://localhost | 5000 |
| front end | http://localhost / https://analytics.dev-ob.forgerock.financial:4206 | 4206 |

> GO TO http://localhost:4206 or https://analytics.dev-ob.forgerock.financial:4206

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
