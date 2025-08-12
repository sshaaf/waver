package dev.shaaf.waver.kantra.task;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.shaaf.waver.core.PipelineContext;
import dev.shaaf.waver.core.Task;
import dev.shaaf.waver.core.TaskRunException;
import dev.shaaf.waver.kantra.model.RuleGenContext;
import dev.shaaf.waver.kantra.prompt.RewriteConverter;


import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class RewriteConverterTask implements Task<RuleGenContext, RuleGenContext> {

    private ChatModel chatModel;
    private Path outputDir;

    public RewriteConverterTask(ChatModel chatModel, Path outputDir){
        this.chatModel = chatModel;
        this.outputDir = outputDir;
    }

    @Override
    public CompletableFuture<RuleGenContext> execute(RuleGenContext ruleGenContext, PipelineContext context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                RewriteConverter analyzer = AiServices.create(RewriteConverter.class, chatModel);
                return ruleGenContext.withKantraRules(analyzer.writeKantraRules(ruleGenContext.recipeAsString()));
            } catch (Exception e) {
                throw new TaskRunException("Failed to organize chapters", e);
            }
        });
    }
}
