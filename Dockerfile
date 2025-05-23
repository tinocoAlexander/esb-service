FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml  .

COPY src ./src

RUN mvn clean install -Dmaven.test.skip=true


FROM openjdk:8-jdk-alpine

#Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENV JAVA_TOOL_OPTIONS="-Xmx256m"

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]