#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRONTEND_DIR="${ROOT_DIR}/gym_visit_flutter"
FRONTEND_REPO_URL="https://github.com/imanishpr/decentralised-gym-fe.git"

echo "==> Checking Homebrew"
if ! command -v brew >/dev/null 2>&1; then
  echo "Homebrew not found."
  echo "Install Homebrew first: /bin/bash -c \"\$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)\""
  exit 1
fi

echo "==> Installing required tools (git, openjdk@21, maven, flutter, ngrok)"
brew install git openjdk@21 maven docker-compose ngrok/ngrok/ngrok || true
brew install --cask flutter || true

export PATH="/opt/homebrew/bin:/usr/local/bin:${PATH}"

if ! command -v flutter >/dev/null 2>&1; then
  if ls /opt/homebrew/Caskroom/flutter/*/flutter/bin/flutter >/dev/null 2>&1; then
    FLUTTER_BIN="$(ls -d /opt/homebrew/Caskroom/flutter/*/flutter/bin | tail -n 1)"
    export PATH="${FLUTTER_BIN}:${PATH}"
  elif [ -x "${HOME}/develop/flutter/bin/flutter" ]; then
    export PATH="${HOME}/develop/flutter/bin:${PATH}"
  elif [ -x "${HOME}/flutter/bin/flutter" ]; then
    export PATH="${HOME}/flutter/bin:${PATH}"
  fi
fi

if ! command -v flutter >/dev/null 2>&1; then
  echo "Flutter CLI not found in PATH."
  echo "Run these and retry:"
  echo "  brew install --cask flutter"
  echo "  echo 'export PATH=\"/opt/homebrew/bin:\$PATH\"' >> ~/.zshrc"
  echo "  source ~/.zshrc"
  exit 1
fi

if [ -d "/opt/homebrew/opt/openjdk@21" ]; then
  export JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
  export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"
fi

echo "==> Verifying Docker"
if ! docker info >/dev/null 2>&1; then
  echo "Docker Desktop is not running. Start Docker Desktop and run this script again."
  exit 1
fi

if [ ! -d "${FRONTEND_DIR}/.git" ]; then
  echo "==> Cloning frontend repo into ${FRONTEND_DIR}"
  git clone "${FRONTEND_REPO_URL}" "${FRONTEND_DIR}"
else
  echo "==> Frontend repo already exists at ${FRONTEND_DIR}"
fi

echo "==> Building Flutter web"
cd "${FRONTEND_DIR}"
flutter --version
flutter config --enable-web
flutter pub get
flutter build web --release

echo "==> Starting backend stack with Docker Compose"
cd "${ROOT_DIR}"
docker compose up -d --build

echo ""
echo "Setup complete."
echo "App URL:      http://localhost:8088"
echo "Swagger URL:  http://localhost:8088/swagger-ui/index.html"
echo "Health URL:   http://localhost:8088/actuator/health"
echo ""
echo "Seeded users:"
echo "  Admin -> admin1@gymapp.com / Admin@12345"
echo "  User  -> user1@gymapp.com / User@12345"
