#!/usr/bin/env bash
set -euo pipefail

MODE="docker"
if [[ "${1:-}" == "--local" ]]; then
  MODE="local"
fi

if [[ "$MODE" == "docker" ]]; then
  if ! command -v docker >/dev/null 2>&1; then
    echo "docker is not installed. Use: ./scripts/dev-up.sh --local"
    exit 1
  fi

  if docker compose version >/dev/null 2>&1; then
    echo "Starting app + mysql with docker compose..."
    docker compose up --build -d
    echo "Started. Open: http://localhost:8090/swagger-ui.html (MySQL: localhost:3307)"
  else
    echo "docker compose is not available. Use Docker Desktop v2+ or run: ./scripts/dev-up.sh --local"
    exit 1
  fi
  exit 0
fi

if ! command -v mvn >/dev/null 2>&1; then
  echo "mvn is not installed. Install Maven or use docker mode: ./scripts/dev-up.sh"
  exit 1
fi

echo "Starting Spring Boot locally (requires local MySQL)..."
exec mvn spring-boot:run -Djava.version=21
