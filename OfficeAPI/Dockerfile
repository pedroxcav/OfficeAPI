FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-22 -y
COPY . .

RUN apt-get instal maven -y
RUN mvn clean install

FROM openjdk:22-jdk-slim

EXPOSE 8080

COPY --from=build /target/OfficeAPI-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar"]