#Etapa 1: Compilar con Maven
FROM maven:3.6.9-eclipse-temurin-17 AS builder

WORKDIR /app

#Copiar archivos del proyecto y compilar
COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

FROM adoptopenjdk/openjdk8:alpine

#Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]