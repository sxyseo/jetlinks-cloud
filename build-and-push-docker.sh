#!/usr/bin/env bash

servers="$1"
repo="$2"

if [ ! -n "$servers" ];then
echo "未指定构建的服务"
exit 0;
fi
if [ ! -n "repo" ];then
repo=jetlinks
fi
if [ "$servers" = "all" ];then
servers=device-gateway-service
fi

echo "构建服务:$servers"
echo "docker仓库:$repo"

./mvnw -pl common-components,common-components/logging-component,common-components/redis-component,common-components/service-dependencies -am clean install -DskipTests

./mvnw -Dgit-commit-id=$(git rev-parse HEAD) -pl "$servers" clean package docker:removeImage docker:build -DpushImage -DskipTests -Ddocker.image.baseName="$repo"
