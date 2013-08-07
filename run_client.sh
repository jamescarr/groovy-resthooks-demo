#!/bin/sh
export JAVA_OPTS=-Dratpack.port=5050
export DEMO_CLIENT_BASE_URL="$1"
groovy client.groovy
