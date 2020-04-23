# Stage 1 - the build process
FROM node:10 as build-ui

WORKDIR /src
ADD package.json package-lock.json /src/

RUN NG_CLI_ANALYTICS=ci npm ci

ADD . /src

RUN npm run build.analytics.themes

# Stage 2 - the production environment
FROM nginx:1.13.9-alpine

EXPOSE 80

ARG BUILD_VERSION
ENV BUILD_VERSION ${BUILD_VERSION}

ENV TEMPLATE "forgerock"
ENV DOMAIN ""
ENV NODE_BACKEND_URL "https://node.analytics.DOMAIN"
ENV METRICS_BACKEND_URL "https://service.metrics.DOMAIN"

RUN rm -rf /etc/nginx/conf.d
COPY --from=build-ui /src/dist /usr/share/nginx
COPY projects/analytics/docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY projects/analytics/docker/start.sh /opt/start.sh

CMD ["/opt/start.sh"]
