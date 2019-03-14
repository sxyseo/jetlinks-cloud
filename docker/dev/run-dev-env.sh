#!/usr/bin/env bash
docker-compose stop
docker-compose rm -f
sudo sysctl -w vm.max_map_count=262144
docker-compose up -d