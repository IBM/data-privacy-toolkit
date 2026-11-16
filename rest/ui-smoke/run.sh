#!/usr/bin/env bash
# Builds the web UI, serves it from the real REST back-end, and drives it with a headless Chrome.
#
#   ./run.sh                    build, start everything, run the scenarios, clean up
#
# Environment (all optional):
#   DPT_REST_JAR  Spring Boot jar to run; built with `./gradlew bootJar` when not set
#   CHROME        Chrome/Chromium binary; looked up automatically when not set
#   NODE_IMAGE    container image used when no local Node >= 22 exists
#   APP_PORT      port of the back-end             (default 18080)
#   CDP_PORT      DevTools port of the browser     (default 19222)
#   SCREENSHOTS_OUT  directory that receives a PNG per scenario (for reviewing the look of the UI)
#
# Needs: Java 21, Chrome or Chromium, and either Node >= 22 or Docker. Everything listens on 127.0.0.1 only.
set -euo pipefail

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REST="$(dirname "$HERE")"
APP_PORT="${APP_PORT:-18080}"
CDP_PORT="${CDP_PORT:-19222}"
NODE_IMAGE="${NODE_IMAGE:-registry.access.redhat.com/ubi9/nodejs-24-minimal:latest}"

WORK="$(mktemp -d)"
PIDS=()
CONTAINER=""

cleanup () {
  local status=$?
  for pid in ${PIDS[@]+"${PIDS[@]}"}; do kill "$pid" 2>/dev/null || true; done
  [ -z "$CONTAINER" ] || docker rm -f "$CONTAINER" >/dev/null 2>&1 || true
  rm -rf "$WORK"
  exit $status
}
trap cleanup EXIT INT TERM

log () { printf '\n==> %s\n' "$*"; }

find_chrome () {
  if [ -n "${CHROME:-}" ]; then echo "$CHROME"; return; fi
  local candidate
  for candidate in \
    "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome" \
    "/Applications/Chromium.app/Contents/MacOS/Chromium" \
    "$(command -v google-chrome 2>/dev/null || true)" \
    "$(command -v chromium 2>/dev/null || true)" \
    "$(command -v chromium-browser 2>/dev/null || true)"; do
    if [ -n "$candidate" ] && [ -x "$candidate" ]; then echo "$candidate"; return; fi
  done
  echo "No Chrome or Chromium found: set CHROME to its path" >&2
  exit 1
}

wait_for () { # url, seconds
  local i
  for ((i = 0; i < $2 * 2; i++)); do
    if curl -fsS -m 2 -o /dev/null "$1" 2>/dev/null; then return 0; fi
    sleep 0.5
  done
  echo "Timed out waiting for $1" >&2
  return 1
}

local_node_ok () {
  command -v node >/dev/null 2>&1 || return 1
  [ "$(node -p 'process.versions.node.split(".")[0]')" -ge 22 ]
}

# ---------------------------------------------------------------------------------------------------------------
# 1. build the web UI and prepare a way to run the driver
# ---------------------------------------------------------------------------------------------------------------
CDP_HOST=127.0.0.1
if local_node_ok; then
  log "Building the web UI with local Node $(node -v)"
  cp -R "$REST/front-end/." "$WORK/front-end"
  rm -rf "$WORK/front-end/node_modules" "$WORK/front-end/build"
  (cd "$WORK/front-end" && npm ci --ignore-scripts --no-audit --no-fund && npm run build)
  UI_BUILD="$WORK/front-end/build"
  run_driver () { SCREENSHOT_DIR="${SCREENSHOTS_OUT:-}" node "$HERE/smoke.mjs"; }
else
  command -v docker >/dev/null 2>&1 || { echo "Need Node >= 22 or Docker" >&2; exit 1; }
  log "No local Node >= 22: building the web UI in $NODE_IMAGE"
  CONTAINER="dpt-ui-smoke-$$"
  # The driver, in the container, has to reach the DevTools port that Chrome opens on the loopback interface of the
  # host. On Linux the container shares the network of the host. Docker Desktop (macOS, Windows) runs containers in a
  # virtual machine, where host.docker.internal is forwarded to the loopback interface of the host.
  if [ "$(uname -s)" = "Linux" ]; then
    NETWORK=(--network host)
    CDP_HOST=127.0.0.1
  else
    NETWORK=(--add-host=host.docker.internal:host-gateway)
    CDP_HOST=host.docker.internal
  fi
  docker create --name "$CONTAINER" -w /tmp/app -e HOME=/tmp -e CI=true \
    "${NETWORK[@]}" --entrypoint sh "$NODE_IMAGE" -c 'sleep 3600' >/dev/null
  docker start "$CONTAINER" >/dev/null
  docker exec -u 0 "$CONTAINER" mkdir -p /tmp/app /tmp/ui-smoke
  # streamed with tar instead of `docker cp`, which rejects the extended attributes macOS adds to files
  tar --no-xattrs -C "$REST/front-end" --exclude=node_modules --exclude=build -cf - . \
    | docker exec -i -u 0 "$CONTAINER" tar -xf - -C /tmp/app
  tar --no-xattrs -C "$HERE" -cf - . | docker exec -i -u 0 "$CONTAINER" tar -xf - -C /tmp/ui-smoke
  docker exec -u 0 "$CONTAINER" chown -R 1001:0 /tmp/app /tmp/ui-smoke
  docker exec -w /tmp/app "$CONTAINER" sh -c 'npm ci --ignore-scripts --no-audit --no-fund && npm run build'
  mkdir -p "$WORK/front-end"
  docker cp "$CONTAINER:/tmp/app/build" "$WORK/front-end/build"
  UI_BUILD="$WORK/front-end/build"
  run_driver () {
    docker exec -e CDP_HOST="$CDP_HOST" -e CDP_PORT="$CDP_PORT" -e APP_URL="http://127.0.0.1:$APP_PORT/" \
      -e SCREENSHOT_DIR="${SCREENSHOTS_OUT:+/tmp/screenshots}" -w /tmp/ui-smoke "$CONTAINER" node smoke.mjs
  }
fi

# ---------------------------------------------------------------------------------------------------------------
# 2. start the back-end, serving the freshly built UI as its static content
# ---------------------------------------------------------------------------------------------------------------
JAR="${DPT_REST_JAR:-}"
if [ -z "$JAR" ]; then
  log "Building the back-end jar"
  (cd "$REST" && ./gradlew --console=plain bootJar -x test)
  JAR="$(ls -t "$REST"/build/libs/*.jar | grep -v plain | head -1)"
fi

log "Starting the back-end on 127.0.0.1:$APP_PORT"
SERVER_ADDRESS=127.0.0.1 SERVER_PORT="$APP_PORT" \
  SPRING_WEB_RESOURCES_STATIC_LOCATIONS="file:$UI_BUILD/" \
  java -jar "$JAR" >"$WORK/backend.log" 2>&1 &
PIDS+=($!)
wait_for "http://127.0.0.1:$APP_PORT/api/information/identifiers" 60 || { tail -30 "$WORK/backend.log"; exit 1; }

# ---------------------------------------------------------------------------------------------------------------
# 3. start the browser
# ---------------------------------------------------------------------------------------------------------------
BROWSER="$(find_chrome)"
log "Starting headless $(basename "$BROWSER") on 127.0.0.1:$CDP_PORT"
"$BROWSER" --headless=new --remote-debugging-address=127.0.0.1 --remote-debugging-port="$CDP_PORT" \
  --user-data-dir="$WORK/chrome-profile" --no-first-run --no-default-browser-check --disable-gpu \
  about:blank >"$WORK/chrome.log" 2>&1 &
PIDS+=($!)
wait_for "http://127.0.0.1:$CDP_PORT/json/version" 30 || { tail -30 "$WORK/chrome.log"; exit 1; }

# ---------------------------------------------------------------------------------------------------------------
# 4. run the scenarios
# ---------------------------------------------------------------------------------------------------------------
log "Running the UI smoke scenarios"
export CDP_HOST CDP_PORT APP_URL="http://127.0.0.1:$APP_PORT/"
if [ -n "${SCREENSHOTS_OUT:-}" ]; then rm -rf "$SCREENSHOTS_OUT"; mkdir -p "$SCREENSHOTS_OUT"; fi

status=0
run_driver || status=$?

if [ -n "${SCREENSHOTS_OUT:-}" ] && [ -n "$CONTAINER" ]; then
  docker cp "$CONTAINER:/tmp/screenshots/." "$SCREENSHOTS_OUT" 2>/dev/null || true
fi
[ -z "${SCREENSHOTS_OUT:-}" ] || echo "Screenshots: $SCREENSHOTS_OUT"
exit $status
