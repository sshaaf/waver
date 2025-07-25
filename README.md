<!-- For a centered logo -->
<p align="center">
  <img src=".github/assets/waver-128.png" alt="Project Logo" width="128">
</p>
<h2 align="center">
  <b> Waver - An easy to use code tutorial generator </b>
</h2>

Waver is a command-line tool that generates code tutorials from source code using Large Language Models (LLMs). It analyzes the source code, identifies abstractions and relationships, and generates a structured tutorial with chapters.

## Features

- Analyzes source code to identify abstractions and relationships
- Generates structured tutorials with chapters
- Supports multiple LLM providers (OpenAI, Gemini)
- Colorized output for better readability
- Progress indicators for long-running operations
- Multiple output formats (Markdown, HTML, PDF)
- Shell auto-completion

## Installation

### Prerequisites

- Java 21 or higher
- Gradle 8.0 or higher
- An API key for OpenAI or Gemini
- Git (required for --github-url feature)

### Building from Source

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/waver.git
   cd waver
   ```

2. Build the project:
   ```
   ./gradlew build
   ```

For more detailed build instructions, including native compilation, see [Developers.md](DEVELOPERS.md).

## Usage

### Basic Usage

#### From Local Directory
```
waver --input ./my-project --output ./tutorials --project-name "My Project" --llm-provider OpenAI
```

#### From GitHub Repository
```
waver --github-url https://github.com/user/repo --output ./tutorials --project-name "My Project" --llm-provider OpenAI
```

### Command-Line Options

- `--input <path>`: The path to the source code files to analyze (required unless --github-url is provided)
- `--github-url <url>`: GitHub repository URL to analyze. The repository will be cloned automatically (required unless --input is provided)
- `--output <directory>`: The directory where generated markdown files will be stored (required)
- `--project-name <n>`: The name of the project (required)
- `--llm-provider <provider>`: The LLM provider to use (OpenAI, Gemini)
- `--format <format>`: Output format for the generated tutorial (MARKDOWN, HTML, PDF)
- `-v, --verbose`: Enable verbose output for debugging
- `-h, --help`: Show help message and exit
- `-V, --version`: Print version information and exit

### Environment Variables

- `OPENAI_API_KEY`: API key for OpenAI (required if using OpenAI provider)
- `GEMINI_AI_KEY`: API key for Gemini (required if using Gemini provider)

## Examples

### Generate a Tutorial from Local Code

```
waver --input ./my-project --output ./tutorials --project-name "My Project" --llm-provider OpenAI
```

### Generate a Tutorial from GitHub Repository

```
waver --github-url https://github.com/spring-projects/spring-petclinic --output ./tutorials --project-name "Spring PetClinic" --llm-provider OpenAI
```

### Generate a Tutorial with HTML Output

```
waver --input ./my-project --output ./tutorials --project-name "My Project" --llm-provider OpenAI --format HTML
```

### Generate from Private Repository

For private repositories, ensure you have Git configured with appropriate credentials:

```
waver --github-url https://github.com/myorg/private-repo --output ./tutorials --project-name "Private Project" --llm-provider Gemini
```

## Documentation

- [Developers Guide](DEVELOPERS.md): Detailed information for developers working on Waver
- [Contributors Guide](CONTRIBUTORS.md): Guidelines for contributing to the project

## License

This project is distributed under the MIT License. See the `LICENSE` file for more information.