<!-- For a centered logo -->
<p align="center">
  <img src=".github/assets/waver-128.png" alt="Project Logo" width="128">
</p>
<h2 align="center">
  <b> Waver - An easy to use code tutorial generator </b>
</h2>

Waver is a command-line tool that generates code tutorials from source code using Large Language Models (LLMs). It analyzes the source code, identifies abstractions and relationships, and generates a structured tutorial with chapters.


> **AI-Powered Code Documentation Engine** - Transform your codebase into comprehensive technical tutorials using state-of-the-art Large Language Models

[![Java 21+](https://img.shields.io/badge/Java-21+-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![LangChain4j](https://img.shields.io/badge/LangChain4j-1.0.0-blue.svg)](https://docs.langchain4j.dev/)
[![Native Image](https://img.shields.io/badge/GraalVM-Native-green.svg)](https://www.graalvm.org/latest/reference-manual/native-image/)
[![Maven Central](https://img.shields.io/badge/Maven-Central-red.svg)](https://central.sonatype.com/)

## 🚀 What is Waver?

Waver is a sophisticated **code analysis and documentation generation tool** that leverages advanced AI models to automatically create in-depth technical tutorials from your source code. Built on a **robust pipeline architecture**, it combines static code analysis with intelligent LLM processing to produce documentation that actually understands your software's architecture.

### 🔥 Technical Highlights

- **🧠 Multi-Stage AI Pipeline**: 6-stage processing pipeline with specialized tasks
- **⚡ Native Binary Support**: GraalVM native compilation for lightning-fast execution  
- **🎯 Smart Code Analysis**: Deep abstraction and relationship detection
- **🔌 Pluggable LLM Providers**: OpenAI GPT models and Google Gemini support
- **📊 Multiple Output Formats**: Markdown, HTML, and PDF generation
- **🏗️ Production-Ready**: Built with enterprise patterns and error handling

## 🏛️ Architecture Overview

```
Source Code → Pipeline → Generated Tutorial
     ↓
┌─────────────────────────────────────────────────────┐
│  📂 CodeCrawlerTask        │  Filesystem analysis   │
│  🔍 IdentifyAbstractionsTask │  Pattern recognition │  
│  🔗 IdentifyRelationshipsTask│  Dependency mapping  │
│  📚 ChapterOrganizerTask    │  Content structuring  │
│  ✍️  TechnicalWriterTask    │  AI content generation│
│  📝 MetaInfoTask           │  Metadata & navigation │
└─────────────────────────────────────────────────────┘
```

## ⚡ Quick Start

### Prerequisites

- **Java 21+** (LTS recommended)
- **Maven 3.9+** 
- **API Key** for your chosen LLM provider

### Installation & Build

```bash
# Clone the repository
git clone <your-repo-url>
cd waver

# Build executable JAR
mvn clean package

# Or build native binary for optimal performance
mvn clean package -Pnative
```

### Environment Setup

```bash
# For OpenAI (recommended for complex codebases)
export OPENAI_API_KEY="sk-your-openai-api-key-here"

# For Google Gemini (recommended for cost optimization)
export GEMINI_AI_KEY="your-gemini-api-key-here"
```

## 🔧 Command Reference

### Basic Syntax
```bash
java -jar target/waver-cli-0.1.0.jar [OPTIONS]
# or with native binary:
./target/waver-cli [OPTIONS]
```

### Required Parameters

| Parameter | Short | Description | Example |
|-----------|-------|-------------|---------|
| `--input` | - | Source code directory | `--input ./src/main/java` |
| `--output` | - | Output directory | `--output ./docs` |
| `--type` | `-t` | Generation type | `--type tutorial` |
| `--llm-provider` | - | AI model provider | `--llm-provider OpenAI` |

### Optional Parameters

| Parameter | Short | Description | Default | Example |
|-----------|-------|-------------|---------|---------|
| `--verbose` | `-v` | Debug logging | `false` | `-v` |
| `--format` | - | Output format | `MARKDOWN` | `--format PDF` |
| `--help` | `-h` | Show help | - | `-h` |
| `--version` | - | Show version | - | `--version` |

### Generation Types
- `tutorial` ✅ **Available**: Comprehensive code tutorials
- `documentation` ⏳ **Coming Soon**: API documentation
- `blog` ⏳ **Coming Soon**: Blog post generation

### LLM Providers
- `OpenAI` ✅ **GPT-3.5/4**: Best for complex analysis
- `Gemini` ✅ **Google AI**: Cost-effective option

### Output Formats  
- `MARKDOWN` ✅ **Default**: GitHub/GitLab ready
- `HTML` ✅ **Web**: Styled documentation
- `PDF` ✅ **Print**: Professional reports

## 💻 Usage Examples

### 1. Basic Tutorial Generation
```bash
java -jar target/waver-cli-0.1.0.jar \
  --input ./src/main/java \
  --output ./tutorials \
  --type tutorial \
  --llm-provider OpenAI
```

### 2. Spring Boot Project with Verbose Logging
```bash
java -jar target/waver-cli-0.1.0.jar \
  --input ./spring-boot-app/src \
  --output ./documentation \
  --type tutorial \
  --llm-provider Gemini \
  --verbose
```

### 3. Generate PDF Documentation
```bash
java -jar target/waver-cli-0.1.0.jar \
  --input ./microservice \
  --output ./reports \
  --type tutorial \
  --llm-provider OpenAI \
  --format PDF
```

### 4. Native Binary Execution (Faster)
```bash
./target/waver-cli \
  --input ./complex-system/backend \
  --output ./technical-docs \
  --type tutorial \
  --llm-provider Gemini \
  --format HTML \
  --verbose
```

### 5. Multi-Module Maven Project
```bash
java -jar target/waver-cli-0.1.0.jar \
  --input ./enterprise-app \
  --output ./team-docs \
  --type tutorial \
  --llm-provider OpenAI \
  --verbose
```

## 🔄 CI/CD Integration

### GitHub Actions Workflow

```yaml
name: 📚 Auto-Generate Documentation
on:
  push:
    branches: [main, develop]
    paths: ['src/**', 'pom.xml']
  pull_request:
    branches: [main]

jobs:
  generate-docs:
    runs-on: ubuntu-latest
    
    steps:
    - name: 🚀 Checkout Code
      uses: actions/checkout@v4
      
    - name: ☕ Setup Java 21
      uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'
        cache: 'maven'
        
    - name: 🔨 Build Waver Native Binary
      run: |
        mvn clean package -Pnative -DskipTests
        
    - name: 📖 Generate Technical Documentation
      run: |
        ./target/waver-cli \
          --input ./src \
          --output ./generated-docs \
          --type tutorial \
          --llm-provider OpenAI \
          --format MARKDOWN \
          --verbose
      env:
        OPENAI_API_KEY: ${{ secrets.OPENAI_API_KEY }}
        
    - name: 📤 Deploy to GitHub Pages
      uses: peaceiris/actions-gh-pages@v3
      if: github.ref == 'refs/heads/main'
      with:
        github_token: ${{ secrets.GITHUB_TOKEN }}
        publish_dir: ./generated-docs
        
    - name: 💬 Comment on PR
      if: github.event_name == 'pull_request'
      uses: actions/github-script@v7
      with:
        script: |
          github.rest.issues.createComment({
            issue_number: context.issue.number,
            owner: context.repo.owner,
            repo: context.repo.repo,
            body: '📚 Documentation has been generated! Check the artifacts below.'
          })
```

### GitLab CI Pipeline

```yaml
stages:
  - build
  - document
  - deploy

variables:
  MAVEN_OPTS: "-Dmaven.repo.local=$CI_PROJECT_DIR/.m2/repository"

cache:
  paths:
    - .m2/repository/

build-waver:
  stage: build
  image: bellsoft/liberica-openjdk-alpine:21
  script:
    - mvn clean package -Pnative -DskipTests
  artifacts:
    paths:
      - target/waver-cli
    expire_in: 1 hour

generate-docs:
  stage: document
  image: bellsoft/liberica-openjdk-alpine:21
  dependencies:
    - build-waver
  script:
    - chmod +x target/waver-cli
    - ./target/waver-cli 
        --input ./src 
        --output ./documentation 
        --type tutorial 
        --llm-provider OpenAI 
        --format HTML
        --verbose
  artifacts:
    paths:
      - documentation/
    expire_in: 1 week
  only:
    - main
    - develop
```

### Docker Integration

```dockerfile
# Multi-stage build for minimal container size
FROM bellsoft/liberica-openjdk-alpine:21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -Pnative -DskipTests

FROM alpine:latest
RUN apk --no-cache add ca-certificates
COPY --from=builder /app/target/waver-cli /usr/local/bin/waver
ENTRYPOINT ["waver"]
```

```bash
# Build and run in container
docker build -t waver-cli .
docker run --rm \
  -v $(pwd)/src:/input \
  -v $(pwd)/docs:/output \
  -e OPENAI_API_KEY="$OPENAI_API_KEY" \
  waver-cli \
  --input /input \
  --output /output \
  --type tutorial \
  --llm-provider OpenAI
```

## ⚙️ Advanced Configuration

### Performance Tuning

```bash
# JVM tuning for large codebases
export JAVA_OPTS="-Xmx4g -XX:+UseG1GC -XX:G1HeapRegionSize=16m"

java $JAVA_OPTS -jar target/waver-cli-0.1.0.jar \
  --input ./large-enterprise-app \
  --output ./comprehensive-docs \
  --type tutorial \
  --llm-provider OpenAI \
  --verbose
```

### Native Image Configuration

The project includes optimized GraalVM native-image configuration:

- **Reflection Config**: Pre-configured for LangChain4j and internal components
- **Proxy Config**: Dynamic proxy support for AI service interfaces  
- **Resource Config**: Bundled prompt templates and configuration files
- **Build Args**: Optimized initialization and security settings

```bash
# Custom native build with additional options
mvn clean package -Pnative \
  -Dquarkus.native.additional-build-args="-H:+ReportExceptionStackTraces,-H:+PrintClassInitialization"
```

## 🐛 Troubleshooting

### Common Issues

**🚨 "Environment variable not set" Error**
```bash
# Verify API key is set
echo $OPENAI_API_KEY
echo $GEMINI_AI_KEY

# Set if missing
export OPENAI_API_KEY="your-key-here"
```

**🚨 Out of Memory Issues**
```bash
# Increase heap size for large codebases
export JAVA_OPTS="-Xmx8g -XX:+UseG1GC"
java $JAVA_OPTS -jar target/waver-cli-0.1.0.jar [args...]
```

**🚨 Native Binary Issues**
```bash
# Check native binary permissions
chmod +x target/waver-cli

# Verify native dependencies
ldd target/waver-cli
```

**🚨 Debugging Pipeline Issues**
```bash
# Enable maximum verbosity
java -jar target/waver-cli-0.1.0.jar \
  --verbose \
  --input ./problematic-code \
  --output ./debug-output \
  --type tutorial \
  --llm-provider OpenAI 2>&1 | tee waver-debug.log
```

### Performance Benchmarks

| Codebase Size | Processing Time (JAR) | Processing Time (Native) | Memory Usage |
|---------------|----------------------|-------------------------|--------------|
| Small (~50 files) | ~2-3 minutes | ~45-60 seconds | ~512MB |
| Medium (~200 files) | ~8-12 minutes | ~3-5 minutes | ~1GB |
| Large (~1000 files) | ~30-45 minutes | ~12-18 minutes | ~2-4GB |

## 🚢 Production Deployment

For production environments, consider the **[waver-kubernetes](https://github.com/your-org/waver-kubernetes)** project which provides:

- **Horizontal Pod Autoscaling** for processing large repositories
- **Job Queues** with Redis for batch processing  
- **Persistent Volumes** for generated documentation storage
- **Monitoring & Metrics** with Prometheus and Grafana
- **Resource Limits** and quality-of-service guarantees

## 🛠️ Technical Stack

- **Runtime**: Java 21+ (Virtual Threads, Pattern Matching, Records)
- **AI Framework**: LangChain4j 1.0.0 (OpenAI GPT-4, Google Gemini)
- **CLI Framework**: Picocli 4.7.7 (ANSI colors, auto-completion)
- **Build System**: Maven 3.9+ (Shade plugin, Native profile)
- **Native Compilation**: GraalVM Native Image (SubstrateVM)
- **Pipeline Engine**: JGraphlet Task Pipeline (Concurrent execution)
- **Code Analysis**: Custom AST parsing and pattern recognition
- **Output Generation**: FlexMark (Markdown), Flying Saucer (PDF)

## 📊 API Rate Limits & Costs

### OpenAI GPT-4 Recommendations
```bash
# For cost optimization, use fewer input tokens
export WAVER_MAX_CONTEXT_SIZE=8192

# Monitor usage with verbose logging
java -jar waver-cli.jar --verbose [args...] 2>&1 | grep "tokens"
```

### Google Gemini Optimization
```bash
# Gemini offers better value for large codebases
export WAVER_BATCH_SIZE=5  # Process files in batches
```

## 🤝 Contributing

We welcome contributions from the technical community! 

```bash
# Development setup
git clone <repo-url>
cd waver
mvn clean compile
mvn exec:java -Dexec.mainClass="dev.shaaf.waver.cli.Main" -Dexec.args="--help"

# Run tests
mvn test

# Integration testing with testcontainers
mvn integration-test
```

## 📄 License

Licensed under the MIT License - see [LICENSE](LICENSE) for details.

---

**🔥 Ready to revolutionize your code documentation?**  
Deploy Waver today and let AI transform your codebase into comprehensive, intelligent tutorials that your team will actually read and understand.

```bash
# Get started in 60 seconds
git clone <repo> && cd waver && mvn clean package -Pnative
export OPENAI_API_KEY="your-key"
./target/waver-cli --input ./your-project --output ./docs --type tutorial --llm-provider OpenAI
```
