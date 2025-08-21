package dev.shaaf.waver.cli;

import dev.langchain4j.model.chat.ChatModel;
import dev.shaaf.waver.config.AppConfig;
import dev.shaaf.waver.config.llm.ModelProviderFactory;
import dev.shaaf.waver.core.TaskPipeline;
import dev.shaaf.waver.tutorial.task.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class TutorialGenerator {

    private static final Logger logger = Logger.getLogger(TutorialGenerator.class.getName());


    public static void generate(AppConfig appConfig) {

        ChatModel chatModel = ModelProviderFactory.buildChatModel(appConfig.llmProvider(), appConfig.apiKey());
        Path outputDir = Paths.get(appConfig.absoluteOutputPath() + "/" + appConfig.projectName());


        // TODO: Consider passing AppConfig into the constructor so shared config is simplified across tasks.
        TaskPipeline tasksPipeLine = new TaskPipeline();

        // Create a linear chain of tasks to execute
        tasksPipeLine.add(new CodeCrawlerTask())
                .then(new IdentifyAbstractionsTask(chatModel, appConfig.projectName()))
                .then(new IdentifyRelationshipsTask(chatModel, appConfig.projectName()))
                .then(new ChapterOrganizerTask(chatModel))
                .then(new TechnicalWriterTask(chatModel, outputDir))
                .then(new MetaInfoTask(chatModel, outputDir, appConfig.projectName(), appConfig.inputPath()));

        logger.info("🚀 Starting Tutorial Generation for: " + appConfig.inputPath());
        try {
            CompletableFuture<Object> generationFuture = tasksPipeLine.run(appConfig.inputPath());

            CompletableFuture<Void> finalResult = generationFuture.thenAccept(result -> {
                logger.info("\n✅ Tutorial generation complete! Output located at: " + outputDir);
            }).exceptionally(ex -> {
                logger.severe("❌ Tutorial generation failed: " + ex.getMessage());
                throw new RuntimeException("Tutorial generation failed", ex);
            });

            finalResult.join();

        } catch (Exception e) {
            // The exception from the .exceptionally() block will be caught here
            // No need to log again, just re-throw or exit.
            System.err.println("A critical error occurred. Exiting.");
            // Optionally re-throw if this is part of a larger method
            // throw e;
        }

    }
}
