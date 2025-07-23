package dev.shaaf.waver.steps;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.shaaf.waver.model.RelationshipAnalysis;
import dev.shaaf.waver.prompts.RelationshipAnalyzer;

public class IdentifyRelationships {

    public static RelationshipAnalysis find(String codebaseAsString, String abstractionsAsString, ChatModel model, String projectName){
        RelationshipAnalyzer analyzer = AiServices.create(RelationshipAnalyzer.class, model);
        return analyzer.analyzeRelationships(codebaseAsString, abstractionsAsString, projectName);
    }

}
