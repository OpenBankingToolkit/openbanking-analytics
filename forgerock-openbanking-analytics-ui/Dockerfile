FROM node:10

WORKDIR /src
ADD package.json package-lock.json /src/

ARG OB_UI_LIBS_SSH_KEY
ENV OB_UI_LIBS_SSH_KEY ${OB_UI_LIBS_SSH_KEY}
ARG GITHUB_ACCESS_TOKEN
ENV GITHUB_ACCESS_TOKEN ${GITHUB_ACCESS_TOKEN}

RUN mkdir -p ~/.ssh
RUN chmod -R 700 ~/.ssh
RUN ssh-keyscan github.com >> ~/.ssh/known_hosts
RUN chmod 600 ~/.ssh/known_hosts
RUN echo "${OB_UI_LIBS_SSH_KEY}" | tr ',' '\n' > ~/.ssh/id_rsa
RUN chmod 600 ~/.ssh/id_rsa
RUN echo "Host github.com\n  IdentityFile ~/.ssh/id_rsa" > ~/.ssh/config
RUN NG_CLI_ANALYTICS=ci npm ci

ADD . /src

# git init here is to be able to use submodules. .git being in the parent directory 
RUN git init  
RUN sed -i "s/git@github.com:/https:\/\/$GITHUB_ACCESS_TOKEN@github.com\//g" .gitmodules
RUN git submodule init && git submodule update customers