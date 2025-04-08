FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file built by Maven from the Jenkins workspace into the container
COPY target/XPerienceServer-1.0-xps.jar app.jar

# Run the server
ENTRYPOINT ["java", "-jar", "app.jar"]

