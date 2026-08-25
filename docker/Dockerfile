FROM registry.access.redhat.com/ubi10/ubi-minimal:10.2-1782798957

LABEL authors="Marco Simioni <marco.simioni@ibm.com>, Stefano Braghin <stefanob@ie.ibm.com>"

ENV INPUT=/input
ENV OUTPUT=/output
ENV CONFDIR=/config
ENV CONSISTENDIR=/consistency

RUN microdnf install -y java-21-openjdk-headless && microdnf clean all

WORKDIR /app

COPY ./build/libs/data-privacy-toolkit-cli-*-all.jar /app/data-privacy-toolkit-cli.jar

ENTRYPOINT ["/bin/sh", "-c"]
CMD ["exec java -jar /app/data-privacy-toolkit-cli.jar -i ${INPUT} -c ${CONFDIR}/config.json -o ${OUTPUT}"]
