FROM maven:3.9-eclipse-temurin-21 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /backend_device/pom.xml
WORKDIR /backend_device
RUN mvn dependency:go-offline
COPY ./src/ /backend_device/src
WORKDIR /backend_device
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /backend_device
COPY --from=build /backend_device/target/backend_device-0.0.1-SNAPSHOT.jar /backend_device/backend_device.jar
EXPOSE 8080
CMD ["java", "-jar", "backend_device.jar"]