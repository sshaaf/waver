package dev.shaaf.waver.prompts;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface IntroChapter {

    @SystemMessage("""
        You are an expert technical writer creating a tutorial for beginners.
        Your tone should be clear, friendly, and encouraging.
        Generate a single introduction chapter in Markdown format for the specified topic.
        - Explain the project clearly and an easy to understand tone for any reader technical and non-technical
        - Explain core concepts and add a mermaid diagram to showcase them
        - Explain the technical stack used, the programming framework and few of the important depenencies.
        - If a database backend is used explain the model and why with a mermaid diagram as well. 
        - Explain the value proposition of Red Hat OpenShift and why its important for such applications
        - Where appropriate, mention other concepts from the list of all abstractions to help the reader understand the context.
    """)
    @UserMessage("""
        All Abstractions in Tutorial: {{allAbstractions}}
        ---
        Relevant Code Files:
        {{relevantCode}}
    """)
    String writeChapter(
            @V("allAbstractions") String allAbstractions,
            @V("relevantCode") String relevantCode
    );
}
