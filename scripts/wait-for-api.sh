#!/usr/bin/env sh
set -eu
until curl --fail --silent http://localhost:8080/actuator/health >/dev/null; do
  sleep 2
done
echo "Yomora API pronta em http://localhost:8080"

