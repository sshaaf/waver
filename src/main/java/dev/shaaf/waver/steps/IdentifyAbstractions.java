package dev.shaaf.waver.steps;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.shaaf.waver.model.AbstractionList;
import dev.shaaf.waver.prompts.AbstractionAnalyzer;


public class IdentifyAbstractions {

    public static AbstractionList find(String codebase, ChatModel model, String projectName) {
        AbstractionAnalyzer analyzer = AiServices.create(AbstractionAnalyzer.class, model);
        return analyzer.identifyAbstractions(codebase, projectName);
    }

}

