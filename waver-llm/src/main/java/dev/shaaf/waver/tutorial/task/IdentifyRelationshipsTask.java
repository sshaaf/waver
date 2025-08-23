package dev.shaaf.waver.tutorial.task;

import java.util.concurrent.CompletableFuture;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.shaaf.jgraphlet.PipelineContext;
import dev.shaaf.jgraphlet.Task;
import dev.shaaf.jgraphlet.TaskRunException;
import dev.shaaf.waver.tutorial.model.GenerationContext;
import dev.shaaf.waver.tutorial.prompt.RelationshipAnalyzer;


public class IdentifyRelationshipsTask implements Task<GenerationContext, GenerationContext> {

    private final ChatModel model;
    private final String projectName;

    public IdentifyRelationshipsTask(ChatModel model, String projectName) {
        this.model = model;
        this.projectName = projectName;
    }

    @Override
    public CompletableFuture<GenerationContext> execute(GenerationContext generationContext, PipelineContext context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                RelationshipAnalyzer analyzer = AiServices.create(RelationshipAnalyzer.class, model);

                return generationContext.withRelationshipAnalysis(
                        analyzer.analyzeRelationships(
                                generationContext.codeAsString(),
                                generationContext.abstractionsAsString(),
                                projectName));
            } catch (Exception e) {
                throw new TaskRunException("Failed to identify relationships", e);
            }
        });
    }

}
