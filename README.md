# iti0302-2024-backend
This is a RESTful API application for managing inventory. The application is being built
as a part of the ITI0302 Web Development course at TalTech.

### Prerequisites

Before you can run this application, you need to have the following installed:

*   [Java 21](https://adoptium.net/releases.html?variant=openjdk21&jvmVariant=hotspot) (or higher)
*   [PostgreSQL](https://www.postgresql.org/download/) (version 15.0 or higher)
*   [Docker](https://www.docker.com/products/docker-desktop/) (version 24.0 or higher)

### How to Run Your Application

Clone the Repository

```bash
git clone https://github.com/yourusername/iti0302-2024-backend.git
cd iti0302-2024-backend
```

Update the `application.properties` file with your database credentials:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
```

Update the `docker-compose.yaml` file with your database credentials:

```yaml
services:
  postgres:
    image: postgres:17.0-bookworm
    environment:
      - POSTGRES_USER=your_db_user
      - POSTGRES_PASSWORD=your_db_password
      - POSTGRES_DB=your_db_name
    ports:
      - '5432:5432'
    volumes:
      - ./postgres-data:/var/lib/postgresql/data
```
Before running, make sure that Docker Desktop is running.
To run the application, you can use the following commands:

```bash
docker compose up
```

```bash
./gradlew assemble
```

```bash
./gradlew bootRun
```

This command will start the Spring Boot application using the embedded server.

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
COPY build/libs/your-application-name.jar app.jar
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
docker build -t your/docker-repository:latest .
```

Make sure to push the image to your Docker repository.
```bash
docker push your/docker-repository:latest
```

### How to Run the Docker Container with both the database and the application

Make sure to have a properly configured `docker-compose.yaml` file.

It could look like this:

```yaml
services:
  postgres:
    image: postgres:17.1-bookworm
    environment:
      - POSTGRES_USER=your_db_user
      - POSTGRES_PASSWORD=your_db_password
      - POSTGRES_DB=your_db_name
    ports:
      - '5432:5432'
    volumes:
      - ./postgres-data:/var/lib/postgresql/data

  backend:
    image: your/docker-repository:latest
    depends_on:
      - postgres
    ports:
      - "8080:8080"
    volumes:
      - ./application.properties:/app/application.properties

```
Make sure to configure the `application.properties` file with your database credentials.
Can be done the same way as when running without docker.

Before running, make sure that Docker Desktop is running.


To run the Docker container, use the following command:

```bash
docker compose up
```

This command will start the container and map port 8080 of the container to port 8080 on your host machine.

### Technologies Used

* Java 21
* Spring Boot 3.3.5
* Spring Data JPA
* Spring Security
* PostgreSQL
* Lombok
* MapStruct
* JSON Web Tokens (JWT)
* Docker