package dev.shaaf.waver.tutorial.task;

import java.util.concurrent.CompletableFuture;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.shaaf.jgraphlet.PipelineContext;
import dev.shaaf.jgraphlet.Task;
import dev.shaaf.jgraphlet.TaskRunException;
import dev.shaaf.waver.tutorial.model.GenerationContext;
import dev.shaaf.waver.tutorial.prompt.AbstractionAnalyzer;



public class IdentifyAbstractionsTask implements Task<GenerationContext, GenerationContext> {

    private final ChatModel chatModel;
    private final String projectName;

    public IdentifyAbstractionsTask(ChatModel chatModel, String projectName) {
        this.chatModel = chatModel;
        this.projectName = projectName;
    }

    @Override
    public CompletableFuture<GenerationContext> execute(GenerationContext generationContext, PipelineContext context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                AbstractionAnalyzer analyzer = AiServices.create(AbstractionAnalyzer.class, chatModel);
                return generationContext.withAbstractions(analyzer.identifyAbstractions(generationContext.codeAsString(), projectName));
            } catch (Exception e) {
                throw new TaskRunException("Failed to identify abstractions", e);
            }
        });
    }

}

