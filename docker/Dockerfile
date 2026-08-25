FROM registry.access.redhat.com/ubi10/ubi-minimal:10.2-1782798957

LABEL authors="Marco Simioni <marco.simioni@ibm.com>, Stefano Braghin <stefanob@ie.ibm.com>"

ENV INPUT=/input \
    OUTPUT=/output \
    CONFDIR=/config \
    CONSISTENTDIR=/consistency

RUN microdnf install -y shadow-utils java-21-openjdk-headless && microdnf clean all \
    && groupadd -r dpt && useradd -r -g dpt -s /sbin/nologin dpt \
    && mkdir -p /input /output /config /consistency \
    && chown -R dpt:dpt /input /output /config /consistency

WORKDIR /app

COPY ./build/libs/data-privacy-toolkit-cli-*-all.jar /app/data-privacy-toolkit-cli.jar

RUN chown dpt:dpt /app/data-privacy-toolkit-cli.jar

VOLUME ["/input", "/output", "/config", "/consistency"]

USER dpt

ENTRYPOINT ["/bin/sh", "-c"]
CMD ["exec java -jar /app/data-privacy-toolkit-cli.jar -i ${INPUT} -c ${CONFDIR}/config.json -o ${OUTPUT}"]