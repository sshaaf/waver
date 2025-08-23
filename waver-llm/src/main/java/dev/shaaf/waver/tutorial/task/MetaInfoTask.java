package dev.shaaf.waver.tutorial.task;

import java.util.concurrent.CompletableFuture;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.shaaf.jgraphlet.PipelineContext;
import dev.shaaf.jgraphlet.Task;
import dev.shaaf.jgraphlet.TaskRunException;
import dev.shaaf.waver.tutorial.model.GenerationContext;
import dev.shaaf.waver.tutorial.model.TutorialMetadata;
import dev.shaaf.waver.tutorial.prompt.MetaInfoAnalyzer;
import dev.shaaf.waver.files.GitHubRepoFetcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MetaInfoTask implements Task<GenerationContext, GenerationContext> {

    private ChatModel chatModel;
    private Path outputDir;
    private final String projectName;
    private final String inputString;

    public MetaInfoTask(ChatModel chatModel, Path outputDir, String projectName, String inputString){
        this.projectName = projectName;
        this.chatModel = chatModel;
        this.outputDir = outputDir;
        this.inputString = inputString;
    }

    @Override
    public CompletableFuture<GenerationContext> execute(GenerationContext generationContext, PipelineContext context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MetaInfoAnalyzer infoAnalyzer = AiServices.create(MetaInfoAnalyzer.class, chatModel);
                TutorialMetadata metadata = infoAnalyzer.generateMetadata(generationContext.codeAsString(), generationContext.abstractionsAsString(), projectName);

                metadata = metadata.withRepoOwner((GitHubRepoFetcher.getOwnerFromUrl(inputString)).orElse("Unknown"));

                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(SerializationFeature.INDENT_OUTPUT);

                if(Files.isDirectory(outputDir))
                    mapper.writeValue(outputDir.resolve("waver-config.json").toFile(), metadata);
                else
                    throw new TaskRunException("Output directory not found " + outputDir.toAbsolutePath());

                return generationContext;
            } catch (Exception e) {
                throw new TaskRunException("Failed to generate metadata", e);
            }
        });
    }
}
