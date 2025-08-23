package dev.shaaf.waver.backend.process;

import dev.langchain4j.model.chat.ChatModel;
import dev.shaaf.jgraphlet.TaskPipeline;
import dev.shaaf.waver.backend.FileUtil;
import dev.shaaf.waver.backend.config.MinioConfig;
import dev.shaaf.waver.backend.config.WaverConfig;
import dev.shaaf.waver.backend.minio.MinioUploaderTask;
import dev.shaaf.waver.backend.WaverProcessEvent;
import dev.shaaf.waver.config.AppConfig;
import dev.shaaf.waver.config.GenerationType;
import dev.shaaf.waver.config.ProviderConfig;
import dev.shaaf.waver.config.llm.LLMProvider;
import dev.shaaf.waver.config.llm.MissingConfigurationException;
import dev.shaaf.waver.config.llm.ModelProviderFactory;

import dev.shaaf.waver.tutorial.task.*;
import io.minio.MinioClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

@ApplicationScoped
public class BackendProcessingService {

    @Inject
    WaverConfig waverConfig;

    @Inject
    MinioConfig minioConfig;

    @Inject
    MinioClient minioClient;

    private static final Logger logger = Logger.getLogger(BackendProcessingService.class.getName());

    @Incoming("requests")
    public CompletionStage<Void> initAndRunPipeline(WaverProcessEvent event) {
        logger.info("🚀 Event is invoked, starting generation: " + event.sourceUrl());
        return CompletableFuture.runAsync(() -> {
            generate(event.sourceUrl());
            logger.info("🚀 Generation has ended. Good bye! " + event.sourceUrl());
        });
    }

    public void generate(String inputPath) {
        ProviderConfig providerConfig = getProviderConfig();
        if (providerConfig.getApiKey() == null) {
            throw new MissingConfigurationException("LLM API key is missing.");
        }

        generate(
                new AppConfig(inputPath,
                        getAbsolutePath(waverConfig.outputPath()),
                        waverConfig.llmProvider(),
                        providerConfig.getApiKey(),
                        waverConfig.verbose(),
                        FileUtil.getFolderNameFromInputPath(inputPath),
                        waverConfig.outputFormat(),
                        GenerationType.TUTORIAL)
        );
    }

    public String getAbsolutePath(String path) {
        return Paths.get(path).toAbsolutePath().toString();
    }


    public void generate(AppConfig appConfig) {
        logger.info(appConfig.toString());

        ChatModel chatModel = ModelProviderFactory.buildChatModel(appConfig.llmProvider(), appConfig.apiKey());
        Path outputDir = Paths.get(appConfig.absoluteOutputPath() + "/" +appConfig.projectName());

        logger.info("🚀 Starting Tutorial Generation for: " + appConfig.inputPath());
        try(TaskPipeline tasksPipeLine = new TaskPipeline()){
            tasksPipeLine.add("Code-crawler", new CodeCrawlerTask())
                    .then("Identify-abstraction", new IdentifyAbstractionsTask(chatModel, appConfig.projectName()))
                    .then("Identify-relationships",new IdentifyRelationshipsTask(chatModel, appConfig.projectName()))
                    .then("Chapter-organizer",new ChapterOrganizerTask(chatModel))
                    .then("Technical-writer",new TechnicalWriterTask(chatModel, outputDir))
                    .then("Meta-info",new MetaInfoTask(chatModel, outputDir, appConfig.projectName(), appConfig.inputPath()))
                    .then("Minio-upload", new MinioUploaderTask(minioClient, outputDir, minioConfig.bucketName()));
            tasksPipeLine.run(appConfig.inputPath()).join();
            logger.info("\n✅ Tutorial generation complete! Output located at: " + outputDir);
        }
    }

    public ProviderConfig getProviderConfig() {
        LLMProvider llmProvider = waverConfig.llmProvider();

        return switch (llmProvider) {
            case OpenAI -> new ProviderConfig.OpenAI(
                    waverConfig.openai().apiKey().orElseThrow(() ->
                            new MissingConfigurationException("Property 'waver.openai.api-key' was not set for OpenAI provider."))
            );
            case Gemini -> new ProviderConfig.Gemini(
                    waverConfig.gemini().apiKey().orElseThrow(() ->
                            new MissingConfigurationException("Property 'waver.gemini.api-key' was not set for Gemini provider."))
            );
            case null, default -> throw new MissingConfigurationException(
                    "Unsupported or missing LLM provider configured in 'waver.llmprovider'. Valid options are: " + Arrays.toString(LLMProvider.values())
            );
        };
    }

}
