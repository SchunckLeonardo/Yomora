#!/usr/bin/env sh
set -eu
docker run --rm -v "$(pwd):/workspace" redocly/cli:1.34.2 lint /workspace/contracts/openapi/yomora-api.yaml

