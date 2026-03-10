#!/usr/bin/env bash
set -euo pipefail

IMAGE_REPOSITORY="${IMAGE_REPOSITORY:-theprocastinator/decentralised-gym}"
GIT_SHA="$(git rev-parse --short HEAD)"
TIMESTAMP="$(date +%Y%m%d%H%M%S)"
IMAGE_TAG="${IMAGE_TAG:-${TIMESTAMP}-${GIT_SHA}}"
FULL_IMAGE="${IMAGE_REPOSITORY}:${IMAGE_TAG}"

echo "Building ${FULL_IMAGE}"
docker build -t "${FULL_IMAGE}" .

echo "Pushing ${FULL_IMAGE}"
docker push "${FULL_IMAGE}"

echo ""
echo "Published image:"
echo "${FULL_IMAGE}"
