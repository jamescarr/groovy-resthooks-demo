#!/bin/sh
JAVA_OPTS=-Dratpack.port=5055
export DEMO_SERVICE_BASE_URL="$1"
groovy service/ratpack.groovy
