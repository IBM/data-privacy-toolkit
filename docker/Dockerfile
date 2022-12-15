FROM adoptopenjdk:11-jre-hotspot

LABEL authors="Marco Simioni <marco.simioni@ibm.com>, Stefano Braghin <stefanob@ie.ibm.com>"

ENV INPUT=/input
ENV OUTPUT=/output
ENV CONFDIR=/config
ENV CONSISTENDIR=/consistency

WORKDIR /app

COPY ./build/libs/data-privacy-toolkit-cli-*-all.jar /app/data-privacy-toolkit-cli.jar

CMD java -jar /app/data-privacy-toolkit-cli.jar -i ${INPUT} -c ${CONFDIR}/config.json -o ${OUTPUT}
