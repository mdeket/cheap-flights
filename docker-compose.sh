#!/bin/bash

export PROFILE=docker &&
export JWT_SECRET=Cg7i4cOUKJMmMtaCAsGIeZDloIcVcjRcaKuJTej2meUfZmSd1RuaYFuBWup5tDi &&
export NEO4J_AUTH=neo4j/secret &&
export NEO4J_PASSWORD=secret &&
docker-compose up