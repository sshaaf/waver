package dev.shaaf.waver.tutorial.task;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.shaaf.waver.core.PipelineContext;
import dev.shaaf.waver.core.Task;
import dev.shaaf.waver.core.TaskRunException;
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
    public GenerationContext execute(GenerationContext generationContext, PipelineContext context) throws TaskRunException {
        RelationshipAnalyzer analyzer = AiServices.create(RelationshipAnalyzer.class, model);

        return generationContext.withRelationshipAnalysis(
                analyzer.analyzeRelationships(
                        generationContext.codeAsString(),
                        generationContext.abstractionsAsString(),
                        projectName));

    }

}
