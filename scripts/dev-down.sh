#!/usr/bin/env bash
set -euo pipefail

if ! command -v docker >/dev/null 2>&1; then
  echo "docker is not installed; nothing to stop in docker mode"
  exit 0
fi

if docker compose version >/dev/null 2>&1; then
  echo "Stopping docker compose stack..."
  docker compose down
  echo "Stopped."
  exit 0
fi

echo "docker compose not available; nothing stopped"
