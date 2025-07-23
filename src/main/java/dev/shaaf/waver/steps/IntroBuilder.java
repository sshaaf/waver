package dev.shaaf.waver.steps;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.shaaf.waver.model.AbstractionList;
import dev.shaaf.waver.prompts.IntroChapter;


import static dev.shaaf.waver.steps.TechnicalWriter.abstractionsAsString;

public class IntroBuilder {

    public static String write(String codeFiles, AbstractionList abstractionList, ChatModel model){
        IntroChapter introChapter = AiServices.create(IntroChapter.class, model);
        return introChapter.writeChapter(abstractionsAsString(abstractionList), codeFiles);
    }
}
