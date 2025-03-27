FROM openjdk:8-jdk-alpine

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar lo que se genera en la carpeta target que tengo .jar
COPY target/*.jar app.jar

# Exponer el puerto 8081
EXPOSE 8081

ENTRYPOINT [ "java", "-jar", "app.jar" ]