# IBM Data Privacy Toolkit - REST service and web UI

> **Example code, not for production use.**
> The REST API and the web UI in this module are provided as an example of how to expose the toolkit's
> functionality over HTTP. They are not meant to be deployed as production code.
>
> In particular:
> - the API has **no authentication or authorization**: anyone who can reach the port can call every endpoint;
> - the request validation is limited to what is needed to keep the demo safe to run locally and is not a
>   substitute for a security review;
> - the web UI is a demonstration front end and is not maintained as a product.
>
> If you need to offer this functionality as a service, use the [Java library](../library) in your own
> application and add the authentication, authorization, TLS, auditing and operational controls your
> environment requires.

## Running the example

The service listens on `127.0.0.1:8080` by default, so it is reachable from the local machine only.
Use the environment variables `SERVER_ADDRESS` and `SERVER_PORT` to change this.

```bash
cd ../library && ./gradlew publishToMavenLocal
cd ../rest && ./gradlew bootRun
```

Binding to a non-loopback address exposes an unauthenticated API to the network. If you run the service
in a container, set `SERVER_ADDRESS=0.0.0.0` inside the container and publish the port on the host
loopback interface only (`-p 127.0.0.1:8080:8080`). See the comment above the runtime stage of the
[Dockerfile](Dockerfile) for a hardened `docker run` command.

### Container image

The [Dockerfile](Dockerfile) builds the web UI, the library and this service from source (no local Node or
Java needed) and produces a minimal UBI image that runs as an unprivileged user. It must be built from the
repository root, because the service depends on the [library](../library) module:

```bash
docker build -f rest/Dockerfile -t dpt-rest .
```

## Testing the web UI

`./gradlew uiSmokeTest` builds the web UI in [`front-end`](front-end), serves it from this application and drives it
with a headless Chrome, failing on JavaScript errors, failed requests or error dialogs. It needs Chrome or Chromium
and either Node 22+ or Docker, so it is not part of `./gradlew check`. See [`ui-smoke/README.md`](ui-smoke/README.md).

## Masking configuration

`POST /api/feature/mask/{hasHeader}/{useCompound}` accepts the masking configuration from the request.
Options that reference server-side resources (`persistence.*`, `dictionaryBased.mask.filename`,
`freetext.mask.*Filename`) cannot be changed by the caller, and all other options must match the type of the
server default. Requests that do not comply are rejected with HTTP 400.
