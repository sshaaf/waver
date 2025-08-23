package dev.shaaf.waver.cli;

import dev.langchain4j.model.chat.ChatModel;
import dev.shaaf.waver.config.AppConfig;
import dev.shaaf.waver.config.llm.ModelProviderFactory;
import dev.shaaf.jgraphlet.TaskPipeline;
import dev.shaaf.waver.tutorial.task.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class TutorialGenerator {

    private static final Logger logger = Logger.getLogger(TutorialGenerator.class.getName());


    public static void generate(AppConfig appConfig) {

        ChatModel chatModel = ModelProviderFactory.buildChatModel(appConfig.llmProvider(), appConfig.apiKey());
        Path outputDir = Paths.get(appConfig.absoluteOutputPath() + "/" + appConfig.projectName());

        logger.info("🚀 Starting Tutorial Generation for: " + appConfig.inputPath());
        try(TaskPipeline tasksPipeLine = new TaskPipeline()){
            tasksPipeLine.add("Code-crawler", new CodeCrawlerTask())
                    .then("Identify-abstraction", new IdentifyAbstractionsTask(chatModel, appConfig.projectName()))
                    .then("Identify-relationships",new IdentifyRelationshipsTask(chatModel, appConfig.projectName()))
                    .then("Chapter-organizer",new ChapterOrganizerTask(chatModel))
                    .then("Technical-writer",new TechnicalWriterTask(chatModel, outputDir))
                    .then("Meta-info",new MetaInfoTask(chatModel, outputDir, appConfig.projectName(), appConfig.inputPath()));
            tasksPipeLine.run(appConfig.inputPath()).join();
            logger.info("\n✅ Tutorial generation complete! Output located at: " + outputDir);
        }

    }
}
