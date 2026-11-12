FROM node:18-alpine3.16 as webapp

COPY front-end /app
WORKDIR /app

RUN npm install
RUN npm run build

FROM eclipse-temurin:17.0.21_9-jdk as backend
WORKDIR /app
COPY src build.gradle settings.gradle /app
MKDIR /app/src/main/resources/static
COPY --from=webapp /app/build/* /app/src/main/resources/static
RUN ./gradlew build

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
FROM registry.access.redhat.com/ubi10/ubi-minimal:10.3-1794499233

LABEL authors="Marco Simioni <marco.simioni@ibm.com>, Stefano Braghin <stefanob@ie.ibm.com>"

RUN microdnf install -y shadow-utils java-21-openjdk-headless && microdnf clean all \
    && groupadd -r dpt && useradd -r -g dpt -s /sbin/nologin dpt

WORKDIR /app

# Owned by root and not writable by the service user.
COPY --from=backend /app/build/libs/data-privacy-toolkit-rest-6.0.0.jar /app/dpt-demo.jar

ENV SERVER_PORT=8080

# Multipart uploads and Tomcat's working files go to /tmp: mount it as a tmpfs when running read-only.
EXPOSE 8080

USER dpt

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD ["/bin/bash", "-c", "exec 3<>/dev/tcp/${SERVER_ADDRESS:-127.0.0.1}/${SERVER_PORT}"]

CMD ["java", "-XX:MaxRAMPercentage=75", "-Djava.io.tmpdir=/tmp", "-jar", "/app/dpt-demo.jar"]
