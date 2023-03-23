FROM node:18-alpine3.16 as webapp

COPY front-end /app
WORKDIR /app

RUN npm install
RUN npm run build

FROM eclipse-temurin:17-jdk as backend
WORKDIR /app
COPY src build.gradle settings.gradle /app
MKDIR /app/src/main/resources/static
COPY --from=webapp /app/build/* /app/src/main/resources/static
RUN ./gradlew build

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=backend /app/build/libs/data-privacy-toolkit-rest-6.0.0.jar /app/dpt-demo.jar
CMD ["java", "-jar", "./dpt-demo.jar"]