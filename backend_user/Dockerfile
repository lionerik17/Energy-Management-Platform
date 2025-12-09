FROM maven:3.9-eclipse-temurin-21 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /backend_user/pom.xml
WORKDIR /backend_user
RUN mvn dependency:go-offline
COPY ./src/ /backend_user/src
WORKDIR /backend_user
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /backend_user
COPY --from=build /backend_user/target/backend_user-0.0.1-SNAPSHOT.jar /backend_user/backend_user.jar
EXPOSE 8080
CMD ["java", "-jar", "backend_user.jar"]