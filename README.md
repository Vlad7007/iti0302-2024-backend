# iti0302-2024-backend



### How to Run Your Application

To run the application, you can use the following command:

```bash
./gradlew bootRun
```

This command will start the Spring Boot application using the embedded server.

### How to Build It

To build the application, execute the following command:

```bash
./gradlew build
```

This will compile the project and package it into a JAR file located in the `build/libs` directory.

### How to Create a Docker Container

To create a [Docker](https://www.docker.com/products/docker-desktop/) container for your application, you need to have a `Dockerfile` in your project root. Here is an example of what it might look like:

```dockerfile
# Base image with java 21
FROM --platform=linux/amd64 eclipse-temurin:21-jre-alpine
# Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
# Set working directory
WORKDIR /app
# Copy the jar file
COPY build/libs/iti0302-2024-backend-0.0.1-SNAPSHOT.jar app.jar
# Set ownership of the jar file
RUN chown appuser:appgroup app.jar
# Switch to non-root user
USER appuser
# Expose the port your application runs on (adjust as needed)
EXPOSE 8080
# Use ENTRYPOINT for the main command and CMD for default arguments
ENTRYPOINT ["java"]
CMD ["-Dspring.config.location=classpath:/application.properties,file:/app/application.properties", "-jar", "app.jar"]

```

Replace `your-application-name.jar` with the actual name of your JAR file.

Build the Docker image using the following command:

```bash
docker build -t your-image-name .
```

### How to Run the Docker Container

To run the Docker container, use the following command:

```bash
docker run -p 8080:8080 your-image-name
```

This command will start the container and map port 8080 of the container to port 8080 on your host machine.

Make sure to replace `your-image-name` with the name you used when building the Docker image. Adjust the port numbers if your application uses a different port.
