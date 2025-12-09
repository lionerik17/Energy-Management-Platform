FROM maven:3.9-eclipse-temurin-21 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /backend_ws/pom.xml
WORKDIR /backend_ws
RUN mvn dependency:go-offline
COPY ./src/ /backend_ws/src
WORKDIR /backend_ws
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /backend_ws
COPY --from=build /backend_ws/target/backend_ws-0.0.1-SNAPSHOT.jar /backend_ws/backend_ws.jar
EXPOSE 8080
CMD ["java", "-jar", "backend_ws.jar"]