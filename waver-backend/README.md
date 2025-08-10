# Waver Backend

Serverless function built with Quarkus that processes tutorial generation requests and stores results in MinIO/S3 storage.

## Overview

`waver-backend` is a cloud-native function that:
- **Receives Cloud Events**: Processes tutorial generation requests from waver-site
- **Uses waver-core**: Leverages the core tutorial generation engine
- **Processes Git Repositories**: Clones and analyzes remote code repositories
- **Stores Results**: Saves generated tutorials and metadata to MinIO/S3
- **Produces Metadata**: Generates `waver-config.json` files for each tutorial

## Architecture

### Core Components

#### Function Entry Points
- **`WaverFunqy`**: Main function entry point for cloud events
- **`WaverProcessEvent`**: Event model for tutorial generation requests

#### Processing Pipeline
- **`BackendProcessingService`**: Orchestrates the tutorial generation process
- **`MinioBucketInitializer`**: Sets up storage buckets and structure
- **`MinioUploaderTask`**: Uploads generated content to storage

#### Storage Integration
- **`MinioClientProducer`**: Creates and configures MinIO client
- **`MinioS3Source`**: S3-compatible storage operations
- **`MinioConfig`**: Configuration for MinIO connection

### Data Flow
```
Cloud Event → WaverFunqy → BackendProcessingService → waver-core → MinIO Storage
     ↓              ↓              ↓                    ↓           ↓
  Tutorial     Process Event   Orchestrate         Generate    Store Results
  Request      Validation      Pipeline           Tutorial    + Metadata
```

## Building

### Prerequisites
- Java 21 or higher
- Maven 3.8 or higher
- Docker (for native builds)

### Build Commands

#### JVM Build
```bash
# Build only waver-backend
mvn clean package -pl waver-backend

# Build with all dependencies
mvn clean package -pl waver-backend -am
```

#### Native Build
```bash
# Build native executable
mvn clean package -pl waver-backend -Pnative

# Build with container-based native compilation
mvn clean package -pl waver-backend -Pnative -Dquarkus.native.container-build=true
```

### Build Output
- **JVM JAR**: `target/quarkus-app/quarkus-run.jar`
- **Native Executable**: `target/waver-backend` (Linux/macOS) or `target/waver-backend.exe` (Windows)
- **Deployment Files**: `target/kubernetes/` directory for Kubernetes deployment

## Running

### Prerequisites
- LLM API keys (OpenAI or Gemini)
- MinIO or S3-compatible storage access
- Git repository access (for cloning repos)

### Environment Variables
```bash
# LLM Configuration
export OPENAI_API_KEY="your-openai-api-key"
export GEMINI_AI_KEY="your-gemini-api-key"

# MinIO Configuration
export MINIO_ENDPOINT="http://localhost:9000"
export MINIO_ACCESS_KEY="your-access-key"
export MINIO_SECRET_KEY="your-secret-key"
export MINIO_BUCKET="waver-bucket"

# Application Configuration
export WAVER_OUTPUT_FORMAT="MARKDOWN"
export WAVER_LLM_PROVIDER="Gemini"
```

### Running Locally

#### JVM Mode
```bash
# Run in development mode
mvn quarkus:dev -pl waver-backend

# Run packaged application
java -jar target/quarkus-app/quarkus-run.jar
```

#### Native Mode
```bash
# Run native executable
./target/waver-backend
```

### Configuration
The application can be configured via `application.properties` or environment variables:

```properties
# LLM Provider Configuration
waver.llm.provider=Gemini
waver.output.format=MARKDOWN

# MinIO Configuration
quarkus.minio.endpoint=http://localhost:9000
quarkus.minio.access-key=your-access-key
quarkus.minio.secret-key=your-secret-key
quarkus.minio.bucket=waver-bucket
```

## Deployment

### Docker Deployment
```bash
# Build Docker image
docker build -t waver-backend:latest .

# Run container
docker run -p 8080:8080 \
  -e OPENAI_API_KEY="your-key" \
  -e MINIO_ENDPOINT="http://minio:9000" \
  waver-backend:latest
```

### Kubernetes/OpenShift Deployment
```bash
# Apply Kubernetes manifests
kubectl apply -f target/kubernetes/

# Or use Kustomize
kubectl apply -k target/kubernetes/
```

### Knative Deployment
```bash
# Deploy as Knative service
kn service create waver-backend \
  --image waver-backend:latest \
  --env OPENAI_API_KEY="your-key"
```

## API Endpoints

### Cloud Event Endpoint
- **POST** `/`: Receives cloud events for tutorial generation
- **Content-Type**: `application/cloudevents+json`

### Health Checks
- **GET** `/q/health`: Application health status
- **GET** `/q/health/live`: Liveness probe
- **GET** `/q/health/ready`: Readiness probe

### Metrics
- **GET** `/q/metrics`: Prometheus metrics endpoint

## Development

### Project Structure
```
waver-backend/
├── src/main/java/dev/shaaf/waver/backend/
│   ├── config/           # Configuration classes
│   ├── minio/            # MinIO integration
│   ├── process/          # Processing services
│   ├── WaverFunqy.java  # Main function entry point
│   └── WaverProcessEvent.java
├── src/main/resources/   # Configuration files
└── pom.xml              # Maven configuration
```

### Running Tests
```bash
# Run all tests
mvn test -pl waver-backend

# Run with Quarkus dev mode
mvn quarkus:dev -pl waver-backend
```

### IDE Setup
1. Import as a Maven project
2. Ensure Java 21 is configured
3. Run `mvn quarkus:dev` for development mode
4. Main package: `dev.shaaf.waver.backend`

## Dependencies

### Core Dependencies
- **waver-core**: Core tutorial generation logic
- **Quarkus**: Cloud-native Java framework
- **LangChain4j**: LLM integration
- **MinIO Client**: S3-compatible storage

### Quarkus Extensions
- **quarkus-funqy-knative-events**: Cloud event processing
- **quarkus-minio**: MinIO integration
- **quarkus-resteasy-reactive**: HTTP endpoints
- **quarkus-smallrye-health**: Health checks

## Monitoring & Observability

### Health Checks
- Application health status
- Liveness and readiness probes
- Dependency health (MinIO, LLM providers)

### Metrics
- Request counts and response times
- Tutorial generation metrics
- Storage operation metrics

### Logging
- Structured logging with SLF4J
- Request/response logging
- Error tracking and debugging

## Troubleshooting

### Common Issues

#### MinIO Connection Failures
- Verify MinIO endpoint and credentials
- Check network connectivity
- Ensure bucket exists and is accessible

#### LLM API Errors
- Verify API keys and quotas
- Check network connectivity
- Review API rate limits

#### Memory Issues
- Increase JVM heap size for large repositories
- Use native builds for better memory efficiency
- Monitor memory usage during processing

## Related Components

- **[waver-core](../waver-core/README.md)**: Core logic used by the backend
- **[waver-cli](../waver-cli/README.md)**: Local CLI alternative
- **[waver-site](../waver-site/README.md)**: Web interface that triggers the backend

## Contributing

When contributing to waver-backend:

1. **Follow Quarkus best practices**
2. **Add comprehensive tests**
3. **Update configuration documentation**
4. **Ensure cloud-native compatibility**
5. **Test with different storage backends**

## License

This component is part of the Waver project and is distributed under the MIT License.
