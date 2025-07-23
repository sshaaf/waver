package dev.shaaf.waver.prompts;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.shaaf.waver.model.ChapterList;


public interface ChapterOrderAnalyzer {
    @SystemMessage("""
        You are a technical writer planning a tutorial. Given a list of topics (abstractions),
        determine the best order to teach them to a beginner.
        Start with foundational concepts and build up to more complex or specific ones.
        Respond with a JSON object containing a single key "chapters".
    """)
    @UserMessage("Abstractions: {{it}}")
    ChapterList determineChapterOrder(String abstractions);
}