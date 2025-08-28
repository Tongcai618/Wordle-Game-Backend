# Run JAR
FROM eclipse-temurin:17-jdk
LABEL authors="tongcai"

# Copy the built JAR file into the container
COPY target/*.jar app.jar

# Expose the port my Spring boot app runs on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app.jar"]