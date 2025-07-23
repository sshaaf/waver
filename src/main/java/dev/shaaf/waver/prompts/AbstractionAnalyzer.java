package dev.shaaf.waver.prompts;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.V;
import dev.shaaf.waver.model.AbstractionList;


public interface AbstractionAnalyzer {
    @SystemMessage("""
        For the project `{projectName}`:
        Analyze the Java codebase given.
        You are a senior software architect. Analyze the provided codebase.
        Identify the core abstractions (main concepts, classes, or modules).
        For each abstraction, provide:
                1. A concise `name`.
                2. A beginner-friendly `description` explaining what it is with a simple analogy and its purpose, in around 300 words{desc_lang_hint}.
                3. A list of file paths where it is primarily defined or used.
        Respond with a JSON object containing a single key "abstractions".
        Make sure to add abstraction about building the application locally and then deploying to Red Hat OpenShift. The yaml snippets should reference the project. 
    """)
    @UserMessage("""
        Analyze the following files:
        ---
        {{codebase}}
    """)
    AbstractionList identifyAbstractions(@V("codebase") String codebase, @V("projectName") String projecName);
}