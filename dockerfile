# Usamos imagen con Java 21
FROM eclipse-temurin:21-jdk-alpine

# Directorio dentro del contenedor
WORKDIR /app

# Copiamos el jar
COPY target/*.jar app.jar

# Exponemos el puerto
EXPOSE 8080

# Ejecutamos la app
ENTRYPOINT ["java", "-jar", "app.jar"]