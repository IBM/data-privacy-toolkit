# Build from the repository root, because the service depends on the library module:
#
#   docker build -f rest/Dockerfile -t dpt-rest .
#
# Stage 1: the web UI (rest/front-end).
FROM registry.access.redhat.com/ubi10/nodejs-24-minimal:latest AS webapp

WORKDIR /opt/app-root/src/front-end
COPY --chown=1001:0 rest/front-end/package.json rest/front-end/package-lock.json ./
# Install scripts of dependencies are not needed to build the UI and are not run.
RUN npm ci --ignore-scripts --no-audit --no-fund
COPY --chown=1001:0 rest/front-end/ ./
RUN npm run build

# Stage 2: the library (published to the local Maven repository) and the service, with the UI as static content.
FROM registry.access.redhat.com/ubi10/openjdk-21:latest AS backend

WORKDIR /home/default
COPY --chown=185:0 library/ library/
RUN cd library && ./gradlew publishToMavenLocal -x test --no-daemon

COPY --chown=185:0 rest/ rest/
COPY --from=webapp --chown=185:0 /opt/app-root/src/front-end/build/ rest/src/main/resources/static/
RUN cd rest && ./gradlew bootJar --no-daemon

# Runtime image: minimal UBI, unprivileged user, nothing writable by the application except /tmp.
#
# The application listens on 127.0.0.1:8080 unless told otherwise (SERVER_ADDRESS, SERVER_PORT). Inside a
# container it has to listen on all container interfaces to be reachable through a published port; publish
# that port on the host loopback interface only and run the container hardened:
#
#   docker run --rm \
#     -e SERVER_ADDRESS=0.0.0.0 -p 127.0.0.1:8080:8080 \
#     --read-only --tmpfs /tmp:rw,noexec,nosuid,size=64m \
#     --cap-drop=ALL --security-opt=no-new-privileges \
#     --memory=1g --pids-limit=256 \
#     dpt-rest
FROM registry.access.redhat.com/ubi10/ubi-minimal:10.2-1792742351

LABEL authors="Marco Simioni <marco.simioni@ibm.com>, Stefano Braghin <stefanob@ie.ibm.com>"

# shadow-utils is only needed to create the user: it is removed again, with its setuid helpers.
RUN microdnf install -y shadow-utils java-21-openjdk-headless \
    && groupadd -r -g 10001 dpt && useradd -r -u 10001 -g dpt -s /sbin/nologin dpt \
    && microdnf remove -y shadow-utils && microdnf clean all

WORKDIR /app

# Owned by root and not writable by the service user.
COPY --from=backend /home/default/rest/build/libs/data-privacy-toolkit-rest-*.jar /app/dpt-demo.jar

ENV SERVER_PORT=8080

# Multipart uploads and Tomcat's working files go to /tmp: mount it as a tmpfs when running read-only.
EXPOSE 8080

# Numeric, so that Kubernetes (runAsNonRoot) can verify that the user is not root.
USER 10001:10001

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD ["/bin/bash", "-c", "exec 3<>/dev/tcp/${SERVER_ADDRESS:-127.0.0.1}/${SERVER_PORT}"]

CMD ["java", "-XX:MaxRAMPercentage=75", "-Djava.io.tmpdir=/tmp", "-jar", "/app/dpt-demo.jar"]
