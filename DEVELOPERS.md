# Waver Developer Guide

This guide provides detailed technical information for developers working on the Waver project.

## Building from Source

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/waver.git
   cd waver
   ```

2. Build the project:
   ```
   ./gradlew build
   ```

## GitHub Actions Builds

Waver uses GitHub Actions to automatically build the project and generate native executables for all platforms (Linux, macOS with Apple Silicon, and Windows) on every commit.

### Accessing Built Artifacts

1. Go to the [Actions tab](https://github.com/yourusername/waver/actions) in the GitHub repository.
2. Click on the latest workflow run.
3. Scroll down to the "Artifacts" section to download:
   - `waver-jar`: The Java JAR file
   - `waver-linux`: The Linux native executable
   - `waver-macos`: The macOS native executable (compatible with Apple Silicon)
   - `waver-windows`: The Windows native executable

### Releases

Official releases are automatically created when changes are pushed to the main branch. You can find the latest release with all platform executables on the [Releases page](https://github.com/yourusername/waver/releases).

## Native Compilation

Waver supports GraalVM native compilation to create a standalone executable that doesn't require a JVM to run.

### Prerequisites for Native Compilation

- GraalVM JDK 21 or higher
- Gradle 8.0 or higher

### Commands for Native Compilation

1. Compile the application into a native executable:
   ```bash
   # The --no-configuration-cache flag is required to avoid issues with Gradle's configuration cache
   ./gradlew nativeCompile --no-configuration-cache
   ```

2. Verify the native executable was created successfully:
   ```bash
   # Check that the executable exists
   ls -la ./build/native/nativeCompile/waver
   
   # Verify it works by displaying the help information
   ./build/native/nativeCompile/waver --help
   ```

The native executable will be generated at `./build/native/nativeCompile/waver`.

### Running the Native Executable

The native executable can be run directly without needing a JVM:

```bash
./build/native/nativeCompile/waver --input ./my-project --output ./tutorials --project-name "My Project" --llm-provider OpenAI
```

Before running the native executable, make sure to set the appropriate environment variable for your chosen LLM provider:

- For OpenAI:
  ```bash
  export OPENAI_API_KEY=your_api_key
  ```

- For Gemini:
  ```bash
  export GEMINI_AI_KEY=your_api_key
  ```

### Native Image Compilation Notes

- The native compilation process creates a standalone executable that doesn't require a JVM to run
- The compilation may take several minutes to complete
- The resulting executable starts up much faster than the JVM version
- Memory usage is typically lower than running with a JVM
- The project includes GraalVM configuration files in `src/main/resources/META-INF/native-image/`:
  - `reflect-config.json`: Lists classes that need reflection support
  - `resource-config.json`: Specifies resources to include in the native image
  - `proxy-config.json`: Configures dynamic proxies for interfaces
  - `native-image.properties`: Provides default native-image build arguments
- If you encounter class initialization errors during native compilation, you may need to adjust the `--initialize-at-build-time` and `--initialize-at-run-time` flags in `build.gradle.kts`
- The build process automatically copies the configuration files to the build directory using the `copyGraalVMConfig` task

### Troubleshooting Native Compilation

If you encounter issues during native compilation:

1. Make sure you're using the `--no-configuration-cache` flag:
   ```bash
   ./gradlew nativeCompile --no-configuration-cache
   ```

2. If you see reflection-related errors, check that the GraalVM configuration files are properly set up in `src/main/resources/META-INF/native-image/`.

3. For class initialization errors, you may need to modify the initialization strategy in `build.gradle.kts`:
   - Add classes that should be initialized at build time to the `--initialize-at-build-time` list
   - Add classes that should be initialized at runtime to the `--initialize-at-run-time` list

## Shell Auto-Completion

Waver supports shell auto-completion for Bash, Zsh, and other compatible shells. To generate the completion script:

```
./gradlew generateCompletion
```

This will create a file called `waver-completion.sh` in the project root directory.

### Bash

To enable auto-completion in Bash:

```bash
# Add to your ~/.bashrc file
source /path/to/waver-completion.sh
```

### Zsh

To enable auto-completion in Zsh:

```zsh
# Add to your ~/.zshrc file
autoload -U +X compinit && compinit
source /path/to/waver-completion.sh
```

## Testing

Waver includes a comprehensive test suite to ensure code quality and prevent regressions. The tests are written using JUnit 5 and Mockito.

### Running Tests

To run the tests locally:

```bash
./gradlew test
```

This will execute all tests and generate a report in `build/reports/tests/test/index.html`.

### Test Coverage

The test suite covers key components of the application:

- **Utility Classes**: Tests for the `FormatConverter` class ensure proper conversion between Markdown, HTML, and PDF formats.
- **LLM Provider**: Tests for the `ModelType` enum and `ModelProviderFactory` verify correct handling of different LLM providers.
- **Configuration**: Tests for configuration classes ensure proper error handling for missing configurations.

### Continuous Integration

The GitHub Actions workflow automatically runs all tests for:
- Every push to the main branch
- Every pull request targeting the main branch

This ensures that all code changes pass tests before being merged, maintaining code quality and preventing regressions.

## Project Architecture

Waver is organized into several key packages:

- `dev.shaaf.waver`: Main package containing the application entry point
- `dev.shaaf.waver.core`: core framework such as Task, Pipeline
- `dev.shaaf.waver.config`: Configuration classes for the application
- `dev.shaaf.waver.config.llm`: Classes for interacting with LLM providers
- `dev.shaaf.waver.util.log`: Logging utilities
- `dev.shaaf.waver.tutorial.model`: Data models for abstractions, relationships, and chapters
- `dev.shaaf.waver.tutorial.prompt`: Classes for generating prompts for the LLM
- `dev.shaaf.waver.tutorial.task`: Pipeline tasks for generating tutorials
- `dev.shaaf.waver.util`: Utility classes

The application follows a pipeline architecture where each step in the tutorial generation process is handled by a separate class in the `task` package.

## License

This project is distributed under the MIT License. See the `LICENSE` file for more information.