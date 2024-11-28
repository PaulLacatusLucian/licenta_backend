# Folosim imaginea oficială OpenJDK
FROM openjdk:17-jdk-slim

# Setăm directorul de lucru
WORKDIR /app

# Copiem aplicația compilată
COPY target/cafeteria-plugin-0.0.1-SNAPSHOT.jar app.jar

# Expunem portul pe care rulează aplicația
EXPOSE 8080

# Comandă pentru a rula aplicația
CMD ["java", "-jar", "app.jar"]

