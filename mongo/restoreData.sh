#!/bin/bash
cd /tmp
tar -xzf /docker-entrypoint-initdb.d/sample-data.tar.gz
mongorestore --db test --drop sample-data
