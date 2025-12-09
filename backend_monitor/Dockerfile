FROM maven:3.9-eclipse-temurin-21 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /backend_monitor/pom.xml
WORKDIR /backend_monitor
RUN mvn dependency:go-offline
COPY ./src/ /backend_monitor/src
WORKDIR /backend_monitor
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /backend_monitor
COPY --from=build /backend_monitor/target/backend_monitor-0.0.1-SNAPSHOT.jar /backend_monitor/backend_monitor.jar
EXPOSE 8080
CMD ["java", "-jar", "backend_monitor.jar"]