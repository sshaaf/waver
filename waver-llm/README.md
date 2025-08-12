# Waver Core

The heart of the Waver platform containing the core logic, task pipeline framework, and tutorial generation engine.

## Overview

`waver-core` is the foundational module that provides:
- **Task Pipeline Framework**: Core abstractions for building processing pipelines
- **Tutorial Generation Engine**: Logic for analyzing code and generating structured tutorials
- **Code Analysis**: Abstractions identification, relationship analysis, and chapter organization
- **LLM Integration**: Support for multiple providers (OpenAI, Gemini) via LangChain4j

## Architecture

### Core Components

#### Task Pipeline Framework
- **`Task<T>`**: Base interface for all processing tasks, also handles connected Tasks
- **`TaskPipeline`**: Orchestrates the execution of multiple tasks
- **`PipelineContext`**: Carries state and data between tasks

#### Tutorial Generation
- **`TutorialWriter`**: Main orchestrator for tutorial generation
- **`AbstractionAnalyzer`**: Identifies code abstractions and patterns
- **`RelationshipAnalyzer`**: Analyzes relationships between code components
- **`ChapterOrganizer`**: Organizes content into logical chapters
- **`CodeCrawlerTask`**: Crawls and analyzes source code
- **`MetaInfoTask`**: Extracts metadata from repositories
- **`TechnicalWriterTask`**: Generates human-readable content

## Building

### Prerequisites
- Java 21 or higher
- Maven 3.8 or higher

### Build Commands

```bash
# Build only waver-core
mvn clean package -pl waver-core

# Build waver-core and install to local repository
mvn clean install -pl waver-core

# Build with tests
mvn clean test -pl waver-core
```

### Build Output
- **JAR File**: `target/waver-core-1.0-SNAPSHOT.jar`
- **Sources JAR**: `target/waver-core-1.0-SNAPSHOT-sources.jar`
- **Javadoc JAR**: `target/waver-core-1.0-SNAPSHOT-javadoc.jar`

## Development

### Project Structure
```
waver-core/
├── src/main/java/dev/shaaf/waver/
│   ├── config/           # Configuration classes
│   ├── core/            # Core pipeline framework
│   ├── files/           # File handling utilities
│   └── tutorial/        # Tutorial generation logic
│       ├── model/       # Data models
│       ├── prompt/      # LLM prompts and analyzers
│       └── task/        # Processing tasks
└── src/test/java/       # Unit tests
```

### Key Classes

#### Core Framework
- **`Task<T>`**: Base interface for all processing tasks
- **`TaskPipeline`**: Manages task execution flow
- **`PipelineContext`**: Carries data between tasks

#### Tutorial Generation
- **`TutorialWriter`**: Main tutorial generation orchestrator
- **`AbstractionAnalyzer`**: Identifies code abstractions
- **`RelationshipAnalyzer`**: Analyzes code relationships
- **`ChapterOrganizer`**: Organizes content into chapters

#### Configuration
- **`AppConfig`**: Main application configuration
- **`LLMProvider`**: LLM provider abstraction
- **`ModelProviderFactory`**: Factory for creating LLM providers

### Running Tests

```bash
# Run all tests
mvn test -pl waver-core

# Run specific test class
mvn test -pl waver-core -Dtest=TaskPipelineTest

# Run tests with debug output
mvn test -pl waver-core -X
```

### IDE Setup
1. Import the project as a Maven project
2. Ensure Java 21 is configured
3. Run `mvn clean install` to install dependencies
4. The main package is `dev.shaaf.waver`

## Dependencies

### Core Dependencies
- **LangChain4j**: LLM integration framework
- **Jackson**: JSON/YAML processing
- **JGit**: Git repository operations
- **Flexmark**: Markdown processing

### Testing Dependencies
- **JUnit 5**: Testing framework
- **AssertJ**: Fluent assertions

## Usage Examples

### Basic Task Pipeline
```java
TaskPipeline pipeline = new TaskPipeline();
pipeline.addTask(new CodeCrawlerTask());
pipeline.addTask(new IdentifyAbstractionsTask());
pipeline.addTask(new TechnicalWriterTask());

PipelineContext context = new PipelineContext();
context.put("inputPath", "./my-project");
context.put("outputPath", "./tutorials");

pipeline.execute(context);
```

### Custom Task Implementation
```java
public class MyCustomTask implements Task<String> {
    @Override
    public String execute(PipelineContext context) {
        String input = context.get("input");
        // Process input...
        return "processed result";
    }
}
```

## Contributing

When contributing to waver-core:

1. **Follow the existing architecture patterns**
2. **Implement the `Task<T>` interface for new processing steps**
3. **Add comprehensive unit tests**
4. **Update this README for new features**
5. **Ensure backward compatibility**

## Related Components

- **[waver-cli](../waver-cli/README.md)**: Command-line interface that uses waver-core
- **[waver-backend](../waver-backend/README.md)**: Serverless function that uses waver-core
- **[waver-site](../waver-site/README.md)**: Web interface that triggers waver-backend

## License

This component is part of the Waver project and is distributed under the MIT License.
