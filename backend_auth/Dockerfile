FROM maven:3.9-eclipse-temurin-21 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /backend_auth/pom.xml
WORKDIR /backend_auth
RUN mvn dependency:go-offline
COPY ./src/ /backend_auth/src
WORKDIR /backend_auth
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /backend_auth
COPY --from=build /backend_auth/target/backend_auth-0.0.1-SNAPSHOT.jar /backend_auth/backend_auth.jar
EXPOSE 8080
CMD ["java", "-jar", "backend_auth.jar"]