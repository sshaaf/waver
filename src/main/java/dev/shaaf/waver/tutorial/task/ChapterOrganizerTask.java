package dev.shaaf.waver.tutorial.task;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.shaaf.waver.core.PipelineContext;
import dev.shaaf.waver.core.Task;
import dev.shaaf.waver.core.TaskRunException;
import dev.shaaf.waver.tutorial.model.GenerationContext;
import dev.shaaf.waver.tutorial.prompt.ChapterOrderAnalyzer;

public class ChapterOrganizerTask implements Task<GenerationContext, GenerationContext> {

    private final ChatModel chatModel;

    public ChapterOrganizerTask(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public GenerationContext execute(GenerationContext generationContext, PipelineContext context) throws TaskRunException {
        ChapterOrderAnalyzer analyzer = AiServices.create(ChapterOrderAnalyzer.class, chatModel);
        return generationContext.withChapterList(analyzer.determineChapterOrder(generationContext.abstractionsAsString()));
    }
}
