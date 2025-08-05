package dev.shaaf.waver.tutorial.prompt;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.shaaf.waver.tutorial.model.TutorialMetadata;


public interface MetaInfoAnalyzer {
    @SystemMessage("""
            You are an expert technical writer and software architect responsible for creating metadata for programming tutorials. Your task is to analyze the provided codebase abstractions and repository URL to generate a structured JSON object that describes the tutorial.

            Instructions:
            Based on the inputs in the user message, generate a single JSON object with the following fields, adhering strictly to these definitions:

            - "title": A concise, descriptive title for the tutorial.
            - "description": A paragraph summarizing the project's goal, the technologies used, and what a developer will learn.
            - "repo": The full repository URL provided as input.
            - "author": Use "Stefan Shaaf" as the author name.
            - "language": The primary programming language used in the codebase (e.g., "Java", "Python").
            - "tags": An array of 5-7 relevant lowercase string tags. Include the language, main frameworks, and key concepts.
            - "difficulty": Classify as one of: "beginner", "intermediate", or "hard".
                - "beginner": For basic syntax or single, isolated concepts.
                - "intermediate": For tutorials involving multiple frameworks or established architectural patterns.
                - "hard": For tutorials on advanced topics like complex architecture or performance optimization.
            - "estimatedTime": A rough estimate of the time required to complete the tutorial (e.g., "2 hours", "3 days").
            - "prerequisites": An array of strings listing the skills a developer should have before starting.
            - "lastUpdated": The current date and time in ISO 8601 format. Use "2025-08-04T21:51:21Z".

            The final output must be a single, valid JSON object only. Do not add any text or explanations before or after the JSON block.
            """)
    @UserMessage("""
            Please generate the JSON metadata by analyzing the following details:
            ProjectName: {{projectName}}
            
            **1. Codebase Abstractions:**
            {{abstractions}}

            **2. Codebase Repository URL:**
            {{codebase}}
            """)
    TutorialMetadata generateMetadata(
            @V("codebase") String codebase,
            @V("abstractions") String abstractions,
            @V("projectName") String projectName
    );

}
