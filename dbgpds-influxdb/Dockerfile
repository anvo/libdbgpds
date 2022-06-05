# gradlew :dbgpds-influxdb:shadowJar
# docker build --tag=dbgpds-influxdb:latest .
# docker run dbgpds-influxdb
FROM gcr.io/distroless/java:11

COPY ./build/libs/dgbpds-influxdb-*-all.jar /app/dgbpds-influxdb-all.jar

WORKDIR /app
CMD ["dgbpds-influxdb-all.jar", "import", "--range=P4D"]

