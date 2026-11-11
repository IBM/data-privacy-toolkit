# UI smoke test

End-to-end check that the web UI in [`../front-end`](../front-end) still works against the real REST back-end.

It builds the UI, serves it from the Spring Boot application exactly as in production, and drives it with a headless
Chrome through the DevTools protocol. There are no test-framework dependencies: the driver uses only what Node 22+
ships (`fetch`, `WebSocket`).

```bash
./gradlew uiSmokeTest        # from rest/, builds the jar first
# or
ui-smoke/run.sh              # builds the jar with ./gradlew bootJar unless DPT_REST_JAR points to one
```

`uiSmokeTest` is intentionally **not** part of `./gradlew check`: it needs a browser and a Node toolchain.

## What it needs

| Requirement | Notes |
|---|---|
| Java 21 | to run the back-end |
| Chrome or Chromium | found automatically on macOS and Linux, or set `CHROME` |
| Node >= 22 **or** Docker | with no suitable local Node, the UI is built in `NODE_IMAGE` (default `registry.access.redhat.com/ubi9/nodejs-24-minimal`) |

Everything listens on `127.0.0.1` only (back-end on `APP_PORT`, default 18080; browser on `CDP_PORT`, default 19222),
works from a temporary directory, and is stopped and removed on exit.

## What a scenario checks

A scenario ([`scenarios.mjs`](scenarios.mjs)) is a named async function that clicks, selects and waits. It fails if a
step throws **or** if the page reports any of:

- an uncaught exception;
- a `console.error` or `console.warn`;
- a failed request or an HTTP status >= 400;
- an `alert()` / `confirm()` dialog (the UI uses `alert` to report back-end errors).

The current scenarios load every information page, open the not-found page, and walk the whole data-protection
workflow (load `simple.csv`, identify types, find vulnerabilities, protect, anonymize), asserting that each back-end
endpoint is actually called. Scenarios run in one browser tab, in order, because the workflow steps depend on each other.
The output ends with the list of back-end calls the UI made.

## Adding a scenario

Append to `scenarios` in [`scenarios.mjs`](scenarios.mjs). The helpers on `page` are documented in
[`cdp.mjs`](cdp.mjs): `route`, `click`, `choose`, `waitFor`, `waitForText`, `evaluate`, plus `expectApiCall(fragment)`.
`click` matches buttons (`button.btn`, and `a.btn` for link-styled buttons), not the plain navigation links.

## Running against an already running instance

```bash
APP_URL=http://127.0.0.1:8080/ CDP_HOST=127.0.0.1 CDP_PORT=9222 node smoke.mjs
```

with Chrome started by hand using `--headless=new --remote-debugging-port=9222`.
