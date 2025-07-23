package dev.shaaf.waver;

import dev.langchain4j.model.chat.ChatModel;
import dev.shaaf.waver.config.AppConfig;
import dev.shaaf.waver.config.MissingConfigurationException;
import dev.shaaf.waver.config.ProviderConfig;
import dev.shaaf.waver.llm.LLMProvider;
import dev.shaaf.waver.llm.ModelProviderFactory;
import dev.shaaf.waver.log.LogConfig;
import dev.shaaf.waver.model.*;
import dev.shaaf.waver.steps.*;
import dev.shaaf.waver.util.FormatConverter;
import dev.shaaf.waver.util.FormatConverter.OutputFormat;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Main application class for Waver.
 * <p>
 * Waver is a command-line tool that generates tutorials and blogs from source code using
 * Large Language Models (LLMs). It analyzes the source code, identifies abstractions and
 * relationships, and generates a structured tutorial with chapters.
 * </p>
 */
@Command(
        name = "waver",
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
                @|fg(green)  waver --input ./my-project --output ./tutorials --project-name "My Project" --llm-provider OpenAI|@
                  Generate a tutorial for "My Project" using OpenAI
                  
                @|fg(green)  waver --input ./my-project --output ./tutorials --project-name "My Project" --llm-provider Gemini --verbose|@
                  Generate a tutorial with verbose logging using Gemini
                """
)
public class Main implements Callable<Integer> {

    // Get a logger instance
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    @Option(
            names = "--input", 
            required = true, 
            description = "The path to the source code files to analyze. Can be a directory or a specific file.",
            paramLabel = "<path>")
    private File inputPath;

    @Option(
            names = "--output", 
            required = true, 
            description = "The directory where generated markdown files will be stored. Will be created if it doesn't exist.",
            paramLabel = "<directory>")
    private File outputPath;

    @Option(
            names = "--project-name", 
            required = true, 
            description = "The name of the project. Used in the generated tutorial titles and content.",
            paramLabel = "<name>")
    private String projectName;

    @Option(
            names = "--llm-provider", 
            description = "The Large Language Model provider to use for generating content. " +
                    "Requires the corresponding API key to be set as an environment variable. " +
                    "Valid values: ${COMPLETION-CANDIDATES}.",
            paramLabel = "<provider>")
    private LLMProvider llmProvider;

    @Option(
            names = {"-v", "--verbose"}, 
            description = "Enable verbose output for debugging. Shows detailed logs and stack traces on errors.",
            paramLabel = "")
    private boolean verbose = false; // Defaults to false
    
    
    @Option(
            names = "--format",
            description = "Output format for the generated tutorial defaults to Markdown. Other values: HTML | PDF",
            paramLabel = "<format>")
    private OutputFormat outputFormat = OutputFormat.MARKDOWN;

    /**
     * Executes the main command logic when the application is run.
     * <p>
     * This method validates the configuration, sets up logging, and initiates
     * the tutorial generation process.
     * </p>
     *
     * @return 0 if the execution was successful, non-zero otherwise
     * @throws Exception if an error occurs during execution
     */
    @Override
    public Integer call() throws Exception {
        LogConfig.setLoggingConfig(logger);
        
        
        ProviderConfig providerConfig = null;
        AppConfig appConfig = null;

        // 1. Validate environment variables based on the chosen provider
        logger.info("Checking required environment variables...");

        try {
            // Check if required options are set
            if (inputPath == null) {
                throw new IllegalArgumentException(
                    CommandLine.Help.Ansi.AUTO.string(
                        "@|fg(red),bold ERROR:|@ Input path is required.\n" +
                        "@|fg(yellow) Suggestion:|@ Specify the input path with --input <path>"
                    )
                );
            }
            
            if (outputPath == null) {
                throw new IllegalArgumentException(
                    CommandLine.Help.Ansi.AUTO.string(
                        "@|fg(red),bold ERROR:|@ Output path is required.\n" +
                        "@|fg(yellow) Suggestion:|@ Specify the output path with --output <directory> or set default_output_dir in your config file."
                    )
                );
            }
            
            if (projectName == null) {
                throw new IllegalArgumentException(
                    CommandLine.Help.Ansi.AUTO.string(
                        "@|fg(red),bold ERROR:|@ Project name is required.\n" +
                        "@|fg(yellow) Suggestion:|@ Specify the project name with --project-name <name>"
                    )
                );
            }
            
            if (llmProvider == null) {
                throw new IllegalArgumentException(
                    CommandLine.Help.Ansi.AUTO.string(
                        "@|fg(red),bold ERROR:|@ LLM provider is required.\n" +
                        "@|fg(yellow) Suggestion:|@ Specify the LLM provider with --llm-provider <provider> or set llm_provider in your config file.\n" +
                        "  Valid values are: " + Arrays.toString(LLMProvider.values())
                    )
                );
            }

            // Validate input path exists
            if (!inputPath.exists()) {
                throw new IllegalArgumentException(
                    CommandLine.Help.Ansi.AUTO.string(
                        "@|fg(red),bold ERROR:|@ Input path does not exist: " + inputPath.getAbsolutePath() + "\n" +
                        "@|fg(yellow) Suggestion:|@ Check that the path is correct and try again.\n" +
                        "  Example: --input ./my-project"
                    )
                );
            }

            // Validate output path
            if (outputPath.exists() && !outputPath.isDirectory()) {
                throw new IllegalArgumentException(
                    CommandLine.Help.Ansi.AUTO.string(
                        "@|fg(red),bold ERROR:|@ Output path exists but is not a directory: " + outputPath.getAbsolutePath() + "\n" +
                        "@|fg(yellow) Suggestion:|@ Specify a directory path for the output.\n" +
                        "  Example: --output ./tutorials"
                    )
                );
            }
            
            // Create output directory if it doesn't exist
            if (!outputPath.exists()) {
                logger.info("Creating output directory: " + outputPath.getAbsolutePath());
                if (!outputPath.mkdirs()) {
                    throw new IllegalArgumentException(
                        CommandLine.Help.Ansi.AUTO.string(
                            "@|fg(red),bold ERROR:|@ Failed to create output directory: " + outputPath.getAbsolutePath() + "\n" +
                            "@|fg(yellow) Suggestion:|@ Check that you have write permissions to the parent directory."
                        )
                    );
                }
            }

            // Get provider configuration
            try {
                providerConfig = getProviderConfig(llmProvider);
            } catch (MissingConfigurationException e) {
                String envVarName = llmProvider == LLMProvider.OpenAI ? "OPENAI_API_KEY" : 
                                   (llmProvider == LLMProvider.Gemini ? "GEMINI_AI_KEY" : "unknown");
                
                throw new MissingConfigurationException(
                    CommandLine.Help.Ansi.AUTO.string(
                        "@|fg(red),bold ERROR:|@ " + e.getMessage() + "\n" +
                        "@|fg(yellow) Suggestion:|@ Set the required environment variable: " + envVarName + "\n" +
                        "  For example, in bash/zsh: export " + envVarName + "=your_api_key_here\n" +
                        "  In Windows Command Prompt: set " + envVarName + "=your_api_key_here\n" +
                        "  In PowerShell: $env:" + envVarName + " = \"your_api_key_here\""
                    )
                );
            }

            appConfig = new AppConfig(inputPath.getAbsolutePath(), outputPath.getAbsolutePath(), llmProvider, providerConfig.getApiKey(), verbose, projectName, outputFormat);
        } catch (Exception e) {
            if (verbose) {
                e.printStackTrace();
            } else {
                logger.severe(e.getMessage());
            }
            return 1; // Return error code instead of System.exit
        }
        
        // 2. All conditions are met, print the configuration and call the main logic
        logger.info(picoColors(appConfig));

        // 3. Call your main tutorial generator class with the parameters
        try {
            generate(appConfig);
        } catch (Exception e) {
            logger.severe(CommandLine.Help.Ansi.AUTO.string(
                "@|fg(red),bold ERROR:|@ An error occurred during tutorial generation: " + e.getMessage()
            ));
            if (verbose) {
                e.printStackTrace();
            }
            return 1;
        }

        return 0; // Indicate success
    }

    /**
     * Formats the application configuration as a colorized string for console output.
     * <p>
     * This method uses picocli's Ansi formatting to create a colorful display of
     * the current application configuration.
     * </p>
     *
     * @param appConfig the application configuration to format
     * @return a colorized string representation of the configuration
     */
    private static String picoColors(AppConfig appConfig) {
        String styledMessage = """
                
                @|fg(green) ✅ All checks passed. 
                🌟Starting generator with the following configuration:|@
                  - @|bold Project Name         :|@ @|yellow %s|@
                  - @|bold Input Path           :|@ @|yellow %s|@
                  - @|bold Output Path          :|@ @|yellow %s|@
                  - @|bold LLM Provider         :|@ @|yellow %s|@
                  - @|bold LLM Credentials      :|@ @|yellow Found|@
                --------------------------------------------------""".formatted(
                appConfig.projectName(),
                appConfig.absoluteInputPath(),
                appConfig.absoluteOutputPath(),
                appConfig.llmProvider()
        );
        return CommandLine.Help.Ansi.AUTO.string(styledMessage);
    }

    /**
     * Creates a provider configuration based on the specified LLM provider.
     * <p>
     * This method retrieves the appropriate API key from environment variables
     * based on the selected LLM provider.
     * </p>
     *
     * @param llmProvider the LLM provider to configure
     * @return a provider-specific configuration object
     * @throws MissingConfigurationException if the provider is unsupported or the required API key is missing
     */
    private static ProviderConfig getProviderConfig(LLMProvider llmProvider) {
        return switch (llmProvider) {
            case OpenAI -> new ProviderConfig.OpenAI(getConfigParamFromEnv("OPENAI_API_KEY"));
            case Gemini -> new ProviderConfig.Gemini(getConfigParamFromEnv("GEMINI_AI_KEY"));
            case null, default -> {
                throw new MissingConfigurationException(
                        "Unsupported or missing LLM provider. Valid options are: " + Arrays.toString(LLMProvider.values())
                );
            }
        };
    }

    /**
     * Retrieves a configuration parameter from environment variables.
     * <p>
     * This method checks if the specified environment variable is set and not empty.
     * </p>
     *
     * @param requiredApiKeyEnv the name of the environment variable to retrieve
     * @return the value of the environment variable
     * @throws MissingConfigurationException if the environment variable is not set or empty
     */
    private static String getConfigParamFromEnv(String requiredApiKeyEnv) {
        if (requiredApiKeyEnv != null) {
            String apiKeyValue = System.getenv(requiredApiKeyEnv);
            if (apiKeyValue == null || apiKeyValue.isBlank()) {
                throw new MissingConfigurationException("Environment variable " + requiredApiKeyEnv + " is not set.");
            }
            return apiKeyValue;
        }
        throw new MissingConfigurationException("Found null while looking for environment variable, try setting the variable: " + requiredApiKeyEnv + " and try again.");
    }


    /**
     * Generates a tutorial from source code using the specified configuration.
     * <p>
     * This method orchestrates the entire tutorial generation process, including:
     * <ol>
     *   <li>Fetching repository files</li>
     *   <li>Identifying abstractions in the code</li>
     *   <li>Analyzing relationships between abstractions</li>
     *   <li>Ordering chapters for the tutorial</li>
     *   <li>Writing chapters</li>
     *   <li>Combining everything into a final tutorial</li>
     * </ol>
     * </p>
     *
     * @param appConfig the application configuration containing input/output paths, LLM provider, etc.
     */
    public static void generate(AppConfig appConfig) {

        ChatModel model = ModelProviderFactory.buildChatModel(appConfig.llmProvider(), appConfig.apiKey());

        logger.info("🚀 Starting Tutorial Generation for: " + appConfig.absoluteInputPath());

        // This map will hold the state and pass data between steps
        Map<String, Object> shared = new HashMap<>();

        try {
            // Step A: Fetch Repo
            logger.info("\n[1/6] 📂 Fetching repository files...");
            
            List<CodeFile> codeFiles = FetchRepo.crawl(appConfig.absoluteInputPath());
            shared.put("codeFiles", codeFiles);
            logger.info("   > Found " + codeFiles.size() + " code files.");

            String codebaseAsString = codeFiles.stream()
                    .map(file -> "--- File: " + file.path() + " ---\n" + file.content())
                    .collect(Collectors.joining("\n\n"));

            // Step B: Identify Abstractions
            logger.info("[2/6] 🧠 Identifying abstractions...");
            
            AbstractionList abstractionList = IdentifyAbstractions.find(codebaseAsString, model, appConfig.projectName());
            
            shared.put("abstractions", abstractionList);
            logger.info("   > Identified " + abstractionList.abstractions().size() + " abstractions.");

            String abstractionsAsString = abstractionList.abstractions().stream()
                    .map(Abstraction::name)
                    .collect(Collectors.joining(", "));

            // Step C: Analyze Relationships
            logger.info("[3/6] 🔗 Analyzing relationships...");
            
            RelationshipAnalysis relationships = IdentifyRelationships.find(codebaseAsString, abstractionsAsString, model, appConfig.projectName());
            
            shared.put("relationships", relationships);
            logger.info("   > " + relationships.summary());

            // Step D: Order Chapters
            logger.info("[4/6] 📑 Ordering chapters...");
            
            ChapterList chapterOrder = ChapterOrganizer.find(abstractionsAsString, model);
            
            shared.put("chapter_order", chapterOrder);
            logger.info("   > Chapter order determined: " + chapterOrder.toString());

            // Step E: Write Chapters
            logger.info("[5/6] ✍️ Writing chapters...");
            
            List<String> chapters = TechnicalWriter.build(codeFiles, chapterOrder.chapterList(), abstractionList, model);
            
            shared.put("chapters", chapters);
            logger.info("   > All " + chapters.size() + " chapters written.");

            // Step F: Combine Tutorial
            logger.info("[6/6] 📦 Combining tutorial into final output...");
            
            Path outputDir = Paths.get(appConfig.absoluteOutputPath() + "/" + appConfig.llmProvider().name().toLowerCase() + "/" + appConfig.projectName() + "/" + System.currentTimeMillis());
            String introChapter = IntroBuilder.write(codebaseAsString, abstractionList, model);
            CombineTutorial.build(outputDir, chapters, chapterOrder.chapterList(), relationships, introChapter);
            
            shared.put("final_output_dir", outputDir);

            logger.info("\n✅ Tutorial generation complete! Output located at: " + outputDir);

            // Convert output to the selected format if not Markdown
            if (appConfig.outputFormat() != OutputFormat.MARKDOWN) {
                logger.info("\n🔄 Converting output to " + appConfig.outputFormat() + " format...");
                
                try {
                    // Convert all markdown files in the output directory
                    int count = FormatConverter.convertDirectory(outputDir.toFile(), appConfig.outputFormat());
                    
                    logger.info("\n✅ Conversion complete! " + count + " files converted to " + appConfig.outputFormat() + " format.");
                } catch (IOException e) {
                    logger.severe("\n❌ An error occurred during format conversion: " + e.getMessage());
                    if (appConfig.verbose()) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            logger.severe("\n❌ An error occurred during tutorial generation:");
            if (appConfig.verbose())
                e.printStackTrace();
        }
    }


    /**
     * The main entry point of the application.
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}