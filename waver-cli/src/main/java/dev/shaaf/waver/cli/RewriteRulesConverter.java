package dev.shaaf.waver.cli;

import dev.langchain4j.model.chat.ChatModel;
import dev.shaaf.waver.config.AppConfig;
import dev.shaaf.waver.config.llm.ModelProviderFactory;
import dev.shaaf.waver.core.TaskPipeline;
import dev.shaaf.waver.kantra.task.GetYamlFilesFromDirTask;
import dev.shaaf.waver.kantra.task.RewriteConverterTask;
import dev.shaaf.waver.tutorial.task.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class RewriteRulesConverter {

    private static final Logger logger = Logger.getLogger(RewriteRulesConverter.class.getName());

    public static void generate(AppConfig appConfig) {

        ChatModel chatModel = ModelProviderFactory.buildChatModel(appConfig.llmProvider(), appConfig.apiKey());
        Path outputDir = Paths.get(appConfig.absoluteOutputPath() + "/" +appConfig.projectName());


        // TODO: Consider passing AppConfig into the constructor so shared config is simplified across tasks.
        TaskPipeline tasksPipeLine = new TaskPipeline();

        // Create a linear chain of tasks to execute
        tasksPipeLine.add(new GetYamlFilesFromDirTask())
                .then(new RewriteConverterTask(chatModel,outputDir));

        logger.info("🚀 Starting Rules Generation for: " + appConfig.inputPath());
        try {
            CompletableFuture<Object> generationContext = tasksPipeLine.run(appConfig.inputPath());
            logger.info("\n✅ Rules generation complete! Output located at: " + outputDir);
        } catch (Exception e) {
            logger.severe("❌ Rules generation failed: " + e.getMessage());
            throw new RuntimeException("Rules generation failed", e);
        }

    }
}
