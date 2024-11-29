# iti0302-2024-backend
This is a RESTful API application for managing inventory. The application is being built
as a part of the ITI0302 Web Development course at TalTech.

### Prerequisites

Before you can run this application, you need to have the following installed:

*   [Java 21](https://adoptium.net/releases.html?variant=openjdk21&jvmVariant=hotspot) (or higher)
*   [PostgreSQL](https://www.postgresql.org/download/) (version 15.0 or higher)
*   [Docker](https://www.docker.com/products/docker-desktop/) (optional)


### How to Run Your Application

To run the application, you can use the following commands:

Update the `application.properties` file with your database credentials:

   ```
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
   spring.datasource.username=your_db_username
   spring.datasource.password=your_db_password
   ```

```bash
docker compose up
```

```bash
./gradlew bootRun
```

This command will start the Spring Boot application using the embedded server.

### How to Build It

To build the application, execute the following command:

```bash
./gradlew assemble
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
docker build -t vnikif/inventory-juggler:backend .
```

### How to Run the Docker Container

To run the Docker container, use the following command:

```bash
docker run -p 8080:8080 vnikif/inventory-juggler
```

This command will start the container and map port 8080 of the container to port 8080 on your host machine.

### Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/iti0302-2024-backend.git
   cd iti0302-2024-backend
   ```

2. **Configure the Database**

   Update the `application.properties` file with your database credentials:

   ```
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
   spring.datasource.username=your_db_username
   spring.datasource.password=your_db_password
   ```

3. **Install Java 21**

    Download and install Java 21 

4. **Build the Project**

   Run the following command to build the project:

   ```bash
   ./gradlew assemble
   ```

5. **Run the Application**

   Start the application using:

   ```bash
   ./gradlew bootRun
   ```

6. **Access the Application**

   Open your browser and navigate to `http://localhost:8080` to access the application.


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