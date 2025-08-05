package dev.shaaf.waver.tutorial.task;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.shaaf.waver.core.PipelineContext;
import dev.shaaf.waver.core.Task;
import dev.shaaf.waver.core.TaskRunException;
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
    public GenerationContext execute(GenerationContext generationContext, PipelineContext context) throws TaskRunException {
        AbstractionAnalyzer analyzer = AiServices.create(AbstractionAnalyzer.class, chatModel);
        return generationContext.withAbstractions(analyzer.identifyAbstractions(generationContext.codeAsString(), projectName));
    }

}

