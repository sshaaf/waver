# Waver CLI

Command Line Interface for Waver - a tool for generating tutorials from code repositories using the waver-core engine.

## Overview

`waver-cli` is a standalone command-line tool that leverages `waver-core` to generate tutorials from:
- Local source code directories
- Git repositories (GitHub, GitLab, etc.)
- Individual code files

It provides a developer-friendly interface for generating tutorials locally without requiring the full web infrastructure.

## Features

- **Local Processing**: Generate tutorials from local directories or Git repositories
- **Multiple Output Formats**: Markdown, HTML, and PDF generation
- **Native Builds**: GraalVM native-image compilation for optimal performance
- **Uber JAR**: Self-contained executable with all dependencies
- **Progress Indicators**: Visual feedback for long-running operations
- **Colorized Output**: Enhanced readability with ANSI color support
- **Shell Auto-completion**: Bash and Zsh completion support

## Building

### Prerequisites
- Java 21 or higher
- Maven 3.8 or higher
- For native builds: GraalVM 21+ with native-image support

### Build Commands

#### Regular Build (JAR)
```bash
# Build only waver-cli
mvn clean package -pl waver-cli

# Build with all dependencies
mvn clean package -pl waver-cli -am
```

This creates a shaded JAR file at `target/waver-cli-1.0-SNAPSHOT.jar` that includes all dependencies.

#### Native Build (Executable)
```bash
# Build native executable
mvn clean package -pl waver-cli -Pnative

# Build with container-based native compilation
mvn clean package -pl waver-cli -Pnative -Dquarkus.native.container-build=true
```

This creates a native executable at `target/waver-cli` (Linux/macOS) or `target/waver-cli.exe` (Windows).

**Requirements for native build:**
- GraalVM 21+ with native-image support
- C compiler (gcc/clang)
- Sufficient memory (4GB+ recommended)
- Docker (for container-based builds)

### Build Output
- **JAR File**: `target/waver-cli-1.0-SNAPSHOT.jar` (shaded with dependencies)
- **Native Executable**: `target/waver-cli` (Linux/macOS) or `target/waver-cli.exe` (Windows)

## Running

### Prerequisites
- LLM API keys (OpenAI or Gemini)
- Input source code or Git repository URL

### Environment Variables
```bash
# For OpenAI
export OPENAI_API_KEY="your-openai-api-key"

# For Gemini
export GEMINI_AI_KEY="your-gemini-api-key"
```

### Running the JAR
```bash
java -jar target/waver-cli-1.0-SNAPSHOT.jar [options]
```

### Running the Native Executable
```bash
./target/waver-cli [options]
```

## Usage

### Basic Commands

#### Generate Tutorial from Local Directory
```bash
waver --input ./my-project \
      --output ./tutorials \
      --project-name "My Project" \
      --llm-provider Gemini
```

#### Generate Tutorial from Git Repository
```bash
waver --input https://github.com/user/repo.git \
      --output ./tutorials \
      --project-name "Repository Name" \
      --llm-provider OpenAI
```

#### Generate Tutorial with HTML Output
```bash
waver --input ./my-project \
      --output ./tutorials \
      --project-name "My Project" \
      --llm-provider Gemini \
      --format HTML
```

### Command-Line Options

| Option | Description | Required | Default |
|--------|-------------|----------|---------|
| `--input <path>` | Input source code path or Git URL | Yes | - |
| `--output <directory>` | Output directory for tutorials | Yes | - |
| `--project-name <name>` | Name of the project | Yes | - |
| `--llm-provider <provider>` | LLM provider (OpenAI, Gemini) | Yes | - |
| `--format <format>` | Output format (MARKDOWN, HTML, PDF) | No | MARKDOWN |
| `-v, --verbose` | Enable verbose output | No | false |
| `-h, --help` | Show help message | No | - |
| `-V, --version` | Print version information | No | - |

### Output Formats

- **MARKDOWN**: Standard Markdown files (default)
- **HTML**: HTML files with styling
- **PDF**: PDF documents (requires additional dependencies)

## Development

### Project Structure
```
waver-cli/
├── src/main/java/dev/shaaf/waver/cli/
│   ├── log/              # Logging configuration
│   └── Main.java         # Main CLI entry point
├── src/main/resources/   # Configuration files
└── pom.xml              # Maven configuration
```

### Running from IDE
The main class is `dev.shaaf.waver.cli.Main`.

### Testing
```bash
# Run all tests
mvn test -pl waver-cli

# Run specific test
mvn test -pl waver-cli -Dtest=MainTest

# Run with debug output
mvn test -pl waver-cli -X
```

### Profiles

- **default**: Builds JAR with all dependencies shaded
- **native**: Builds native executable using GraalVM native-image

## Dependencies

### Core Dependencies
- **waver-core**: Core tutorial generation logic
- **LangChain4j**: LLM integration framework
- **Picocli**: Command-line argument parsing
- **SLF4J**: Logging framework

### Build Dependencies
- **Maven Shade Plugin**: Creates uber JAR
- **GraalVM Native Image**: Native compilation support

## Troubleshooting

### Common Issues

#### Native Build Failures
- Ensure GraalVM is properly installed with native-image support
- Check available memory (4GB+ recommended)
- Use container-based builds for consistent environments

#### LLM API Errors
- Verify API keys are correctly set in environment variables
- Check API quota and rate limits
- Ensure network connectivity to LLM providers

#### Memory Issues
- Increase JVM heap size: `java -Xmx4g -jar target/waver-cli-1.0-SNAPSHOT.jar`
- Use native executable for better memory efficiency

## Related Components

- **[waver-core](../waver-core/README.md)**: Core logic used by the CLI
- **[waver-backend](../waver-backend/README.md)**: Serverless function for cloud processing
- **[waver-site](../waver-site/README.md)**: Web interface for tutorial generation

## Contributing

When contributing to waver-cli:

1. **Follow the existing CLI patterns**
2. **Add comprehensive help text for new options**
3. **Include unit tests for new functionality**
4. **Update this README for new features**
5. **Ensure backward compatibility**

## License

This component is part of the Waver project and is distributed under the MIT License.

