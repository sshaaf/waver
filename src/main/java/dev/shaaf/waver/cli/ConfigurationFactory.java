package dev.shaaf.waver.cli;

import dev.shaaf.waver.llm.config.AppConfig;
import dev.shaaf.waver.llm.config.LLMProvider;
import dev.shaaf.waver.llm.config.MissingConfigurationException;
import dev.shaaf.waver.llm.config.ProviderConfig;
import picocli.CommandLine;

import java.util.Arrays;

/**
 * Responsible for creating a valid AppConfig from the parsed command-line arguments.
 * It handles validation, environment variable checks, and default value assignments.
 */
public class ConfigurationFactory {

    public static AppConfig from(CliArguments args) {
        validate(args);
        ProviderConfig providerConfig = getProviderConfig(args.llmProvider);
        return new AppConfig(
                args.inputPath,
                args.outputPath.getAbsolutePath(),
                args.llmProvider,
                providerConfig.getApiKey(),
                args.verbose,
                "My Project",
                args.outputFormat,
                args.generationType
        );
    }

    private static void validate(CliArguments args) {
        if (args.outputPath.exists() && !args.outputPath.isDirectory()) {
            throw new IllegalArgumentException(
                    CommandLine.Help.Ansi.AUTO.string(
                            "@|fg(red),bold ERROR:|@ Output path exists but is not a directory: " + args.outputPath.getAbsolutePath()
                    )
            );
        }

        if (!args.outputPath.exists()) {
            if (!args.outputPath.mkdirs()) {
                throw new IllegalArgumentException(
                        CommandLine.Help.Ansi.AUTO.string(
                                "@|fg(red),bold ERROR:|@ Failed to create output directory: " + args.outputPath.getAbsolutePath()
                        )
                );
            }
        }
    }

    private static ProviderConfig getProviderConfig(LLMProvider llmProvider) {
        return switch (llmProvider) {
            case OpenAI -> new ProviderConfig.OpenAI(getApiKeyFromEnv("OPENAI_API_KEY"));
            case Gemini -> new ProviderConfig.Gemini(getApiKeyFromEnv("GEMINI_AI_KEY"));
            default -> throw new MissingConfigurationException(
                    "Unsupported or missing LLM provider. Valid options are: " + Arrays.toString(LLMProvider.values())
            );
        };
    }

    private static String getApiKeyFromEnv(String envVar) {
        String apiKey = System.getenv(envVar);
        if (apiKey == null || apiKey.isBlank()) {
            throw new MissingConfigurationException("Environment variable " + envVar + " is not set.");
        }
        return apiKey;
    }
}
