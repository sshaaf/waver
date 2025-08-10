# Waver - Maven Build

This project has been converted from Gradle to Maven with a multi-module structure.

## Project Structure

```
waver/
├── pom.xml                 # Parent POM
├── core/
│   ├── pom.xml            # Core module POM
│   └── src/
├── cli/
│   ├── pom.xml            # CLI module POM
│   └── src/
└── backend/
    ├── pom.xml            # Backend module POM
    └── src/
```

## Prerequisites

- Java 21 or higher
- Maven 3.8+ 
- GraalVM (for native image builds)

## Building the Project

### Build all modules
```bash
mvn clean install
```

### Build specific module
```bash
# Build core only
mvn clean install -pl waver-core

# Build CLI only
mvn clean install -pl waver-cli

# Build backend only
mvn clean install -pl waver-backend
```

### Run tests
```bash
# Run all tests
mvn test

# Run tests for specific module
mvn test -pl waver-core
mvn test -pl waver-cli
mvn test -pl waver-backend
```

## Module Details

### Core Module
Contains the core functionality shared across all modules:
- Task pipeline implementation
- Configuration management
- File processing utilities
- Tutorial generation logic

**Build:**
```bash
mvn clean install -pl waver-core
```

### CLI Module
Command-line interface with native image support:
- Picocli-based CLI
- Native image compilation
- Executable JAR with dependencies

**Build:**
```bash
# Regular JAR
mvn clean package -pl waver-cli

# Native image
mvn clean package -pl waver-cli -Pnative

# Run CLI
mvn exec:java -pl waver-cli -Dexec.mainClass="dev.shaaf.waver.cli.Main"
```

### Backend Module
Quarkus-based backend services:
- REST endpoints
- MinIO integration
- Event processing
- Health checks

**Build:**
```bash
# Regular JAR
mvn clean package -pl waver-backend

# Native image
mvn clean package -pl waver-backend -Pnative

# Run in dev mode
mvn quarkus:dev -pl waver-backend
```

## Common Maven Commands

### Clean and Build
```bash
mvn clean install
```

### Skip Tests
```bash
mvn clean install -DskipTests
```

### Run Integration Tests
```bash
mvn clean verify
```

### Generate Site Documentation
```bash
mvn site
```

### Dependency Tree
```bash
mvn dependency:tree
```

### Update Dependencies
```bash
mvn versions:display-dependency-updates
```

## Native Image Building

### CLI Native Image
```bash
mvn clean package -pl waver-cli -Pnative
```

### Backend Native Image
```bash
mvn clean package -pl waver-backend -Pnative
```

## Development

### IDE Setup
The project is configured for:
- IntelliJ IDEA
- Eclipse
- VS Code

### Running in Development Mode

**Backend:**
```bash
mvn quarkus:dev -pl waver-backend
```

**CLI:**
```bash
mvn exec:java -pl waver-cli -Dexec.mainClass="dev.shaaf.waver.cli.Main" -Dexec.args="your-args"
```

## Migration Notes

This project was migrated from Gradle to Maven. Key changes:

1. **Build System**: Gradle → Maven
2. **Dependency Management**: Centralized in parent POM
3. **Plugin Configuration**: Maven-specific plugins
4. **Native Image**: GraalVM native-maven-plugin
5. **Quarkus**: Maven plugin configuration

## Troubleshooting

### Common Issues

1. **Java Version**: Ensure Java 21 is used
2. **Maven Version**: Use Maven 3.8+
3. **Native Image**: Requires GraalVM installation
4. **Dependencies**: Run `mvn dependency:resolve` to download all dependencies

### Clean Build
If you encounter build issues:
```bash
mvn clean
rm -rf ~/.m2/repository/dev/shaaf/waver
mvn clean install
``` 