package dev.shaaf.waver.cli;

import dev.shaaf.waver.config.FormatConverter;
import dev.shaaf.waver.config.GenerationType;
import dev.shaaf.waver.config.llm.LLMProvider;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.io.File;

/**
 * A data class to hold the raw command-line arguments parsed by Picocli.
 * This keeps argument parsing separate from the application's internal configuration.
 */
@Command(
        name = "waver-cli",
        mixinStandardHelpOptions = true,
        version = "0.0.1",
        description = "Generates tutorials and blogs from sourcecode using LLMs",
        headerHeading = "@|bold,fg(blue) Waver - Tutorial Generator|@%n%n",
        synopsisHeading = "@|bold,fg(yellow) Usage:|@ ",
        descriptionHeading = "%n@|bold,fg(yellow) Description:|@%n%n",
        parameterListHeading = "%n@|bold,fg(yellow) Parameters:|@%n",
        optionListHeading = "%n@|bold,fg(yellow) Options:|@%n",
        footerHeading = "%n@|bold,fg(yellow) Examples:|@%n",
        footer = """
                @|fg(green)  waver --input ./my-project --output ./tutorials --type tutorial --llm-provider OpenAI|@
                  Generate a tutorial for "My Project" using OpenAI
                  
                @|fg(green)  waver --input ./my-project --output ./tutorials --type tutorial --llm-provider Gemini --verbose|@
                  Generate a tutorial with verbose logging using Gemini
                """
)
public class CliArguments {

    @Option(names = "--input", required = true, description = "The path to the source code files to analyze.", paramLabel = "<path>")
    public String inputPath;

    @Option(names = "--output", required = true, description = "The directory where generated markdown files will be stored.", paramLabel = "<directory>")
    public File outputPath;

    @Option(names = {"-t", "--type"}, required = true, description = "The type of content to generate. Valid values: ${COMPLETION-CANDIDATES}.", paramLabel = "<Generation type>")
    public GenerationType generationType;

    @Option(names = "--llm-provider", required = true, description = "The Large Language Model provider to use. Valid values: ${COMPLETION-CANDIDATES}.", paramLabel = "<provider>")
    public LLMProvider llmProvider;

    @Option(names = {"-v", "--verbose"}, description = "Enable verbose output for debugging.")
    public boolean verbose = false;

    @Option(names = "--format", description = "Output format for the generated tutorial. Defaults to Markdown. Other values: HTML | PDF", paramLabel = "<format>")
    public FormatConverter.OutputFormat outputFormat = FormatConverter.OutputFormat.MARKDOWN;
}
