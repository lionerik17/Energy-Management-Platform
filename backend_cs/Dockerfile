FROM maven:3.9-eclipse-temurin-21 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /backend_cs/pom.xml
WORKDIR /backend_cs
RUN mvn dependency:go-offline
COPY ./src/ /backend_cs/src
WORKDIR /backend_cs
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /backend_cs
COPY --from=build /backend_cs/target/backend_cs-0.0.1-SNAPSHOT.jar /backend_cs/backend_cs.jar
EXPOSE 8080
CMD ["java", "-jar", "backend_cs.jar"]