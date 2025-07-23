package dev.shaaf.waver.steps;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.shaaf.waver.model.ChapterList;
import dev.shaaf.waver.prompts.ChapterOrderAnalyzer;




public class ChapterOrganizer {

    public static ChapterList find(String abstractionsAsString, ChatModel model){
        ChapterOrderAnalyzer analyzer = AiServices.create(ChapterOrderAnalyzer.class, model);
        return analyzer.determineChapterOrder(abstractionsAsString);
    }

}
