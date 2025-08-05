package dev.shaaf.waver.backend.process;

import dev.langchain4j.model.chat.ChatModel;
import dev.shaaf.waver.backend.config.WaverConfig;
import dev.shaaf.waver.backend.minio.MinioUploaderTask;
import dev.shaaf.waver.backend.WaverProcessEvent;
import dev.shaaf.waver.backend.minio.UploadResult;
import dev.shaaf.waver.config.AppConfig;
import dev.shaaf.waver.config.ProviderConfig;
import dev.shaaf.waver.config.llm.LLMProvider;
import dev.shaaf.waver.config.llm.MissingConfigurationException;
import dev.shaaf.waver.config.llm.ModelProviderFactory;
import dev.shaaf.waver.core.TaskPipeline;
import dev.shaaf.waver.tutorial.task.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jetbrains.annotations.Blocking;

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

    private static final Logger logger = Logger.getLogger(BackendProcessingService.class.getName());

    @Incoming("requests")
    public CompletionStage<Void> initAndRunPipeline(WaverProcessEvent event) {
        logger.info("🚀 Event is invoked, starting my business: " + event.sourceUrl());
        return CompletableFuture.runAsync(() -> {
            generate(event.sourceUrl());
            logger.info("🚀 My business has ended. Good bye! " + event.sourceUrl());
        });
    }

    public void generate(String inputPath) {
        ProviderConfig providerConfig = getProviderConfig();
        if (providerConfig.getApiKey() == null) {
            throw new MissingConfigurationException("LLM API key is missing.");
        }

        generate(
                new AppConfig(
                        inputPath,
                        getAbsolutePath(waverConfig.outputPath()),
                        waverConfig.llmProvider(),
                        providerConfig.getApiKey(),
                        waverConfig.verbose(),
                        waverConfig.projectName(),
                        waverConfig.outputFormat()));
    }

    public String getAbsolutePath(String path) {
        return Paths.get(path).toAbsolutePath().toString();
    }


    public static void generate(AppConfig appConfig) {

        ChatModel chatModel = ModelProviderFactory.buildChatModel(appConfig.llmProvider(), appConfig.apiKey());
        Path outputDir = Paths.get(appConfig.absoluteOutputPath() + "/" +appConfig.projectName());

        // TODO: Consider passing AppConfig into the constructor so shared config is simplified across tasks.
        TaskPipeline tasksPipeLine = new TaskPipeline();
        tasksPipeLine.add(new CodeCrawlerTask())
                .then(new IdentifyAbstractionsTask(chatModel, appConfig.projectName()))
                .then(new IdentifyRelationshipsTask(chatModel, appConfig.projectName()))
                .then(new ChapterOrganizerTask(chatModel))
                .then(new TechnicalWriterTask(chatModel, outputDir))
                .then(new MetaInfoTask(chatModel, outputDir, appConfig.projectName(), appConfig.inputPath()))
                .then(new MinioUploaderTask(outputDir));

        logger.info("🚀 Starting Tutorial Generation for: " + appConfig.inputPath());
        UploadResult finalOutput = tasksPipeLine.run(appConfig.inputPath());
        logger.info("\n✅ Tutorial generation complete! Output located at: " + outputDir);
        logger.info("\n✅ %d files uploaded, %d failed to upload. See logs for details.".formatted(finalOutput.getSuccessCount(), finalOutput.getFailureCount()));

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
