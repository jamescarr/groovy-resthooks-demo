#!/bin/sh
JAVA_OPTS=-Dratpack.port=5050
DEMO_CLIENT_BASE_URL="$1"
groovy client/ratpack.groovy 
