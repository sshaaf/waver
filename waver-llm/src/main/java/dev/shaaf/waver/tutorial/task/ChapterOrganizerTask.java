package dev.shaaf.waver.tutorial.task;

import java.util.concurrent.CompletableFuture;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;

import dev.shaaf.jgraphlet.PipelineContext;
import dev.shaaf.jgraphlet.Task;
import dev.shaaf.jgraphlet.TaskRunException;
import dev.shaaf.waver.tutorial.model.GenerationContext;
import dev.shaaf.waver.tutorial.prompt.ChapterOrderAnalyzer;

public class ChapterOrganizerTask implements Task<GenerationContext, GenerationContext> {

    private final ChatModel chatModel;

    public ChapterOrganizerTask(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public CompletableFuture<GenerationContext> execute(GenerationContext generationContext, PipelineContext context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ChapterOrderAnalyzer analyzer = AiServices.create(ChapterOrderAnalyzer.class, chatModel);
                return generationContext.withChapterList(analyzer.determineChapterOrder(generationContext.abstractionsAsString()));
            } catch (Exception e) {
                e.printStackTrace();
                throw new TaskRunException("Failed to organize chapters", e);
            }
        });
    }
}
