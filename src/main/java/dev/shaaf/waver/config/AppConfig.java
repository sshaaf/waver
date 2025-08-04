package dev.shaaf.waver.config;

import dev.shaaf.waver.config.llm.LLMProvider;
import dev.shaaf.waver.util.FormatConverter.OutputFormat;

/**
 * Configuration record for the Waver application.
 * <p>
 * This record holds all the configuration parameters needed for the application to run,
 * including input and output paths, LLM provider details, and project information.
 * </p>
 *
 * @param inputPath  the absolute path to the source code files
 * @param absoluteOutputPath the absolute path to store the generated markdown files
 * @param llmProvider        the LLM provider to use (OpenAI, Gemini, etc.)
 * @param apiKey             the API key for the LLM provider
 * @param verbose            whether to enable verbose output for debugging
 * @param projectName        the name of the project
 * @param outputFormat       the format to use for the generated output (Markdown, HTML, PDF)
 */
public record AppConfig(
        String inputPath,
        String absoluteOutputPath,
        LLMProvider llmProvider,
        String apiKey,
        boolean verbose,
        String projectName,
        OutputFormat outputFormat
) {

    public static final String DEFAULT_GITHUB_CLONE_PATH = System.getProperty("user.dir")+"/waver-git-clone/";


}