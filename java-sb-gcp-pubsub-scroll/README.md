# Java Spring Boot GCP Pub/Sub Scroll

A Spring Boot application that integrates with Google Cloud Pub/Sub for message processing and publishing.

## Prerequisites

Before running this project, ensure you have the following installed:

- **Java 24** 
  - The project uses Java 24 as specified in `.sdkmanrc`
  - You can install it using SDKMAN: `sdk install java`
- **Gradle** (version 8.0 or later)
  - The project includes Gradle Wrapper, so you don't need to install Gradle separately

## Project Structure

```
java-sb-gcp-pubsub-scroll/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/sougat818/scroll/java_sb_gcp_pubsub_scroll/
│   │   │       └── JavaSbGcpPubsubScrollApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/
│           └── com/sougat818/scroll/java_sb_gcp_pubsub_scroll/
│               ├── JavaSbGcpPubsubScrollApplicationTests.java
│               ├── TestcontainersConfiguration.java
│               └── TestJavaSbGcpPubsubScrollApplication.java
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
└── .sdkmanrc
```

## Getting Started

### 1. Verify Java Version

Ensure you're using Java 24:

```bash
sdk env install
java -version
```

### 2. Build the Project

Using Gradle Wrapper (recommended):

```bash
# On macOS/Linux
./gradlew build

# On Windows
gradlew.bat build
```

### 3. Run the Application Using Gradle Wrapper

```bash
# On macOS/Linux
./gradlew bootRun

# On Windows
gradlew.bat bootRun
```

### 4. Verify the Application

The application should start on the default Spring Boot port (8080). You can verify it's running by:

- Checking the actuator endpoints at `http://localhost:8080/actuator`
- Using the included `endpoints.http` file to test endpoints directly in your editor

## Actuator Endpoints

This application includes Spring Boot Actuator for monitoring and management. The following endpoints are available:

| Endpoint | Description | URL |
|----------|-------------|-----|
| **Discovery** | Lists all available actuator endpoints | `GET /actuator` |
| **Health** | Application health information | `GET /actuator/health` |
| **Info** | Application information | `GET /actuator/info` |
| **Environment** | Environment variables and properties | `GET /actuator/env` |
| **Config Props** | Configuration properties | `GET /actuator/configprops` |
| **Metrics** | Application metrics | `GET /actuator/metrics` |
| **JVM Memory** | JVM memory usage | `GET /actuator/metrics/jvm.memory.used` |
| **HTTP Requests** | HTTP request metrics | `GET /actuator/metrics/http.server.requests` |

### Testing Actuator Endpoints

You can test these endpoints using:
1. The included `endpoints.http` file in your IDE
2. cURL commands
3. Any HTTP client (Postman, Insomnia, etc.)

Example cURL commands:
```bash
# Check application health
curl http://localhost:8080/actuator/health

# Get application info
curl http://localhost:8080/actuator/info

```


## Testing

Run the test suite:

```bash
./gradlew test
```

The project includes:
- Unit tests
- Integration tests with Testcontainers
- Spring Boot application tests

## Development

### IDE Setup

This project should work well with:
- IntelliJ IDEA
- Eclipse
- VS Code with Java extensions

However, it was created with Cursor.

### Testing Endpoints with .http Files

The project includes an `endpoints.http` file that allows you to test API endpoints directly from your editor:

1. **Install HTTP Client Extension** (if not already installed):
   - In Cursor: Install an HTTP client extension from the marketplace
   - In IntelliJ IDEA: Built-in support
   - In VS Code: Install "REST Client" extension

2. **Run Requests**:
   - Open `endpoints.http` in your editor
   - Click the "Send Request" button next to each request
   - View the response directly in the editor

The `endpoints.http` file includes:
- Actuator endpoint test (`/actuator`) 