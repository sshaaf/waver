package dev.shaaf.waver.cli;


import dev.shaaf.waver.llm.config.AppConfig;
import picocli.CommandLine;

import java.util.logging.Logger;


/**
 * Contains the core application logic. It takes a validated AppConfig
 * and orchestrates the content generation process.
 */
public class Application {

    private static final Logger logger = Logger.getLogger(Application.class.getName());
    private final AppConfig appConfig;

    public Application(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public int run() {
        logger.info(getStartupMessage());

        try {
            switch (appConfig.generationType()) {
                case TUTORIAL:
                    TutorialGenerator.generate(appConfig);
                    break;
                case DOCUMENTATION:
                case BLOG:
                    throw new UnsupportedOperationException(appConfig.generationType() + " generation is not yet implemented.");
                default:
                    System.err.println("Unsupported generation type.");
                    return 1;
            }
        } catch (Exception e) {
            logger.severe(CommandLine.Help.Ansi.AUTO.string(
                    "@|fg(red),bold ERROR:|@ An error occurred during generation: " + e.getMessage()
            ));
            if (appConfig.verbose()) {
                e.printStackTrace();
            }
            return 1;
        }

        logger.info(CommandLine.Help.Ansi.AUTO.string("@|fg(green) ✅ Generation complete!|@"));
        return 0;
    }

    private String getStartupMessage() {
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
                appConfig.inputPath(),
                appConfig.absoluteOutputPath(),
                appConfig.llmProvider()
        );
        return CommandLine.Help.Ansi.AUTO.string(styledMessage);
    }
}
