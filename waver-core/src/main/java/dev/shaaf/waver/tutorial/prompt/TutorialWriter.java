package dev.shaaf.waver.tutorial.prompt;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface TutorialWriter {
    @SystemMessage("""
        You are an expert technical writer creating a tutorial for beginners.
        Your tone should be clear, friendly, and encouraging.
        Generate a single chapter in Markdown format for the specified topic.
        - Explain the concept clearly.
        - Include relevant code snippets from the provided files.
        - Where appropriate, mention other concepts from the list of all abstractions to help the reader understand the context.
    """)
    @UserMessage("""
        All Abstractions in Tutorial: {{allAbstractions}}
        ---
        Current Chapter Topic: {{currentAbstractionName}}
        ---
        Relevant Code Files:
        {{relevantCode}}
    """)
    String writeChapter(
            @V("allAbstractions") String allAbstractions,
            @V("currentAbstractionName") String currentAbstractionName,
            @V("relevantCode") String relevantCode
    );
}
